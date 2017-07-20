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

package de.monticore.symboltable.mocks.languages.entity.cocos;

import java.util.HashMap;

import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.se_rwth.commons.logging.Log;

public class UniquePropertyNamesInEntity implements EntityASTEntityCoCo {
  
  public static final String ERROR_CODE = "0xA2020";
  
  /**
   * @see de.monticore.symboltable.mocks.languages.entity.cocos.EntityASTEntityCoCo#check(de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity)
   */
  @Override
  public void check(ASTEntity node) {
    // TODO return check(node.getSymbol());
  }
  
  public void check(EntitySymbol entitySymbol) {
    Log.errorIfNull(entitySymbol);
    
    HashMap<String, PropertySymbol> duplicates = new HashMap<>();
    
    for (PropertySymbol field : entitySymbol.getProperties()) {
      // find duplicates
      entitySymbol.getProperties().stream()
          .filter(f -> f.getName().equals(field.getName()) && (f != field))
          .forEach(f2 -> duplicates.put(f2.getName(), f2));
    }
    
    if (!duplicates.isEmpty()) {
      for (PropertySymbol duplicate : duplicates.values()) {
        final String warnMsg = " Property " + duplicate.getName() +
            " is already defined";
        Log.warn(ERROR_CODE + warnMsg, duplicate.getSourcePosition());
      }
    }
  }
  
}
