/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.utils.ASTNodes;

/**
 * Checks if a MC production is a left directly left recursive: e.g. of the form A -> A.*
 *
 */
public class DirectLeftRecursionDetector {
  
  public boolean isAlternativeLeftRecursive(final ASTAlt productionAlternative,
      final ASTNonTerminal actualNonTerminal) {
    final String classProductionName = actualNonTerminal.getName();
    Collection<Class<? extends ASTNode>> types = new HashSet<>(
        Arrays.asList(ASTNonTerminal.class, ASTTerminal.class, ASTConstant.class));
    final List<ASTNode> nodes = ASTNodes.getSuccessors(productionAlternative, types);
    
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
    Collection<Class<? extends ASTNode>> types = new HashSet<>(
        Arrays.asList(ASTNonTerminal.class, ASTTerminal.class, ASTConstant.class));
    final List<ASTNode> nodes = ASTNodes.getSuccessors(productionAlternative, types);
    
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
    Collection<Class<? extends ASTNode>> types = new HashSet<>(
        Arrays.asList(ASTNonTerminal.class, ASTTerminal.class, ASTConstant.class));
    final List<ASTNode> nodes = ASTNodes.getSuccessors(productionAlternative, types);
    
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
