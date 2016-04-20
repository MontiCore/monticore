/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

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
  
  Optional<MyTemplateController> tc = Optional.empty();
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyGeneratorEngine
   */
  public MyGeneratorEngine(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }
  
  /**
   * 
   * @param templateName
   * @param node
   * @param templateArguments
   * @return
   */
  public String generateToString(String templateName, ASTNode node,
      Object... templateArguments) {
    MyTemplateController tc = (MyTemplateController)templateControllerFactory.create(templateControllerConfig, "");
    return tc.processTemplate(templateName, node, Arrays.asList(templateArguments));
  }
  
  
  public void signature(String ... params){
    if(tc.isPresent()){
      tc.get().signature(params);
    }
  }
  
  public void createTemplateController(String templateName){
    tc = Optional.of((MyTemplateController)templateControllerFactory.create(templateControllerConfig, templateName));
  }
  
  /**
   * @see de.monticore.generating.GeneratorEngine#generate(java.lang.String, java.nio.file.Path, de.monticore.ast.ASTNode, java.lang.Object[])
   */
  @Override
  public void generate(String templateName, Path filePath, ASTNode node,
      Object... templateArguments) {
    if(tc.isPresent()){
      tc.get().writeArgs(templateName, filePath, node, Arrays.asList(templateArguments));
    }else{
      try {
        throw new InstantiationException("TemplateController is not initialized! Please call createTemplateController first");
      }
      catch (InstantiationException e) {
        e.printStackTrace();
      }
    }
  }
}
