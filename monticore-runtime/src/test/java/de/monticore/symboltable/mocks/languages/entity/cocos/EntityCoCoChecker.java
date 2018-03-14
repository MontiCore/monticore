/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.cocos;

import java.util.Collection;
import java.util.LinkedHashSet;

import de.monticore.symboltable.mocks.languages.entity.asts.ASTAction;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty;
import de.monticore.symboltable.mocks.languages.entity.asts.EntityLanguageVisitor;

public class EntityCoCoChecker implements EntityLanguageVisitor {
  
  /**
   * Generated cococheckers will have collections for each type of coco so that
   * no instanceof is needed in the visit-methods. However, for the mock its less
   * to implement doing it the "bad" way.
   */
  private Collection<ContextCondition> cocos = new LinkedHashSet<>();
  
  /**
   * @param cocos the cocos to set
   */
  public void addContextCondition(ContextCondition coco) {
    this.cocos.add(coco);
  }
  
  public void checkAll(ASTEntity root) {
    root.accept(this);
  }
  
  /**
   * @see de.monticore.symboltable.mocks.languages.entity.asts.EntityLanguageBaseVisitor#visit(de.monticore.symboltable.mocks.languages.entity.asts.ASTAction)
   */
  @Override
  public void visit(ASTAction node) {
    cocos.stream().filter(c -> c instanceof EntityASTActionCoCo).map(c
        -> (EntityASTActionCoCo) c)
        .forEach(c -> c.check(node));
  }
  
  /**
   * @see de.monticore.symboltable.mocks.languages.entity.asts.EntityLanguageBaseVisitor#visit(de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity)
   */
  @Override
  public void visit(ASTEntity node) {
    cocos.stream().filter(c -> c instanceof EntityASTEntityCoCo).map(c
        -> (EntityASTEntityCoCo) c)
        .forEach(c -> c.check(node));
  }
  
  /**
   * @see de.monticore.symboltable.mocks.languages.entity.asts.EntityLanguageBaseVisitor#visit(de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit)
   */
  @Override
  public void visit(ASTEntityCompilationUnit node) {
    cocos.stream().filter(c -> c instanceof EntityASTEntityCompilationUnitCoCo).map(c
        -> (EntityASTEntityCompilationUnitCoCo) c)
        .forEach(c -> c.check(node));
  }
  
  /**
   * @see de.monticore.symboltable.mocks.languages.entity.asts.EntityLanguageBaseVisitor#visit(de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty)
   */
  @Override
  public void visit(ASTProperty node) {
    cocos.stream().filter(c -> c instanceof EntityASTPropertyCoCo).map(c
        -> (EntityASTPropertyCoCo) c)
        .forEach(c -> c.check(node));
  }
  
  protected Collection<ContextCondition> getAll() {
    return cocos;
  }
}
