/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable;

import java.util.Collection;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;

/**
 * @author Pedram Mir Seyed Nazari
 */
public interface MutableScope extends Scope {
  // TODO PN DOC

  <T extends Symbol> Optional<T> resolve(ResolvingInfo resolvingInfo, String name,
      SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier);

  /**
   * @param enclosingScope the enclosing scope. In Java, for example, a class scope is the
   *                       enclosing scope of method scopes.
   */
  void setEnclosingScope(MutableScope enclosingScope);

  /**
   * Adds a sub subScope. In Java, for example, sub scopes of a class are the method scopes.
   * @param subScope the sub scope to be added.
   */
  void addSubScope(MutableScope subScope);

  /**
   * Removes the sub scope <code>subScope</code>.
   * @param subScope the sub scope to be removed
   *
   */
  void removeSubScope(MutableScope subScope);

  /**
   * @param symbol the symbol that spans this scope. For example, a Java method spans a
   * method scope.
   */
  void setSpanningSymbol(ScopeSpanningSymbol symbol);

  /**
   * @deprecated use {@link #add(Symbol)} instead
   */
  @Deprecated
  void define(Symbol symbol);

  /**
   * Adds the symbol to this scope. Also, this scope is set as the symbol's enclosing scope.
   */
  void add(Symbol symbol);

  /**
   * Sets the resolvers that are available in this scope. Within a simple grammarlanguage, these
   * resolvers are usually the same for all scopes of the grammarlanguage. The composing languages this
   * may vary.
   *
   * @param resolvingFilters the resolvers available in this scope.
   */
  void setResolvingFilters(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters);

  /**
   * Adds a <code>resolver</code> to this scope.
   *
   * @param resolvingFilter the resolvers available in this scope.
   */
  void addResolver(ResolvingFilter<? extends Symbol> resolvingFilter);


  // TODO PN method needed?
  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);

  /**
   * @param name of the scope
   */
  void setName(String name);

  <T extends Symbol> Collection<T> continueAsSubScope(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier);
}
