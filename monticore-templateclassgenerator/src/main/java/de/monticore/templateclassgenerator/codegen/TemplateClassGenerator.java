/* (c) https://github.com/MontiCore/monticore */
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
import de.monticore.generating.ExtendedGeneratorSetup;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
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
 * @author Jerome Pfeiffer
 */
public class TemplateClassGenerator {
  
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
      String fqnTemplateName,
      File targetFilepath) {
    List<Parameter> params = new ArrayList<>();
    Optional<String> result = Optional.empty();
    Configuration config = new Configuration();
    Template t = null;
    boolean hasSignature = false;
    try {
      config.setTemplateLoader(new FileTemplateLoader(modelPath.toFile()));
      t = config.getTemplate(fqnTemplateName);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    Map<String, List<List<String>>> methodCalls = FMHelper.getMethodCalls(t);
    if (methodCalls.containsKey(TemplateClassGeneratorConstants.PARAM_METHOD)) {
      // we just recognize the first entry as there
      // must not be multiple params definitions
      hasSignature = true;
      params = FMHelper
          .getParams(methodCalls.get(TemplateClassGeneratorConstants.PARAM_METHOD).get(0));
    }
    
    if (methodCalls.containsKey(TemplateClassGeneratorConstants.RESULT_METHOD)) {
      // A template can only have one result type.
      String dirtyResult = methodCalls.get(TemplateClassGeneratorConstants.RESULT_METHOD).get(0)
          .get(0);
      String cleanResult = dirtyResult.replace("\"", "");
      result = Optional.of(cleanResult);
    }
    
    doGenerateTemplateClass(targetFilepath, fqnTemplateName, targetName, params, result,
        hasSignature);
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
  private static void doGenerateTemplateClass(File targetFilepath, String fqnTemplateName,
      String targetName,
      List<Parameter> params, Optional<String> result, boolean hasSignature) {
    final ExtendedGeneratorSetup setup = new ExtendedGeneratorSetup();
    setup.setOutputDirectory(targetFilepath);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("TemplateClassPackage",
        TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE);
    glex.setGlobalValue("TemplateClassSetupPackage",
        TemplateClassGeneratorConstants.TEMPLATE_CLASSES_SETUP_PACKAGE);
    setup.setGlex(glex);
    TemplateClassHelper helper = new TemplateClassHelper();
    final GeneratorEngine generator = new GeneratorEngine(setup);
    ASTNode node = new EmptyNode();
    String packageNameWithSeperators = TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE
        + File.separator
        + Names.getPathFromFilename(fqnTemplateName);
    String packageNameWithDots = Names.getPackageFromPath(packageNameWithSeperators);
    boolean isMainTemplate = targetName.endsWith("Main");
    
    if (isMainTemplate) {
      generateMainTemplateFactory(generator, packageNameWithSeperators, targetName,
          node, packageNameWithDots);
    }
    
    generator.generate("typesafety.TemplateClass",
        Paths.get(packageNameWithSeperators, targetName + ".java"), node,
        packageNameWithDots, fqnTemplateName, targetName, params, result, hasSignature,
        isMainTemplate, helper);
  }
  
  public static void generateMainTemplateFactory(GeneratorEngine generator,
      String packageNameWithSeperators, String targetName, ASTNode node,
      String packageNameWithDots) {
    generator.generate("typesafety.MainTemplateFactory",
        Paths.get(packageNameWithSeperators, targetName + "Factory.java"), node, targetName,
        packageNameWithDots);
  }
  
  /**
   * Generates a TemplateStorage class, which contains all generated template
   * classes. Further it generates a generator config class to configure the
   * used generator engine in template classes and a setup template to configure
   * the static use of template classes within a template.
   * 
   * @param foundTemplates
   * @param targetFilepath
   * @param modelPath
   * @param foundTemplates
   */
  public static void generateTemplateSetup(File targetFilepath, File modelPath,
      List<String> foundTemplates) {
    String packageName = TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE + "."
        + TemplateClassGeneratorConstants.TEMPLATE_CLASSES_SETUP_PACKAGE;
    final ExtendedGeneratorSetup setup = new ExtendedGeneratorSetup();
    setup.setOutputDirectory(targetFilepath);
    setup.setTracing(false);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("TemplatePostfix",
        TemplateClassGeneratorConstants.TEMPLATE_CLASSES_POSTFIX);
    glex.setGlobalValue("TemplateClassPackage",
        TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE);
    glex.setGlobalValue("TemplatesAlias", TemplateClassGeneratorConstants.TEMPLATES_ALIAS);
    setup.setGlex(glex);
    final GeneratorEngine generator = new GeneratorEngine(setup);
    
    String basedir = getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(),
        targetFilepath.getAbsolutePath());
    String relativePath = getRelativePathFromAbsolute(basedir, targetFilepath.getAbsolutePath());
    if (relativePath.contains(File.separator)) {
      relativePath = relativePath.replace(File.separator, "/");
    }
    
    String filePath = Names.getPathFromPackage(packageName) + File.separator;
    String mp = modelPath.getPath();
    List<File> nodes = TemplateClassHelper.walkTree(modelPath);
    List<String> templates = foundTemplates;
    generator.generate("typesafety.setup.TemplateAccessor",
        Paths.get(filePath + "TemplateAccessor.java"),
        new EmptyNode(),
        packageName, templates, mp, new TemplateClassHelper());
    generator.generate("typesafety.setup.Setup", Paths.get(filePath + "Setup.ftl"),
        new EmptyNode(),
        nodes, mp,
        new TemplateClassHelper(), new ArrayList<File>());
    generator.generate("typesafety.setup.GeneratorConfig",
        Paths.get(filePath + "GeneratorConfig.java"),
        new EmptyNode(), packageName, relativePath);
  }
  
  /**
   * Compares the two paths and returns the common path. The common path is the
   * basedir.
   * 
   * @param modelPath
   * @param targetPath
   * @return
   */
  private static String getBasedirFromModelAndTargetPath(String modelPath, String targetPath) {
    String basedir = "";
    
    for (int i = 0; i < modelPath.length(); i++) {
      if (modelPath.charAt(i) == targetPath.charAt(i)) {
        basedir += modelPath.charAt(i);
      }
      else {
        break;
      }
      
    }
    return basedir;
    
  }
  
  /**
   * Replaces the basedir in the absolute Path -> path gets relative
   * 
   * @param basedir
   * @param absolutePath
   * @return
   */
  private static String getRelativePathFromAbsolute(String basedir, String absolutePath) {
    if (absolutePath.contains(basedir)) {
      return absolutePath.replace(basedir, "");
    }
    return absolutePath;
  }
  
}
