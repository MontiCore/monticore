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

package de.monticore.symboltable.mocks.asts.grammar;

import de.monticore.ast.ASTNode;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class ASTSymbolProd extends ASTProd {

  private boolean definesNamespace;
  
  /**
   * @return true, if a child ast exists that is a symbol (definition) production.
   */
  public boolean spansScope() {
    return containsSymbolProductionReference(this);
  }

  private boolean containsSymbolProductionReference(ASTNode node) {
    for(ASTNode child : node.get_Children()) {
      if (((child instanceof ASTSymbolProdReference) && ((ASTSymbolProdReference)child).isSubRule()) 
          || containsSymbolProductionReference(child)) {
        // at least one child found
        return true;
      }
    }
    
    return false;
  }
  
  public boolean definesNamespace() {
    return definesNamespace;
  }
  
  public void setDefinesNamespace(boolean definesNamespace) {
    // TODO PN wird beim Parsen schon erkannt, dass es ein Namespace definiert oder wird es eher
    //         dynamisch berechnet (beim Generieren der Symtab), z.B. hasNameProduction()
    this.definesNamespace = definesNamespace;
  }
}
