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
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.ITemplateControllerFactory;
import de.monticore.generating.templateengine.MyFreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.MyTemplateController;
import de.monticore.generating.templateengine.MyTemplateControllerConstants;
import de.monticore.generating.templateengine.MyTemplateControllerFactory;
import de.monticore.generating.templateengine.TemplateControllerConfiguration;
import de.monticore.generating.templateengine.TemplateControllerConfigurationBuilder;
import de.monticore.generating.templateengine.TemplateControllerFactory;
import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.io.FileReaderWriter;
import de.monticore.templateclassgenerator.ITemplates;
import freemarker.template.Configuration;

/**
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MyGeneratorEngine extends GeneratorEngine {
  
  private TemplateControllerConfiguration myTemplateControllerConfig;
  
  private Optional<ITemplates> templates = Optional.empty();
  
  private GeneratorSetup setup;
  
//  /**
//   * @param templates the templates to set
//   */
//  public void setTemplates(Optional<ITemplates> templates) {
//    if (templates.isPresent()) {
//      myTemplateControllerConfig.getGlEx().defineGlobalValue(
//          MyTemplateControllerConstants.TEMPLATES, templates.get());
//      this.templates = templates;
//    }
//  }
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyGeneratorEngine
   */
  public MyGeneratorEngine(GeneratorSetup generatorSetup, Optional<ITemplates> templates) {
    super(generatorSetup);
    this.setup = generatorSetup;
    this.templates = templates;
    myTemplateControllerConfig = createTemplateControllerConfiguration(generatorSetup, null, null);
//    setTemplates(templates);
  }
  
  public MyGeneratorEngine(GeneratorSetup generatorSetup) {
    super(generatorSetup);
    this.setup = generatorSetup;
    myTemplateControllerConfig = createTemplateControllerConfiguration(generatorSetup, null, null);
  }
  
  public GeneratorSetup getSetup() {
    return this.setup;
  }
  
  public String generateToString(String templateName,
      Object... templateArguments) {
    MyTemplateController tc = new MyTemplateControllerFactory().create(myTemplateControllerConfig,
        templateName);
    
    tc.setTemplateControllerFactory(new MyTemplateControllerFactory());
    return tc.includeArgs(templateName, Arrays.asList(templateArguments));
  }
  
  /**
   * @see de.monticore.generating.GeneratorEngine#generate(java.lang.String,
   * java.nio.file.Path, de.monticore.ast.ASTNode, java.lang.Object[])
   */
  @Override
  public void generate(String templateName, Path filePath, ASTNode node,
      Object... templateArguments) {
    MyTemplateController tc = new MyTemplateControllerFactory().create(myTemplateControllerConfig,
        templateName);
    tc.setTemplateControllerFactory(new MyTemplateControllerFactory());
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
//    File f = new File(System.getProperty("user.dir"));
//    addPaths.add(f);
    
    Configuration freemarkerConfig = new FreeMarkerConfigurationBuilder()
        .classLoader(generatorSetup.getClassLoader())
        .additionalTemplatePaths(addPaths)
        .build();
    
    
    if (null != templates && templates.isPresent()) {
      Map<String,String> map = new HashMap<>();
      map.put("_templates","Setup.ftl");
      freemarkerConfig.setAutoImports(map);
    }
    
    GlobalExtensionManagement glex = generatorSetup.getGlex().orElse(
        new GlobalExtensionManagement());
    
    MyFreeMarkerTemplateEngine freeMarkerTemplateEngine = new MyFreeMarkerTemplateEngine(
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
