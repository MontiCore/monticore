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

package de.monticore.symboltable.mocks.languages.entity.cocos;

import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty;
import de.se_rwth.commons.logging.Log;

public class PropertyNameMustStartWithLowerCase implements EntityASTPropertyCoCo {
  
  /**
   * @see de.monticore.symboltable.mocks.languages.entity.cocos.EntityASTPropertyCoCo#check(de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty)
   */
  @Override
  public void check(ASTProperty node) {
    // TODO return check(node.getSymbol());
  }
  
  public void check(PropertySymbol propertySymbol) {
    Log.errorIfNull(propertySymbol);
    
    final boolean succeeded = Character.isLowerCase(propertySymbol.getName().charAt(0));
    if (!succeeded) {
      final String message = "Property names should start in lower case.";
      // TODO error code
      Log.error("TODO " + message, propertySymbol.getSourcePosition());
    }
  }
}
