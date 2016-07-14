/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating;

import java.io.File;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.ITemplateControllerFactory;
import de.monticore.generating.templateengine.MyTemplateControllerFactory;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateControllerConfiguration;
import de.monticore.generating.templateengine.TemplateControllerConfigurationBuilder;
import de.monticore.generating.templateengine.TemplateControllerFactory;
import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriter;
import freemarker.template.Configuration;

/**
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MyGeneratorEngine extends GeneratorEngine {
  
  public MyGeneratorEngine(GeneratorSetup generatorSetup) {
    super(generatorSetup, new MyTemplateControllerFactory(), null);
  }
  
  public String generateToString(String templateName,
      Object... templateArguments) {
    TemplateController tc = this.templateControllerFactory.create(this.templateControllerConfig,
        templateName);
    return tc.includeArgs(templateName, Arrays.asList(templateArguments));
  }
  
  /**
   * @see de.monticore.generating.GeneratorEngine#generate(java.lang.String,
   * java.nio.file.Path, de.monticore.ast.ASTNode, java.lang.Object[])
   */
  @Override
  public void generate(String templateName, Path filePath, ASTNode node,
      Object... templateArguments) {
    TemplateController tc = this.templateControllerFactory.create(this.templateControllerConfig,
        templateName);
    tc.writeArgs(templateName, filePath, node, Arrays.asList(templateArguments));
  }
  
  /**
   * @see de.monticore.generating.GeneratorEngine#createTemplateControllerConfiguration(de.monticore.generating.GeneratorSetup,
   * de.monticore.generating.templateengine.ITemplateControllerFactory,
   * de.monticore.io.FileReaderWriter)
   */
  @Override
  TemplateControllerConfiguration createTemplateControllerConfiguration(
      GeneratorSetup generatorSetup, ITemplateControllerFactory templateControllerFactory,
      FileReaderWriter fileHandler) {
    if (templateControllerFactory == null) {
      templateControllerFactory = TemplateControllerFactory.getInstance();
    }
    
    if (fileHandler == null) {
      fileHandler = new FileReaderWriter();
    }
    
    List<File> addPaths = new ArrayList<>(generatorSetup.getAdditionalTemplatePaths());
    
    Configuration freemarkerConfig = new FreeMarkerConfigurationBuilder()
        .classLoader(generatorSetup.getClassLoader())
        .additionalTemplatePaths(addPaths)
        .build();
    
//    if (false) {
//      Map<String, String> map = new HashMap<>();
//      map.put("_templates", "Setup.ftl");
//      freemarkerConfig.setAutoImports(map);
//    }
    
    GlobalExtensionManagement glex = generatorSetup.getGlex().orElse(
        new GlobalExtensionManagement());
    
    FreeMarkerTemplateEngine freeMarkerTemplateEngine = new FreeMarkerTemplateEngine(
        freemarkerConfig);
    
    TemplateControllerConfiguration tcConfig = new TemplateControllerConfigurationBuilder()
        .glex(glex)
        .templateControllerFactory(templateControllerFactory)
        .classLoader(generatorSetup.getClassLoader())
        .fileHandler(fileHandler)
        .targetDir(generatorSetup.getOutputDirectory())
        .freeMarkerTemplateEngine(freeMarkerTemplateEngine)
        .tracing(generatorSetup.isTracing())
        .commentStart(
            generatorSetup.getCommentStart().orElse(
                TemplateControllerConfigurationBuilder.DEFAULT_COMMENT_START))
        .commentEnd(
            generatorSetup.getCommentEnd().orElse(
                TemplateControllerConfigurationBuilder.DEFAULT_COMMENT_END))
        .build();
    
    return tcConfig;
  }
}
