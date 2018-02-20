/* (c) https://github.com/MontiCore/monticore */

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
