/* (c) https://github.com/MontiCore/monticore */

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
