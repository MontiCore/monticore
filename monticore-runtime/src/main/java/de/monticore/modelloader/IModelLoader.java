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

public interface IModelLoader<T extends ASTNode, S extends IScope> {

  List<T> loadModelsIntoScope(String qualifiedModelName, ModelPath modelPath, S enclosingScope);

  default ModelCoordinate resolveFile (String qualifiedModelName, String modelFileExtension, ModelPath modelPath)  {
    String fileName = Names.getSimpleName(qualifiedModelName) + "." + modelFileExtension;
    Path qualifiedPath = Paths.get(Names.getPathFromQualifiedName(qualifiedModelName)).resolve(fileName);
    ModelCoordinate qualifiedModel = ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
    return modelPath.resolveModel(qualifiedModel);
  }
}
