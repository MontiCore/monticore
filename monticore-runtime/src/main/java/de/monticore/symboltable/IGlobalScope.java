/* (c) https://github.com/MontiCore/monticore */
/*
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable;

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
}
