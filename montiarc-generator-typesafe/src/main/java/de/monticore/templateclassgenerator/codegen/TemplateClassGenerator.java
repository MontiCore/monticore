/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.templateclassgenerator.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.MyGeneratorEngine;
import de.monticore.templateclassgenerator.EmptyNode;
import de.se_rwth.commons.Names;
import freemarker.cache.FileTemplateLoader;
import freemarker.core.FMHelper;
import freemarker.core.Parameter;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * This class generates a template class for each template.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class TemplateClassGenerator {
  
  private static final String PARAM_METHOD_1 = "tc.params";
  
  private static final String PARAM_METHOD_2 = "params";
  
  private static final String RESULT_METHOD_1 = "tc.result";
  
  private static final String RESULT_METHOD_2 = "result";
  
  /**
   * Generates the template fqnTemplateName from the modelPath to the
   * targetFilePath with the targetName
   * 
   * @param targetName
   * @param modelPath
   * @param fqnTemplateName
   * @param targetFilepath
   */
  public static void generateClassForTemplate(String targetName, Path modelPath,
      String fqnTemplateName, File targetFilepath) {
    List<Parameter> params = new ArrayList<>();
    Optional<String> result = Optional.empty();
    Configuration config = new Configuration();
    Template t = null;
    try {
      config.setTemplateLoader(new FileTemplateLoader(modelPath.toFile()));
      t = config.getTemplate(fqnTemplateName);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    Map<String, List<List<String>>> methodCalls = FMHelper.getMethodCalls(t);
    if (methodCalls.containsKey(PARAM_METHOD_1)) {
      // we just recognize the first entry as there
      // must not be multiple params definitions
      params = FMHelper.getParams(methodCalls.get(PARAM_METHOD_1).get(0));
    }
    else if (methodCalls.containsKey(PARAM_METHOD_2)) {
      params = FMHelper.getParams(methodCalls.get(PARAM_METHOD_2).get(0));
    }
    
    if (methodCalls.containsKey(RESULT_METHOD_1)) {
      // A template can only have one result type.
      String dirtyResult = methodCalls.get(RESULT_METHOD_1).get(0).get(0);
      String cleanResult = dirtyResult.replace("\"", "");
      result = Optional.of(cleanResult);
    }
    else if (methodCalls.containsKey(RESULT_METHOD_2)) {
      // A template can only have one result type.
      String dirtyResult = methodCalls.get(RESULT_METHOD_2).get(0).get(0);
      String cleanResult = dirtyResult.replace("\"", "");
      result = Optional.of(cleanResult);
    }
    doGenerate(targetFilepath, fqnTemplateName, targetName, params, result);
  }
  
  /**
   * Does the generation with the parameters of the signature method
   * tc.params(...) and tc.signature(...).
   * 
   * @param targetFilepath
   * @param fqnTemplateName
   * @param targetName
   * @param params
   * @param result
   */
  private static void doGenerate(File targetFilepath, String fqnTemplateName, String targetName,
      List<Parameter> params, Optional<String> result) {
    final GeneratorSetup setup = new GeneratorSetup(targetFilepath);
    TemplateClassHelper helper = new TemplateClassHelper();
    final MyGeneratorEngine generator = new MyGeneratorEngine(setup);
    ASTNode node = new EmptyNode();
    String packageNameWithSeperators = "templates" + File.separator
        + Names.getPathFromFilename(fqnTemplateName);
    String packageNameWithDots = Names.getPackageFromPath(packageNameWithSeperators);
    generator.generate("typesafety.TemplateClass",
        Paths.get(packageNameWithSeperators, targetName + ".java"), node,
        packageNameWithDots, fqnTemplateName, targetName,
        params, result, helper);
  }
  

  /**
   * TODO: Write me!
   * @param foundTemplates
   * @param targetFilepath
   * @param modelPath
   */
  public static void generateTemplateSetup(File targetFilepath, File modelPath) {
    String packageName = "setup";
    final GeneratorSetup setup = new GeneratorSetup(targetFilepath);
    TemplateClassHelper helper = new TemplateClassHelper();
    final MyGeneratorEngine generator = new MyGeneratorEngine(setup);
    String filePath = Names.getPathFromPackage(packageName) + File.separator + "Templates.java";
    String mp = modelPath.getPath();
    List<File> nodes = TemplateClassHelper.walkTree(modelPath);
    generator.generate("typesafety.Templates", Paths.get(filePath), new EmptyNode(),
        packageName, nodes, mp, new TemplateClassHelper());
  }
  
  public static void generateGeneratorConfig(File targetFilepath) {
    String packageName = "setup";
    final GeneratorSetup setup = new GeneratorSetup(targetFilepath);
    TemplateClassHelper helper = new TemplateClassHelper();
    final MyGeneratorEngine generator = new MyGeneratorEngine(setup);
    String filePath = Names.getPathFromPackage(packageName) + File.separator + "GeneratorConfig.java";
    generator.generate("typesafety.GeneratorConfig", Paths.get(filePath), new EmptyNode(),
        packageName);
  }
  
}
