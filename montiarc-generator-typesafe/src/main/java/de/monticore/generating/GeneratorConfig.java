/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating;

import java.io.File;

import de.se_rwth.commons.Files;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class GeneratorConfig {
  
  private static MyGeneratorEngine generator;
  
  private static final String DEFAULT_OUTPUT_FOLDER = "/target/generated-sources/templateClasses";
  
  public static MyGeneratorEngine getGeneratorEngine() {
    if (null == generator) {
      String workingDir = System.getProperty("user.dir");
      GeneratorSetup setup = new GeneratorSetup(new File(workingDir+DEFAULT_OUTPUT_FOLDER));
      
      GeneratorConfig.generator = new MyGeneratorEngine(setup);
    }
    return generator;
  }
  
  public static void setGeneratorEngine(MyGeneratorEngine generator) {
    GeneratorConfig.generator = generator;
  }
  
}
