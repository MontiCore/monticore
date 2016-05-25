/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating;

import java.nio.file.Path;
import java.util.Arrays;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.MyTemplateController;
import de.monticore.generating.templateengine.MyTemplateControllerFactory;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MyGeneratorEngine extends GeneratorEngine {
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyGeneratorEngine
   */
  public MyGeneratorEngine(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }
  
  public String generateToString(String templateName, ASTNode node,
      Object... templateArguments) {
    MyTemplateController tc = new MyTemplateControllerFactory().create(templateControllerConfig,
        templateName);
    return tc.processTemplate(templateName, node, Arrays.asList(templateArguments));
  }
  
  /**
   * @see de.monticore.generating.GeneratorEngine#generate(java.lang.String,
   * java.nio.file.Path, de.monticore.ast.ASTNode, java.lang.Object[])
   */
  @Override
  public void generate(String templateName, Path filePath, ASTNode node,
      Object... templateArguments) {
    MyTemplateController tc = new MyTemplateControllerFactory().create(templateControllerConfig,
        templateName);
    tc.setTemplateControllerFactory(new MyTemplateControllerFactory());
    tc.writeArgs(templateName, filePath, node, Arrays.asList(templateArguments));
  }
}
