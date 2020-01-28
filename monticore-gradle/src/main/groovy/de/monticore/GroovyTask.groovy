/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import com.google.common.collect.ImmutableListMultimap
import com.google.common.collect.ImmutableMultimap
import de.se_rwth.commons.configuration.Configuration
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor
import de.se_rwth.commons.groovy.GroovyInterpreter
import de.se_rwth.commons.groovy.GroovyRunner
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.work.InputChanges

import java.lang.reflect.InvocationTargetException
import java.util.stream.Collectors

public class GroovyTask extends DefaultTask {

  GroovyTask() {
    // set the task group name, in which all instances of MCTask will appear
    group = 'MC'
  }

  File model

  File outputDir

  List<File> handcodedPath = []

  List<File> modelPath = []

  List<File> templatePath = []

  List<String> includeConfigs = []
  
  String script
  
  String baseClass
  
  Map<String,String> arguments = [:]

  Iterable<File> classpath;

  boolean help = false

  @InputFiles @Optional
  Iterable<File> getClasspath() {
    return classpath
  }

  @Optional @InputFile
  File getModel() {
    return model
  }

  @OutputDirectory
  File getOutputDir() {
    return outputDir
  }

  @Input
  String getScript() {
    return script
  }

  @Input
  String getBaseClass() {
    return baseClass
  }

  @Input @Optional
  Map<String, String> getArguments() {
    return arguments
  }
  
  public void handcodedPath(File... paths){
    handcodedPath.addAll(paths)
  }

  public void modelPath(File... paths){
    modelPath.addAll(paths)
  }

  public void templatePath(File... paths){
    templatePath.addAll(paths)
  }
  
  public void includeConfigs(String... configurations){
    includeConfigs.addAll(configurations)
  }

  @InputFiles @Optional
  List<File> getHandcodedPath() {
    return handcodedPath
  }

  @InputFiles @Optional
  List<File> getModelPath() {
    return modelPath
  }

  @InputFiles @Optional
  List<File> getTemplatePath() {
    return templatePath
  }

  @InputFiles @Optional
  List<String> getIncludeConfigs() {
    return includeConfigs
  }

  @Input
  boolean getHelp() {
    return help
  }
  
  // TODO handcodedPath, templatePath
  @TaskAction
  void execute(InputChanges inputs) {
    ClassLoader l = project.buildscript.classLoader
    Class<? extends Script> baseClass = null
    String script = null
  
    // 1st we try to load the Groovy script
    try {
      // first per class loader
      URL r = l.getResource(getScript())
      if (r == null) {
        // otherwise from file system
        r = fromBasePath(getScript()).toURI().toURL()
      }
      Scanner scr = new Scanner(r.openStream())
      script = scr.useDelimiter("\\A").next()
      scr.close()
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to load the specified script " + getScript(), e)
    }
  
    // 2nd we check the base class parameter try to load base class if
    // specified
    try {
      if (getBaseClass() != null && !getBaseClass().isEmpty()) {
        baseClass = (Class<? extends Script>) l.loadClass(getBaseClass().toString())
      }
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to load the script base class $baseClass.", e)
    }
  
    // 3rd prepare the configuration instance
    Configuration configuration = null
    // a bit of debug
    if (logger.isDebugEnabled()) {
      logger.debug("Configuration content:")
      logger.debug("  modelPath " + getModelPath())
      getArguments().forEach({ key, value -> logger.debug("  " + key + " " + value) })
    }
  
    ImmutableMultimap.Builder<String, String> arguments2 = ImmutableListMultimap.builder()
    // add all generic arguments
    getArguments()
            .forEach({ key, value -> arguments2.putAll(key, Arrays.asList(value.split(";"))) })
    // add the models parameter
    File model = getModel()
    if (model != null) {
      arguments2.put("model", model.getPath())
    }
    // add the modelPath parameter
    List<File> combinedModelPath = doCreateModelPath()
    if (!combinedModelPath.isEmpty()) {
      arguments2.putAll("modelPath", combinedModelPath.stream().collect(
              Collectors.mapping({ p -> p.getPath() }, Collectors.toList())))
    }
    // add the outputDirectory parameter
    File outputDirectory = getOutputDir()
    if (outputDirectory != null) {
      arguments2.putAll("outputDirectory", fromBasePath(outputDirectory).getPath())
    }
  
    configuration = ConfigurationPropertiesMapContributor.fromSplitMap(arguments2.build())
  
    // 4th we run the script
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
    if (baseClass != null) {
      builder.withScriptBaseClass(baseClass)
      // now we check if the base class can be used as Groovy runner by checking
      // our interface if this is the case we use the runner and terminate the
      // execution otherwise execution will continue and use the manual Groovy
      // invocation
      if (GroovyRunner.class.isAssignableFrom(baseClass)) {
        logger.info("Given base class " + baseClass.getName() + " is used as Groovy runner.")
        try {
          GroovyRunner runner = (GroovyRunner) baseClass.getConstructor().newInstance()
          runner.run(script, configuration)
        }
        catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
          throw new RuntimeException(
                  "Failed to instantiate the script base class as Groovy runner. The base class Groovy runner must supply a public default no-args constructor.",
                  e)
        }
        return
      }
      else {
        logger.info(
                "Given base class " + baseClass.getName()
                        + " is not a Groovy runner; using default runner.")
      }
    }
  
    // inject any configuration parameters as variables and execute the script
    builder.addVariable("_configuration", configuration)
    configuration.getAllValues().forEach({ key, value -> builder.addVariable(key, value) })
    builder.build().evaluate(script)
  
  }
  
  private List<File> doCreateModelPath(){
    List<File> mp = new ArrayList()
    mp.addAll(modelPath)
    for(c in includeConfigs){
      project.configurations.getByName(c).each { mp.add it}
    }
    return mp
  }
  
  protected File fromBasePath(String filePath) {
    File file = new File(filePath)
    return !file.isAbsolute()
            ? new File(project.getProjectDir(), filePath)
            : file
  }
  
  protected File fromBasePath(File file) {
    return fromBasePath(file.getPath())
  }
}
