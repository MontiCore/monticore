/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.nio.file.Path;
import java.util.Arrays;

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class MyGeneratorEngine extends GeneratorEngine{
  
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyGeneratorEngine
   */
  public MyGeneratorEngine(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }
  
  public String generateToString(String templateName, ASTNode node,
      Object... templateArguments) {
    MyTemplateController tc = (MyTemplateController) templateControllerFactory.create(templateControllerConfig, "");
    return tc.processTemplate(templateName, node, Arrays.asList(templateArguments));
  }
  
}
