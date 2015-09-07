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

package de.monticore.symboltable.types;

import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;

/**
 * @author Pedram Mir Seyed Nazari
 */
public interface JTypeSymbol extends TypeSymbol, ScopeSpanningSymbol {

  JTypeSymbolKind KIND = new JTypeSymbolKind();

  boolean isGeneric();

  List<? extends JTypeSymbol> getFormalTypeParameters();

  Optional<? extends JTypeReference<? extends JTypeSymbol>> getSuperClass();

  List<? extends JTypeReference<? extends JTypeSymbol>> getInterfaces();

  List<? extends JTypeReference<? extends JTypeSymbol>> getSuperTypes();

  List<? extends JAttributeSymbol> getFields();

  Optional<? extends JAttributeSymbol> getField(String attributeName);

  List<? extends JMethodSymbol> getMethods();

  Optional<? extends JMethodSymbol> getMethod(String methodName);

  List<? extends JMethodSymbol> getConstructors();


  boolean isAbstract();

  boolean isFinal();

  boolean isInterface();

  boolean isEnum();

  boolean isClass();

  boolean isPrivate();

  boolean isProtected();

  boolean isPublic();

  /**
   * @return true, if this type itself is a formal type parameter.
   */
  boolean isFormalTypeParameter();

}
