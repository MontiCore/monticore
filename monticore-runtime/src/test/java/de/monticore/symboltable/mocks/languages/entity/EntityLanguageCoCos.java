/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.mocks.languages.entity.cocos.EntityCoCoChecker;
import de.monticore.symboltable.mocks.languages.entity.cocos.PropertyNameMustStartWithLowerCase;
import de.monticore.symboltable.mocks.languages.entity.cocos.UniquePropertyNamesInEntity;

/**
 * Holds the CoCos of the Entity language.
 *
 * @author Robert Heim
 */
public class EntityLanguageCoCos {
  public EntityCoCoChecker getAllCoCosChecker() {
    EntityCoCoChecker checker = new EntityCoCoChecker();
    checker.addContextCondition(new UniquePropertyNamesInEntity());
    checker.addContextCondition(new PropertyNameMustStartWithLowerCase());
    
    return checker;
  }
}
