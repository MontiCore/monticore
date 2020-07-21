/* (c) https://github.com/MontiCore/monticore */
package de.monticore.modelloader;

import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;

public interface IModelLoader<T extends ASTNode, S extends IScope> {

  @Deprecated
  public void createSymbolTableFromAST (T ast, String modelName, S enclosingScope);

  public boolean loadSymbolsIntoScope(String qualifiedModelName, ModelPath modelPath, S enclosingScope);
}
