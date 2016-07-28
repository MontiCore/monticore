/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating;

import de.monticore.generating.templateengine.ExtendedTemplateControllerFactory;

/**
 * 
 * Sets the {@link ExtendedTemplateControllerFactory} in the GeneratorEngine.
 * 
 * @author Jerome Pfeiffer
 */
public class ExtendedGeneratorEngine extends GeneratorEngine {
  
  public ExtendedGeneratorEngine(GeneratorSetup generatorSetup) {
    super(generatorSetup, new ExtendedTemplateControllerFactory(), null);
  }

}
