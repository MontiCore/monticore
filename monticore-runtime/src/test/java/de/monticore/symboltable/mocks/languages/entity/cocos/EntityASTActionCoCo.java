/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.cocos;

import de.monticore.symboltable.mocks.languages.entity.asts.ASTAction;

public interface EntityASTActionCoCo extends ContextCondition {
  public void check(ASTAction node);
}
