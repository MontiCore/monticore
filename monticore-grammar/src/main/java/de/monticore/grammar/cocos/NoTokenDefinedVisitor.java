/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;

public class NoTokenDefinedVisitor implements Grammar_WithConceptsVisitor {

  private boolean terminalFound = false;

  @Override
  public void handle(ASTTerminal node) {
    terminalFound = true;
  }

  @Override
  public void handle(ASTConstant node) {
    terminalFound = true;
  }

  @Override
  public void handle(ASTLexProd node) {
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
