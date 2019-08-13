/* (c) https://github.com/MontiCore/monticore */
package de.monticore.modelloader;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;

public interface IModelLoader<T extends ASTNode, S extends IScope> {
  
  public void createSymbolTableFromAST (T ast, String modelName, S enclosingScope);
}
