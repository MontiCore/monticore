/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.asts;

import de.monticore.ast.ASTNode;

public interface EntityLanguageVisitor {

  public default void visit(ASTEntityCompilationUnit node) {}
  public default void endVisit(ASTEntityCompilationUnit node) {}

  public default void traverse(ASTEntityCompilationUnit node) {
    visit(node);

    node.get_Children().stream()
        .filter(child -> child instanceof ASTEntity)
        .forEach(child -> ((ASTEntity) child).accept(this));

    endVisit(node);
  }


  public default void visit(ASTEntity node) {}
  public default void endVisit(ASTEntity node) {}

  public default void traverse(ASTEntity node) {
    visit(node);

    for (ASTNode child : node.get_Children()) {
      if (child instanceof ASTAction) {
        ((ASTAction) child).accept(this);
      }
      else if (child instanceof ASTProperty) {
        ((ASTProperty) child).accept(this);
      }
    }

    endVisit(node);
  }

  public default void visit(ASTAction node) {}
  public default void endVisit(ASTAction node) {}

  public default void traverse(ASTAction node) {
    visit(node);

    node.get_Children().stream()
        .filter(child -> child instanceof ASTProperty)
        .forEach(child -> ((ASTProperty) child).accept(this));

    endVisit(node);
  }


  public default void visit(ASTProperty node) {}
  public default void endVisit(ASTProperty node) {}
  public default void traverse(ASTProperty node) {
    visit(node);
    // no children to traverse.
    endVisit(node);
  }

}
