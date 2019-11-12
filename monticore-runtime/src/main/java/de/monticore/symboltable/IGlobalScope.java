/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable;

/**
 * Common interface for all global scopes
 */
public interface IGlobalScope {

  /**
   * Method returning the model path of this global scope
   * @return
   */
   public  de.monticore.io.paths.ModelPath getModelPath () ;
}
