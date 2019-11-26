/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.utils.ASTNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.mc2cd.TransformationHelper.getName;
import static de.monticore.codegen.mc2cd.TransformationHelper.getUsageName;
import static de.monticore.utils.ASTNodes.getIntermediates;
import static java.util.Collections.max;

/**
 * Denotes the multiplicity of nonterminals in a MC grammar such as '*', '+', or '?'.
 */
public enum Multiplicity {

  /**
   * No quantifier present
   */
  STANDARD,
  /**
   * '?' quantifier present
   */
  OPTIONAL,
  /**
   * '*' or '+' quantifier present
   */
  LIST;

  public static Multiplicity determineMultiplicity(ASTNode rootNode, ASTNode astNode) {
    if (astNode instanceof ASTAdditionalAttribute) {
      return multiplicityOfAttributeInAST((ASTAdditionalAttribute) astNode);
    }
    return multiplicityOfASTNodeWithInheritance(rootNode, astNode);
  }

  public static Multiplicity multiplicityOfAttributeInAST(ASTAdditionalAttribute attributeInAST) {
    if (!attributeInAST.isPresentCard()) {
      return STANDARD;
    }
    ASTCard cardinality = attributeInAST.getCard();
    if (cardinality.getIteration() == ASTConstantsGrammar.STAR
        || cardinality.getIteration() == ASTConstantsGrammar.PLUS
        || (cardinality.isPresentMax() && (cardinality.getMax().equals("*") || getMaxCardinality(cardinality) > 1))) {
      return LIST;
    } else if (cardinality.getIteration() == ASTConstantsGrammar.QUESTION
        || (!cardinality.isPresentMin() || getMinCardinality(cardinality) == 0)) {
      return OPTIONAL;
    }
    return STANDARD;
  }

  private static int getMaxCardinality(ASTCard cardinality) {
    return Integer.parseInt(cardinality.getMax());
  }

  private static int getMinCardinality(ASTCard cardinality) {
    return Integer.parseInt(cardinality.getMin());
  }

  /**
   * Performs the multiplicity calculation for inherited attributes.
   *
   * @param rootNode The grammar symbol of the ast node.
   * @param astNode  The ast node.
   * @return The multiplicity of the ast in the defining grammar.
   */
  private static Multiplicity multiplicityOfASTNodeWithInheritance(ASTNode rootNode, ASTNode astNode) {
    // multiplicity by inheritance is only relevant for nonterminals and can
    // cause errors otherwise; cast rootNode to ASTMCGrammar for further use
    // switch to default behavior without inheritance otherwise
    if (!(rootNode instanceof ASTMCGrammar) || !(astNode instanceof ASTNonTerminal)) {
      return multiplicityOfASTNode(rootNode, astNode);
    }
    ASTMCGrammar grammar = (ASTMCGrammar) rootNode;

    // check if own grammar is the defining grammar
    IScopeSpanningSymbol definingGrammarSymbol = ((ASTNonTerminal) astNode).getEnclosingScope().getEnclosingScope().getSpanningSymbol();

    String definingGrammarName = definingGrammarSymbol.getName();
    String definingGrammarFullName = definingGrammarSymbol.getFullName();
    if (grammar.getName().equals(definingGrammarName)) {
      return multiplicityOfASTNode(rootNode, astNode);
    }

    // resolve defining grammar or switch to default behavior without inheritance
    Optional<MCGrammarSymbol> grammarSymbol = ((ASTMCGrammar) rootNode).getEnclosingScope().resolveMCGrammar(definingGrammarFullName);
    if (!grammarSymbol.isPresent() || !grammarSymbol.get().isPresentAstNode()) {
      return multiplicityOfASTNode(rootNode, astNode);
    }
    ASTNode definingGrammar = grammarSymbol.get().getAstNode();

    // perform multiplicity computation with defining grammar
    return multiplicityOfASTNode(definingGrammar, astNode);
  }

  private static Multiplicity multiplicityOfASTNode(ASTNode rootNode, ASTNode astNode) {
    Multiplicity byAlternative = multiplicityByAlternative(rootNode, astNode);
    Multiplicity byDuplicates = multiplicityByDuplicates(rootNode, astNode);
    Multiplicity byIteration = multiplicityByIteration(rootNode, astNode);
    ArrayList<Multiplicity> newArrayList = newArrayList(byDuplicates, byIteration, byAlternative);
    return max(newArrayList);
  }

