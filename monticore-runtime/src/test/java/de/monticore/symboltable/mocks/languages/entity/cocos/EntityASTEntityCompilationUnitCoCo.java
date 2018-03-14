/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.cocos;

import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;

public interface EntityASTEntityCompilationUnitCoCo extends ContextCondition  {
  public void check(ASTEntityCompilationUnit node);
}
