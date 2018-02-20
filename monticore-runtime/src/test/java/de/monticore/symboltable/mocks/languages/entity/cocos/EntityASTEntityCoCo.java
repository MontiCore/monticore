/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.cocos;

import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;

public interface EntityASTEntityCoCo extends ContextCondition {
  public void check(ASTEntity node);
}
