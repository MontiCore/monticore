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

package de.monticore.symboltable.references;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;

/**
 * Represents a symbol reference and is the super type of all symbol references. Do not
 * implement this interface directly. Instead, use one of its subtypes.
 *
 * @author Pedram Mir Seyed Nazari
 */
public interface SymbolReference<T extends Symbol> {

  /**
   * @return the reference name
   */
  String getName();

  /**
   * @return the corresponding ast node
   */
  Optional<ASTNode> getAstNode();

  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);

  /**
   * @return the referenced symbol
   * @throws FailedLoadingSymbol if the referenced symbol cannot be loaded
   */
  T getReferencedSymbol();

  /**
   * @return true, if the referenced symbol exists.
   */
  boolean existsReferencedSymbol();

  /**
   * @return true, if the referenced symbol is loaded
   */
  boolean isReferencedSymbolLoaded();

  /**
   * @return the enclosing scope of the reference itself
   */
  Scope getEnclosingScope();

}
