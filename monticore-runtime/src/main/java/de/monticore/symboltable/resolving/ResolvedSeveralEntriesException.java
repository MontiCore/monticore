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

package de.monticore.symboltable.resolving;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.Symbol;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvedSeveralEntriesException extends RuntimeException {

  private static final long serialVersionUID = 931330102959575779L;

  private Collection<? extends Symbol> symbols = new ArrayList<>();

  public ResolvedSeveralEntriesException (String message, Collection<? extends Symbol> symbols) {
    super(message);
    this.symbols = new ArrayList<>(symbols);
  }

  public ResolvedSeveralEntriesException(Collection<? extends Symbol> symbols) {
    this("", symbols);
  }

  public Collection<? extends Symbol> getSymbols() {
    return ImmutableList.copyOf(symbols);
  }

}
