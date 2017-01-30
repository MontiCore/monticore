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

package de.monticore;

import java.util.Set;

import de.monticore.symboltable.SymbolKind;

/**
 * Calculates the possible model names from a (un-)qualified symbol <code>name</code> depending on the
 * <code>kind</code> (see {@link #calculateModelNames(String, SymbolKind)}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public interface ModelNameCalculator {


  /**
   * Calculates the possible model names from a (un-)qualified symbol <code>name</code> depending on its
   * <code>kind</code>. This is required to resolve inner model elements, since their containing
   * model needs to be loaded first. For example, a class diagram symbol is the top-level symbol
   * of a class diagram. To resolve a type <code>T</code> defined in a class diagram a.b.CD, first
   * <code>a.b.CD.MyClass</code> in the path <code>a/b/CD/MyClass.cd</code> would fail, since
   * <code>MyClass</code> is not a top level element and consequently no file <code>MyClass.cd</code>
   * exists. The name of the model (here class diagram) is <code>a.b.CD</code>, leading
   * to the correct model path <code>a/b/CD.cd</code>.
   *
   * @param name the (qualified) name
   * @param kind the symbol kind
   * @return model name depending on the
   */
  Set<String> calculateModelNames(String name, SymbolKind kind);

}
