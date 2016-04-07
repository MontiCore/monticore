/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator;

import groovy.lang.Script;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.google.common.collect.Sets;

import de.montiarc.generator.typesafety.TemplateClassGenerator;
import de.monticore.ModelingLanguageFamily;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.symboltable.GlobalScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author Robert Heim
 */
public class TemplateClassGeneratorScript extends Script implements GroovyRunner {
  
  protected static final String[] DEFAULT_IMPORTS = { "de.monticore.lang.montiarc.montiarc._ast" };
  
  protected static final String LOG = "TypesafetyScript";
  
  /**
   * @see de.se_rwth.commons.groovy.GroovyRunner#run(java.lang.String,
   * de.se_rwth.commons.configuration.Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(TemplateClassGeneratorScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
    
    // configuration
    TemplateClassGeneratorConfiguration config = TemplateClassGeneratorConfiguration.withConfiguration(configuration);
    
    // we add the configuration object as property with a special property
    // name
    builder.addVariable(TemplateClassGeneratorConfiguration.CONFIGURATION_PROPERTY, config);
    
    config.getAllValues().forEach((key, value) -> builder.addVariable(key, value));
    
    // after adding everything we override a couple of known variable
    // bindings
    // to have them properly typed in the script
    builder.addVariable(TemplateClassGeneratorConfiguration.Options.MODELPATH.toString(),
        config.getModelPath());
    builder.addVariable(TemplateClassGeneratorConfiguration.Options.OUT.toString(),
        config.getOut());
    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }
  
 
  
  /**
   * TODO: Write me!
   * @param targetName Classname of the target TemplateClass
   * @param modelPath Path of templates e.g. src/main/resources
   * @param fqnTemplateName full qualified name of template e.g. src/test/resources/templates/component/Component.ftl
   * @param targetFilepath Path where the TemplateClass should be generated to e.g. target/generated-source/
   */
  public void generate(String targetName, Path modelPath, File fqnTemplateName,
      File targetFilepath) {
    TemplateClassGenerator.generateClassForTemplate(targetName, modelPath, fqnTemplateName, targetFilepath);
  }
  
  /**
   * Gets called by Groovy Script. Generates Template Classes for all templates in {@link modelPath} to {@link targetFilepath}
   * @param modelPath
   * @param fqnTemplateName
   */
  public void generate(File modelPath, File targetFilepath){
    List<String> foundTemplates = Modelfinder.getModelsInModelPath(Paths.get(modelPath.getAbsolutePath()).toFile(), "ftl");
    for(String template : foundTemplates){
      System.out.println("[TypesafetyScript] generates model: "+ template);
      String simpleName = Names.getSimpleName(template);
      String fileName = modelPath.getAbsolutePath()+File.separator+Names.getPathFromQualifiedName(template)+File.separator+simpleName+".ftl";
      generate(simpleName+"TemplateClass", Paths.get(modelPath.getAbsolutePath()), new File(fileName), targetFilepath);
    }
  }
  
//  public void generate(final List<File> modelPaths, File outputDirectory,
//      Optional<String> hwcPath) {
//    for(File modelPath : modelPaths){
//      File fqnMP = Paths.get(modelPath.getAbsolutePath()).toFile();
//      List<String> modelsInModelPath = Modelfinder.getModelsInModelPath(fqnMP, "ftl");
//      for(String model : modelsInModelPath){
////        generate(modelPaths, model, outputDirectory, hwcPath);
//      }
//    }
//  }
  
  // #######################
  // log functions
  // #######################
  
  public boolean isDebugEnabled() {
    return Log.isDebugEnabled(LOG);
  }
  
  public void debug(String msg) {
    Log.debug(msg, LOG);
  }
  
  public void debug(String msg, Throwable t) {
    Log.debug(msg, t, LOG);
  }
  
  public boolean isInfoEnabled() {
    return Log.isInfoEnabled(LOG);
  }
  
  public void info(String msg) {
    Log.info(msg, LOG);
  }
  
  public void info(String msg, Throwable t) {
    Log.info(msg, t, LOG);
  }
  
  public void warn(String msg) {
    Log.warn(msg);
  }
  
  public void warn(String msg, Throwable t) {
    Log.warn(msg, t);
  }
  
  public void error(String msg) {
    Log.error(msg);
  }
  
  public void error(String msg, Throwable t) {
    Log.error(msg, t);
  }
  
  /**
   * @see groovy.lang.Script#run()
   */
  @Override
  public Object run() {
    return true;
  }
  
}
