/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.cocos;

import de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty;

public interface EntityASTPropertyCoCo extends ContextCondition {
  public void check(ASTProperty node);
}
