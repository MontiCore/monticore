/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

package de.monticore.symboltable.types;

import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

import java.util.List;

/**
 * @author Pedram Mir Seyed Nazari
 */
public interface JMethodSymbol extends ScopeSpanningSymbol {

  JMethodSymbolKind KIND = new JMethodSymbolKind();

  JTypeReference<? extends JTypeSymbol> getReturnType();

  List<? extends JFieldSymbol> getParameters();

  List<? extends JTypeSymbol> getFormalTypeParameters();

  List<? extends JTypeReference<? extends JTypeSymbol>> getExceptions();

  boolean isAbstract();

  boolean isStatic();

  boolean isConstructor();

  boolean isFinal();

  boolean isEllipsisParameterMethod();

  boolean isPrivate();

  boolean isProtected();

  boolean isPublic();

}
