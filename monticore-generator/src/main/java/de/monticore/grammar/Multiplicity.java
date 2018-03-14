/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.ASTNodes;
import de.monticore.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.mc2cd.TransformationHelper.getName;
import static de.monticore.codegen.mc2cd.TransformationHelper.getUsageName;
import static de.monticore.utils.ASTNodes.getIntermediates;
import static de.monticore.utils.ASTNodes.getSuccessors;
import static java.util.Collections.max;

/**
 * Denotes the multiplicity of nonterminals in a MC grammar such as '*', '+', or '?'.
 * 
 * @author Sebastian Oberhoff
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
    if (astNode instanceof ASTAttributeInAST) {
      return multiplicityOfAttributeInAST((ASTAttributeInAST) astNode);
    }
    return multiplicityOfASTNode(rootNode, astNode);
  }
  
  public static Multiplicity multiplicityOfAttributeInAST(ASTAttributeInAST attributeInAST) {
    if (!attributeInAST.isPresentCard()) {
      return STANDARD;
    }
    ASTCard cardinality = attributeInAST.getCard();
    if (!cardinality.isPresentMax() || cardinality.isUnbounded()
        || "*".equals(cardinality.getMax())
        || getMaxCardinality(cardinality) != 1) {
      return LIST;
    }
    else {
      if (!cardinality.isPresentMin() || getMinCardinality(cardinality)==0)  {
        return OPTIONAL;
      }
    }
    return STANDARD;
  }
  
  private static int getMaxCardinality(ASTCard cardinality) {
    return Integer.parseInt(cardinality.getMax());
  }
  
  private static int getMinCardinality(ASTCard cardinality) {
    return Integer.parseInt(cardinality.getMin());
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
    for (ASTNode intermediate: intermediates) {
      if (intermediate instanceof ASTClassProd) {
        containedInAlternative |= ((ASTClassProd) intermediate).getAltList().size()>1;
      } else if (intermediate instanceof ASTBlock) {
        containedInAlternative |= ((ASTBlock) intermediate).getAltList().size()>1;
      }
    }
    return containedInAlternative ? OPTIONAL : STANDARD;
  }
  
  public static Multiplicity multiplicityByDuplicates(ASTNode rootNode, ASTNode astNode) {
    boolean hasDuplicate = getAllNodesInRelatedRuleComponents(rootNode, astNode)
        .anyMatch(sibling -> areDuplicates(rootNode, astNode, sibling));
    if (hasDuplicate) {
      return LIST;
    }
    else {
      return STANDARD;
    }
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
  
  private static Stream<ASTNode> getAllNodesInRelatedRuleComponents(ASTNode rootNode,
      ASTNode astNode) {
    Set<ASTRuleComponent> ancestorRuleComponents = getIntermediates(rootNode, astNode).stream()
        .filter(ASTRuleComponent.class::isInstance)
        .map(ASTRuleComponent.class::cast)
        .collect(Collectors.toSet());
    
    return getIntermediates(rootNode, astNode).stream()
        .filter(ASTAlt.class::isInstance)
        .map(ASTAlt.class::cast)
        .flatMap(alt -> alt.getComponentList().stream())
        .filter(ruleComponent -> !ancestorRuleComponents.contains(ruleComponent))
        .flatMap(ruleComponent -> getSuccessors(ruleComponent, ASTNode.class).stream());
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
    return iteration;
  }
  
}
