/* (c) https://github.com/MontiCore/monticore */
package de.monticore.visitor;


import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;

public interface IVisitor {

  default void endVisit(ASTNode node) {
  }

  default void visit(ASTNode node) {
  }

  default void visit(ISymbol symbol) {
  }

  default void endVisit(ISymbol symbol) {
  }

  default void visit(IScope scope) {
  }

  default void endVisit(IScope scope) {
  }

  default void visit(IScopeSpanningSymbol symbol) {
  }

  default void endVisit(IScopeSpanningSymbol symbol) {
  }
}
