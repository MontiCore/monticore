/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating;

import de.monticore.generating.templateengine.MyTemplateControllerFactory;

/**
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MyGeneratorEngine extends GeneratorEngine {
  
  public MyGeneratorEngine(GeneratorSetup generatorSetup) {
    super(generatorSetup, new MyTemplateControllerFactory(), null);
  }

}
