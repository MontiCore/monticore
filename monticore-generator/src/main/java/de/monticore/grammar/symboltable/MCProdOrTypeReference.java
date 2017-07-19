/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.grammar.symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 *
 * The <code>astextends</code> constructs allows both extending another rule
 * or an external type (see {@link JTypeSymbol]). This class helps resolve the respective type.
 *
 * @author  Pedram Mir Seyed Nazari
 */
public class MCProdOrTypeReference {

  private final MCProdSymbolReference prodRef;
  
  private final JTypeReference<JTypeSymbol> typeRef;

  public MCProdOrTypeReference(String referencedSymbolName, Scope enclosingScopeOfReference) {
    prodRef = new MCProdSymbolReference(referencedSymbolName, enclosingScopeOfReference);
    typeRef = new CommonJTypeReference<>(referencedSymbolName, JTypeSymbol.KIND, enclosingScopeOfReference);
  }

  public MCProdSymbolReference getProdRef() {
    return prodRef;
  }

  public JTypeReference<JTypeSymbol> getTypeRef() {
    return typeRef;
  }

  public boolean isProdRef() {
    return prodRef.existsReferencedSymbol();
  }

  public boolean isTypeRef() {
    return typeRef.existsReferencedSymbol();
  }
}
