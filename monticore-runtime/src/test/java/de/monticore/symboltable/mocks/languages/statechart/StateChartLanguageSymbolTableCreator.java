/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.ArrayList;
import java.util.Optional;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTState;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChart;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChartBase;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChartCompilationUnit;
import de.monticore.symboltable.mocks.languages.statechart.asts.StateChartLanguageBaseVisitor;
import de.se_rwth.commons.logging.Log;

public interface StateChartLanguageSymbolTableCreator extends
    StateChartLanguageBaseVisitor, SymbolTableCreator {

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and returns the first scope
   * that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  default Scope createFromAST(ASTStateChartBase rootNode) {
    Log.errorIfNull(rootNode);
    rootNode.accept(this);
    return getFirstCreatedScope();
  }

  
  @Override
  default void visit(ASTStateChartCompilationUnit node) {
    ArtifactScope scope = new ArtifactScope(Optional.empty(), nullToEmpty(node.getPackageName()), new ArrayList<>());
    putOnStack(scope);
  }

  @Override
  default void endVisit(ASTStateChartCompilationUnit node) {
    removeCurrentScope();
  }

  default void visit(ASTStateChart node) {
    StateChartSymbol sc = new StateChartSymbol(node.getName());
    addToScope(sc);
    putSpannedScopeOnStack(sc);
  }

  @Override
  default void endVisit(ASTStateChart node) {
    removeCurrentScope();
  }

  default void visit(ASTState node) {
    StateSymbol state = new StateSymbol(node.getName());
    addToScope(state);
  }

}
