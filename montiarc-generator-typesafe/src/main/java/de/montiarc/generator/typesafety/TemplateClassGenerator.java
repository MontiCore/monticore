/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.typesafety;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.montiarc.generator.codegen.TemplateClassHelper;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.MyGeneratorEngine;
import de.se_rwth.commons.Names;
import freemarker.cache.FileTemplateLoader;
import freemarker.core.FMHelper;
import freemarker.core.Parameter;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class TemplateClassGenerator {
  
  private static final String PARAM_METHOD = "tc.params";
  
  private static final String RESULT_METHOD = "tc.result";
  
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
    if (methodCalls.containsKey(PARAM_METHOD)) {
      // we just recognize the first entry as there
      // must not be multiple params definitions
      params = FMHelper.getParams(methodCalls.get(PARAM_METHOD).get(0));
    }
    if (methodCalls.containsKey(RESULT_METHOD)) {
      // A template can only have one result type.
      String dirtyResult = methodCalls.get(RESULT_METHOD).get(0).get(0);
      String cleanResult = dirtyResult.replace("\"", "");
      result = Optional.of(cleanResult);
    }
    
    doGenerate(targetFilepath, fqnTemplateName, targetName, params, result);
  }
  
  private static void doGenerate(File targetFilepath, String fqnTemplateName, String targetName,
      List<Parameter> params, Optional<String> result) {
    final GeneratorSetup setup = new GeneratorSetup(targetFilepath);
    TemplateClassHelper helper = new TemplateClassHelper();
    final MyGeneratorEngine generator = new MyGeneratorEngine(setup);
    ASTNode node = new EmptyNode();
    String packageNameWithSeperators = Names.getPathFromFilename(fqnTemplateName);
    String packageNameWithDots = "";
    if (packageNameWithSeperators.length() > 1) {
      packageNameWithDots = Names.getPackageFromPath(packageNameWithSeperators);
    }
    generator.generate("templates.typesafety.TemplateClass",
        Paths.get(packageNameWithSeperators, targetName + ".java"), node,
        packageNameWithDots, fqnTemplateName, targetName,
        params, result, helper);
  }
  
}
