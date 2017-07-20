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

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;

public class MyFieldSymbol extends CommonSymbol {
  
  public static final MyFieldKind KIND = new MyFieldKind();
  
  private final MyElementSymbol type;
  
  public MyFieldSymbol(final String name, final MyElementSymbol type) {
    super(name, KIND);
    this.type = type;
  }
  
  public MyElementSymbol getType() {
    return type;
  }
  
  static final class MyFieldKind implements SymbolKind {
    MyFieldKind() {
    }
  }
}
