/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.ArrayList;
import java.util.Optional;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.symboltable.mocks.asts.ASTSymbolReference;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTAction;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityBase;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty;
import de.monticore.symboltable.mocks.languages.entity.asts.EntityLanguageVisitor;
import de.se_rwth.commons.logging.Log;

public interface EntityLanguageSymbolTableCreator extends EntityLanguageVisitor, SymbolTableCreator {

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and returns the first scope
   * that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  default Scope createFromAST(ASTEntityBase rootNode) {
    Log.errorIfNull(rootNode);
    rootNode.accept(this);
    return getFirstCreatedScope();
  }

  @Override
  default void visit(ASTEntityCompilationUnit node) {
    ArtifactScope scope = new ArtifactScope(Optional.empty(), nullToEmpty(node.getPackageName()), new ArrayList<>());
    putOnStack(scope);
  }

  @Override
  default void endVisit(ASTEntityCompilationUnit node) {
    removeCurrentScope();
  }

  @Override
  default void visit(ASTEntity node) {
    EntitySymbol entity = new EntitySymbol(node.getName());
    addToScope(entity);
    putSpannedScopeOnStack(entity);
  }

  @Override
  default void endVisit(ASTEntity node) {
    removeCurrentScope();
  }

  @Override
  default void visit(ASTAction astAction) {
    ActionSymbol method = new ActionSymbol(astAction.getName());
    addToScope(method);
    putSpannedScopeOnStack(method);
  }

  @Override
  default void endVisit(ASTAction node) {
    removeCurrentScope();
  }

  @Override
  default void visit(ASTProperty node) {

    ASTSymbolReference astReference = node.getReference();

    PropertySymbol variable = new PropertySymbol(node.getName(), null/*typeReference*/);
    addToScope(variable);
  }
}
