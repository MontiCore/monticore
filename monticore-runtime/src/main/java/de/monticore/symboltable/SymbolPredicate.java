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

import com.google.common.base.Predicate;

/**
 * @author Pedram Mir Seyed Nazari
 */
// TODO PN use fp of Java 1.8 instead
// Note by SO: Unless this interface is supposed to define additional methods, subclasses might as
// well just implement Predicate<Symbol> instead, making this interface redundant.
public interface SymbolPredicate extends Predicate<Symbol> {

  @Override
  boolean apply(Symbol symbol);


}