  public static Multiplicity multiplicityByAlternative(ASTNode rootNode, ASTNode astNode) {
    List<ASTNode> intermediates = getIntermediates(rootNode, astNode);
    boolean containedInAlternative = false;
    for (ASTNode intermediate : intermediates) {
      if (intermediate instanceof ASTClassProd) {
        containedInAlternative |= ((ASTClassProd) intermediate).getAltList().size() > 1;
      } else if (intermediate instanceof ASTBlock) {
        containedInAlternative |= ((ASTBlock) intermediate).getAltList().size() > 1;
      }
    }
    return containedInAlternative ? OPTIONAL : STANDARD;
  }

  public static Multiplicity multiplicityByDuplicates(ASTNode rootNode, ASTNode astNode) {
    List<ASTAlt> allAlt = getAllAlts(rootNode, astNode);
    boolean hasDuplicate = hasDuplicateInAtLeastOneAlt(allAlt, rootNode, astNode);
    if (hasDuplicate) {
      return LIST;
    } else {
      return STANDARD;
    }
  }


  private static List<ASTAlt> getAllAlts(ASTNode rootNode, ASTNode astNode) {
    List<ASTAlt> ruleComp = getIntermediates(rootNode, astNode).stream()
        .filter(ASTClassProd.class::isInstance)
        .map(ASTClassProd.class::cast)
        .map(ASTClassProd::getAltList)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    ruleComp.addAll(getIntermediates(rootNode, astNode).stream()
        .filter(ASTInterfaceProd.class::isInstance)
        .map(ASTInterfaceProd.class::cast)
        .map(ASTInterfaceProd::getAltList)
        .flatMap(List::stream)
        .collect(Collectors.toList()));


    ruleComp.addAll(getIntermediates(rootNode, astNode).stream()
        .filter(ASTAbstractProd.class::isInstance)
        .map(ASTAbstractProd.class::cast)
        .map(ASTAbstractProd::getAltList)
        .flatMap(List::stream)
        .collect(Collectors.toList()));

    return ruleComp;
  }

  private static boolean areDuplicates(ASTNode rootNode, ASTNode firstNode, ASTNode secondNode) {
    Optional<String> firstName = getName(firstNode);
    Optional<String> firstUsageName = getUsageName(rootNode, firstNode);
    Optional<String> secondName = getName(secondNode);
    Optional<String> secondUsageName = getUsageName(rootNode, secondNode);

    boolean bothUsageNamesAbsent = !firstUsageName.isPresent() && !secondUsageName.isPresent();
    boolean namesMatch = firstName.equals(secondName);
    boolean usageNamesMatch = firstUsageName.equals(secondUsageName);
    return (bothUsageNamesAbsent && namesMatch) || (!bothUsageNamesAbsent && usageNamesMatch);
  }

  /**
   * for each alt in that the prods that contain the astNode, check if per alt there is more than one element of that ruleComponent
   */
  private static boolean hasDuplicateInAtLeastOneAlt(List<ASTAlt> astAlts,
                                                     ASTNode rootNode, ASTNode astNode) {
    for (ASTAlt astAlt : astAlts) {
      int ruleCompCount = 0;
      for (ASTRuleComponent astRuleComponent : astAlt.getComponentList()) {
        if (areDuplicates(rootNode, astNode, astRuleComponent)) {
          ruleCompCount++;
        }
        if (ruleCompCount > 1) {
          return true;
        }
      }
    }
    return false;
  }

  public static Multiplicity multiplicityByIteration(ASTNode rootNode, ASTNode astNode) {
    Multiplicity multiplicity = STANDARD;
    for (ASTNode intermediate : ASTNodes.getIntermediates(rootNode, astNode)) {
      int iteration = getIterationInt(intermediate);

      if (iteration == ASTConstantsGrammar.PLUS || iteration == ASTConstantsGrammar.STAR) {
        multiplicity = LIST;
      }
      if (iteration == ASTConstantsGrammar.QUESTION && multiplicity != LIST) {
        multiplicity = OPTIONAL;
      }
    }
    return multiplicity;
  }

  private static int getIterationInt(ASTNode ancestor) {
    int iteration = ASTConstantsGrammar.DEFAULT;
    if (ancestor instanceof ASTBlock) {
      iteration = ((ASTBlock) ancestor).getIteration();
    }
    if (ancestor instanceof ASTNonTerminal) {
      iteration = ((ASTNonTerminal) ancestor).getIteration();
    }
    if (ancestor instanceof ASTTerminal) {
      iteration = ((ASTTerminal) ancestor).getIteration();
    }
    if (ancestor instanceof ASTKeyTerminal) {
      iteration = ((ASTKeyTerminal) ancestor).getIteration();
    }
    if (ancestor instanceof ASTConstantGroup) {
      iteration = ((ASTConstantGroup) ancestor).getIteration();
    }
    return iteration;
  }

}
