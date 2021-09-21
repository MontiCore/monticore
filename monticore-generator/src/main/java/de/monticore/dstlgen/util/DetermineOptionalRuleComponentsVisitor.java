/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.util;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarHandler;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;

/**
 * Checks if a rule component is optional,
 * as in the question OR star cardinality
 */
public class DetermineOptionalRuleComponentsVisitor implements
        GrammarHandler {
  
  protected boolean optional = false;

  protected GrammarTraverser traverser;
  
  public boolean isOptional(){
    return optional;
  }

  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  @Override
  public void handle(ASTTerminal astNode){
    optional = astNode.getIteration()==  ASTConstantsGrammar.QUESTION  || astNode.getIteration() ==  ASTConstantsGrammar.STAR;
  }

  @Override
  public void handle(ASTNonTerminal astNode){
    optional = astNode.getIteration()==  ASTConstantsGrammar.QUESTION  || astNode.getIteration() ==  ASTConstantsGrammar.STAR;
  }
  
  @Override
  public void handle(ASTBlock astNode){
    optional = astNode.getIteration()==  ASTConstantsGrammar.QUESTION  || astNode.getIteration() ==  ASTConstantsGrammar.STAR;
  }
  
  @Override
  public void handle(ASTConstantGroup astNode){
    optional = astNode.getIteration()==  ASTConstantsGrammar.QUESTION  || astNode.getIteration() ==  ASTConstantsGrammar.STAR;
  }

  // Override the traversal of all other RuleComponent s

  @Override
  public void traverse(ASTNonTerminalSeparator node) {

  }

  @Override
  public void traverse(ASTKeyTerminal node) {

  }

  @Override
  public void traverse(ASTTokenTerminal node) {

  }

  @Override
  public void traverse(ASTConstantGroup node) {

  }

  @Override
  public void traverse(ASTSemanticpredicateOrAction node) {

  }

  @Override
  public void traverse(ASTLexNonTerminal node) {

  }

  @Override
  public GrammarTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.traverser = traverser;
  }
}
