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

package de.monticore.symboltable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ImportStatement {

  private final String statement;
  private final boolean isStar;

  public ImportStatement(String statement, boolean isStar) {
    checkArgument(!isNullOrEmpty(statement), "An import statement must not be null or empty");
    checkArgument(!statement.endsWith("."), "An import statement must not end with a dot.");

    this.statement = statement;
    this.isStar = isStar;
  }

  /**
   * @return statement
   */
  public String getStatement() {
    return this.statement;
  }

  /**
   * @return isStar
   */
  public boolean isStar() {
    return this.isStar;
  }

}
