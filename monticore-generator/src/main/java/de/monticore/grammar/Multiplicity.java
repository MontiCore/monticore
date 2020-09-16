/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.IGrammarScope;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.symboltable.IGlobalScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.mc2cd.TransformationHelper.getName;
import static de.monticore.codegen.mc2cd.TransformationHelper.getUsageName;
import static java.util.Collections.max;

/**
 * Denotes the multiplicity of nonterminals in a MC grammar such as '*', '+', or '?'.
 *
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

  public static Multiplicity determineMultiplicity(ASTAdditionalAttribute attributeInAST) {
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
   * @param astNode The ast node.
   * @return The multiplicity of the ast in the defining grammar.
   */
  public static Multiplicity determineMultiplicity(ASTRuleComponent astNode) {
    // multiplicity by inheritance is only relevant for nonterminals and can
    // cause errors otherwise; cast rootNode to ASTMCGrammar for further use
    // switch to default behavior without inheritance otherwise
    if (astNode instanceof ASTConstantGroup) {
      // constant groups are always standard iteration
      return STANDARD;
    }
    IGrammarScope scope = astNode.getEnclosingScope();
    while (!(scope instanceof IGlobalScope) ) {
      if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof MCGrammarSymbol) {
        return  determineMultiplicity(((MCGrammarSymbol) scope.getSpanningSymbol()).getAstNode(), astNode);
      }
      scope = scope.getEnclosingScope();
    }
    return Multiplicity.STANDARD;

  }

  public static Multiplicity determineMultiplicity(ASTNode astNode) {
    if (astNode instanceof ASTRuleComponent) {
      return determineMultiplicity((ASTRuleComponent) astNode);
    } else if (astNode instanceof ASTAdditionalAttribute) {
      return determineMultiplicity((ASTAdditionalAttribute) astNode);
    }
    return null;
  }

  public static Multiplicity determineMultiplicity(ASTMCGrammar rootNode, ASTRuleComponent astNode) {
    Multiplicity byAlternative = multiplicityByAlternative(rootNode, astNode);
    Multiplicity byDuplicates = multiplicityByDuplicates(rootNode, astNode);
    Multiplicity byIteration = multiplicityByIteration(rootNode, astNode);
    ArrayList<Multiplicity> newArrayList = newArrayList(byDuplicates, byIteration, byAlternative);
    return max(newArrayList);
  }

  private static Multiplicity multiplicityByAlternative(ASTMCGrammar rootNode, ASTRuleComponent astNode) {
    List<ASTNode> intermediates = new MultiplicityVisitor().getComponents(rootNode, astNode);
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

  private static Multiplicity multiplicityByDuplicates(ASTMCGrammar rootNode, ASTRuleComponent astNode) {
    boolean hasDuplicate = getAllNodesInRelatedRuleComponents(rootNode, astNode)
        .anyMatch(sibling -> areDuplicates(rootNode, astNode, sibling));
    if (hasDuplicate) {
      return LIST;
    } else {
      return STANDARD;
    }
  }

  private static boolean areDuplicates(ASTMCGrammar rootNode, ASTRuleComponent firstNode, ASTRuleComponent secondNode) {
    Optional<String> firstName = getName(firstNode);
    Optional<String> firstUsageName = getUsageName(rootNode, firstNode);
    Optional<String> secondName = getName(secondNode);
    Optional<String> secondUsageName = getUsageName(rootNode, secondNode);

    boolean bothUsageNamesAbsent = !firstUsageName.isPresent() && !secondUsageName.isPresent();
    boolean namesMatch = firstName.equals(secondName);
    boolean usageNamesMatch = firstUsageName.equals(secondUsageName);
    return (bothUsageNamesAbsent && namesMatch) || (!bothUsageNamesAbsent && usageNamesMatch);
  }

  private static Stream<ASTRuleComponent> getAllNodesInRelatedRuleComponents(ASTMCGrammar rootNode,
                                                                    ASTRuleComponent astNode) {
    Set<ASTRuleComponent> ancestorRuleComponents = new MultiplicityVisitor().getComponents(rootNode, astNode).stream()
        .filter(ASTRuleComponent.class::isInstance)
        .map(ASTRuleComponent.class::cast)
        .collect(Collectors.toSet());

    return new MultiplicityVisitor().getComponents(rootNode, astNode).stream()
        .filter(ASTAlt.class::isInstance)
        .map(ASTAlt.class::cast)
        .flatMap(alt -> alt.getComponentList().stream())
        .filter(ruleComponent -> !ancestorRuleComponents.contains(ruleComponent))
        .flatMap(ruleComponent -> TransformationHelper.getAllComponents(ruleComponent).stream());
  }

  public static Multiplicity multiplicityByIteration(ASTMCGrammar rootNode, ASTRuleComponent astNode) {
    Multiplicity multiplicity = STANDARD;
    for (ASTNode intermediate : new MultiplicityVisitor().getComponents(rootNode, astNode)) {
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
    if (ancestor instanceof ASTTokenTerminal) {
      iteration = ((ASTTokenTerminal) ancestor).getIteration();
    }
    if (ancestor instanceof ASTConstantGroup) {
      iteration = ((ASTConstantGroup) ancestor).getIteration();
    }
    return iteration;
  }

}
