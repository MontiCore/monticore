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

package ${package}.symboltable;

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;

public class MyModelSymbol extends CommonScopeSpanningSymbol {
  
  public static final MyModelKind KIND = new MyModelKind();
  
  public MyModelSymbol(final String name) {
    super(name, KIND);
  }
  
  public Optional<MyElementSymbol> getMyElement(final String name) {
    return getSpannedScope().resolveLocally(name, MyElementSymbol.KIND);
  }
  
  public Collection<MyElementSymbol> getMyElements() {
    return getSpannedScope().resolveLocally(MyElementSymbol.KIND);
  }
  
  static final class MyModelKind implements SymbolKind {
    MyModelKind() {
    }
  }
}
