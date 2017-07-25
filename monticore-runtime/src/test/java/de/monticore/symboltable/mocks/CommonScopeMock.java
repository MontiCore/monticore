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

package de.monticore.symboltable.mocks;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;

/**
 * Mock for {@link CommonScope} which enables to test the data of the
 * {@link ResolvingInfo} collected during the resolution process
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonScopeMock extends CommonScope {

  private ResolvingInfo resolvingInfo;

  public CommonScopeMock(boolean isShadowingScope) {
    super(isShadowingScope);
  }

  public CommonScopeMock(Optional<MutableScope> enclosingScope, boolean isShadowingScope) {
    super(enclosingScope, isShadowingScope);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name,
      SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    this.resolvingInfo = resolvingInfo;
    return super.resolveMany(resolvingInfo, name, kind, modifier, predicate);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
      Predicate<Symbol> predicate) {
    this.resolvingInfo = resolvingInfo;
    return super.resolveDownMany(resolvingInfo, name, kind, modifier, predicate);
  }

  public ResolvingInfo getResolvingInfo() {
    return resolvingInfo;
  }
}
