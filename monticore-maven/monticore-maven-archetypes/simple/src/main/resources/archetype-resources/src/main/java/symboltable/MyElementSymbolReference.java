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

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;

public class MyElementSymbolReference extends MyElementSymbol implements SymbolReference<MyElementSymbol> {
  
  private final SymbolReference<MyElementSymbol> reference;
  
  public MyElementSymbolReference(final String name, final Scope definingScopeOfReference) {
    super(name);
    
    reference = new CommonSymbolReference<>(name, MyElementSymbol.KIND, definingScopeOfReference);
  }
  
  @Override
  public MyElementSymbol getReferencedSymbol() {
    return reference.getReferencedSymbol();
  }
  
  @Override
  public boolean existsReferencedSymbol() {
    return reference.existsReferencedSymbol();
  }
  
  @Override
  public String getName() {
    return getReferencedSymbol().getName();
  }
  
  @Override
  public boolean isReferencedSymbolLoaded() {
    return reference.isReferencedSymbolLoaded();
  }
  
}
