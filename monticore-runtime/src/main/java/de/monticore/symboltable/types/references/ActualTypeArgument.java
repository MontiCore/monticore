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

package de.monticore.symboltable.types.references;

import de.monticore.symboltable.types.TypeSymbol;

/**
 * Represents an actual type argument as in Java.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ActualTypeArgument {

  private final boolean isLowerBound;
  private final boolean isUpperBound;

  // TODO PN Handle the case List<? extends Bla & Blub>: This is just ONE type argument with several upper bounds
  private final TypeReference<? extends TypeSymbol> type;

  // TODO PN make generic, <T extends TypeSymbol>
  public ActualTypeArgument(boolean isLowerBound, boolean isUpperBound, TypeReference<? extends TypeSymbol> type) {
    this.isLowerBound = isLowerBound;
    this.isUpperBound = isUpperBound;
    this.type = type;
  }

  public ActualTypeArgument(TypeReference<? extends TypeSymbol> type) {
    this(false, false, type);
  }

  public boolean isLowerBound() {
    return isLowerBound;
  }

  public boolean isUpperBound() {
    return isUpperBound;
  }

  public TypeReference<? extends TypeSymbol> getType() {
    return type;
  }
}
