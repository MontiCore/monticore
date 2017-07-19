/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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


package de.monticore.symboltable.names;

import java.util.List;
import java.util.Set;

import de.monticore.symboltable.ImportStatement;

/**
 * Calculates the possible qualified name for a simple name, considering the package
 * information and import statements.
 *
 * @author Pedram Mir Seyed Nazari
 */
public interface QualifiedNamesCalculator {

  /**
   * Calculates possible qualified names for the <code>simpleName</code>. For this,
   * it considers the (possible) <code>packageName</code> and the <code>imports</code>
   * (i.e., import statements).
   *
   * @param simpleName the simple name of the symbol
   * @param packageName the possible package name
   * @param imports the import statements
   * @return a set of possible qualified names for the <code>simpleName</code>
   */
  Set<String> calculateQualifiedNames(String simpleName, String packageName, List<ImportStatement> imports);

}
