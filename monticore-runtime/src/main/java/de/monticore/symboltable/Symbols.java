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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class Symbols {

  private Symbols() {
  }

  public static <T extends Symbol> List<T> sortSymbolsByPosition(final Collection<T> unorderedSymbols) {
    final List<T> sortedSymbols = new ArrayList<>(unorderedSymbols);

    Collections.sort(sortedSymbols,
        (symbol1, symbol2) -> symbol1.getSourcePosition().compareTo(symbol2.getSourcePosition()));

    return ImmutableList.copyOf(sortedSymbols);
  }

}
