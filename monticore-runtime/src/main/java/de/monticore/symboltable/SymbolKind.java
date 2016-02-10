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

import static java.util.Objects.requireNonNull;

/**
 * Represents a kind of a symbol. It is the super class of all symbol kinds and can be used in
 * {@link de.monticore.symboltable.Scope Scopes} and {@link de.monticore.symboltable.resolving.ResolvingFilter ResolvingFilters}
 * to resolve all symbol kinds matching the corresponding criteria.
 * <br /><br />
 * NOTE: This class intentionally is not abstract, because otherwise the above use case is not fulfilled.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public interface SymbolKind {

  SymbolKind KIND = new SymbolKind() {};

  default String getName() {
    return getClass().getSimpleName();
  }

  /**
   *Checks, whether this symbol kind is a kind of the given symbol kind. By default, this is true, if this symbol kind
   * is a sub-type of <code>kind</code>.
   *
   * @param kind
   * @return true, if this symbol kind is a kind of the given symbol kind.
   */
  default boolean isKindOf(SymbolKind kind) {
    requireNonNull(kind);

    return kind.equals(KIND) || kind.getClass().isAssignableFrom(this.getClass());
  }

  default boolean isSame(SymbolKind kind) {
    return (kind != null) && isKindOf(kind) && kind.isKindOf(this);
  }
}
