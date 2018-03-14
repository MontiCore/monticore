/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart.asts;

public interface StateChartLanguageBaseVisitor {

  public default void visit(ASTStateChartCompilationUnit node) {}
  public default void endVisit(ASTStateChartCompilationUnit node) {}

  public default void traverse(ASTStateChartCompilationUnit node) {
    visit(node);
    node.getStateChart().accept(this);
    endVisit(node);
  }

  public default void visit(ASTStateChart node) {}
  public default void endVisit(ASTStateChart node) {}

  public default void traverse(ASTStateChart node) {
    visit(node);

    node.get_Children().stream()
        .filter(child -> child instanceof ASTState)
        .forEach(child -> ((ASTState) child).accept(this));

    endVisit(node);
  }


  public default void visit(ASTState node) {}
  public default void endVisit(ASTState node) {}

  public default void traverse(ASTState node) {
    visit(node);
    // no children to traverse
    endVisit(node);

  }
}
