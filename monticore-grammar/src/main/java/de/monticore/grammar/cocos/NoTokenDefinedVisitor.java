/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarHandler;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;

public class NoTokenDefinedVisitor implements GrammarVisitor2, GrammarHandler {

  protected boolean terminalFound = false;

  GrammarTraverser traverser;

  @Override
  public GrammarTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void visit(ASTTerminal node) {
    terminalFound = true;
  }

  @Override
  public void visit(ASTConstant node) {
    terminalFound = true;
  }

  @Override
  public void visit(ASTLexProd node) {
    terminalFound = true;
  }

  @Override
  public void handle(ASTAbstractProd node){
    //do not navigate here
    //because tokes in abstract prods still lead to the compile error
    //due to the problem, that nor lexer is generated
  }

  @Override
  public void handle(ASTNonTerminalSeparator node) {
    terminalFound = true;
  }

  public boolean foundTerminal() {
    return terminalFound;
  }
}
