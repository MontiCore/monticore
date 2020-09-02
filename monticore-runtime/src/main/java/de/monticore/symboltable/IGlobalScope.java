/* (c) https://github.com/MontiCore/monticore */
/*
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable;

import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;

/**
 * Common interface for all global scopes
 */
public interface IGlobalScope {

  /**
   * Method returning the model path of this global scope
   * @return
   */
   public ModelPath getModelPath () ;

  default ModelCoordinate getModelCoordinate(String modelName, String symbolFileExtension, ModelPath modelPath){
    //1. Calculate qualified path of of stored artifact scopes relative to model path entries
    String simpleName = de.monticore.utils.Names.getSimpleName(modelName);
    String packagePath = de.monticore.utils.Names.getPathFromQualifiedName(modelName);
    java.nio.file.Path qualifiedPath = java.nio.file.Paths.get(packagePath, simpleName + "." + symbolFileExtension);
    //2. try to find qualified path within model path entries
    de.monticore.io.paths.ModelCoordinate modelCoordinate = de.monticore.io.paths.ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
    modelPath.resolveModel(modelCoordinate);
    return modelCoordinate;
  }
}
