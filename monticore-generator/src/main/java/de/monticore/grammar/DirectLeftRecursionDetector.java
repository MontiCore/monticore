/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;

import java.util.Collection;
import java.util.List;

/**
 * Checks if a MC production is a left directly left recursive: e.g. of the form A -> A.*
 *
 */
public class DirectLeftRecursionDetector {
  
  public boolean isAlternativeLeftRecursive(final ASTAlt productionAlternative,
      final ASTNonTerminal actualNonTerminal) {
    final String classProductionName = actualNonTerminal.getName();

    final List<ASTRuleComponent> nodes = TransformationHelper.getAllComponents(productionAlternative);
    
    if (nodes.isEmpty()) {
      return false;
    }
    
    if (nodes.get(0) instanceof ASTNonTerminal) {
      ASTNonTerminal leftmostNonterminal = (ASTNonTerminal) nodes.get(0);
      if ((leftmostNonterminal == actualNonTerminal)
          && leftmostNonterminal.getName().equals(classProductionName)) {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean isAlternativeLeftRecursive(final ASTAlt productionAlternative,
      final String classProductionName) {
    final List<ASTRuleComponent> nodes = TransformationHelper.getAllComponents(productionAlternative);
    
    if (nodes.isEmpty()) {
      return false;
    }
    
    if (nodes.get(0) instanceof ASTNonTerminal) {
      ASTNonTerminal leftmostNonterminal = (ASTNonTerminal) nodes.get(0);
      if (leftmostNonterminal.getName().equals(classProductionName)) {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean isAlternativeLeftRecursive(final ASTAlt productionAlternative,
      final Collection<String> names) {
    final List<ASTRuleComponent> nodes = TransformationHelper.getAllComponents(productionAlternative);
    
    if (nodes.isEmpty()) {
      return false;
    }
    
    if (nodes.get(0) instanceof ASTNonTerminal) {
      ASTNonTerminal leftmostNonterminal = (ASTNonTerminal) nodes.get(0);
      if (names.contains(leftmostNonterminal.getName())) {
        return true;
      }
    }
    
    return false;
  }
  
}
