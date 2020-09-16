/* (c) https://github.com/MontiCore/monticore */
package de.monticore.modelloader;

import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import java.util.List;

public interface IModelLoader<T extends ASTNode, S extends IScope> {

  List<T> loadModelsIntoScope(String qualifiedModelname, ModelPath modelPath, S enclosingScope);
}
