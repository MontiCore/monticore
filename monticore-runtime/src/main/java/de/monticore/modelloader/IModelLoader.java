/* (c) https://github.com/MontiCore/monticore */
package de.monticore.modelloader;

import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;

import java.util.List;

public interface IModelLoader<T extends ASTNode, S extends IScope> {

  List<T> loadModelsIntoScope(String qualifiedModelname, ModelPath modelPath, S enclosingScope);
}
