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

package de.monticore.symboltable.mocks.asts.grammar;

import de.monticore.symboltable.mocks.asts.ASTNodeMock;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class ASTSymbolProdReference extends ASTNodeMock {
 
  // TODO PN die Reference wird später über die Symtab aufgelöst.
  private ASTSymbolProd referencedProd;
  private String referenceName;
  
  private boolean include = true;
  
  public ASTSymbolProdReference(ASTSymbolProd referencedProd, String referenceName) {
    this.referencedProd = referencedProd;
    this.referenceName = referenceName;
  }
  
  public ASTSymbolProd getReferencedProd() {
    return this.referencedProd;
  }
  
  public String getReferenceName() {
    return this.referenceName;
  }
  
  public void setSubRule(boolean include) {
    this.include = include;
  }
  
  /**
   * 
   * @return true, if referenced production is a sub rule
   */
  public boolean isSubRule() {
    return include;
  }
  
  
  
}
