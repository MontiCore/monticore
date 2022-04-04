/* (c) https://github.com/MontiCore/monticore */
package de.monticore


import de.monticore.cli.MontiCoreTool
import de.monticore.mcbasics.MCBasicsMill
import de.monticore.dstlgen.util.DSTLPathUtil
import de.se_rwth.commons.logging.Finding
import de.se_rwth.commons.logging.Log
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

import java.security.Permission
import java.util.stream.Collectors

/**
 * A Gradle task that executes the MontiCore generator.
 * Required configuration:
 *    - grammar
 *    - outputDir
 * Optional configuration
 *   - handcodedPath    - list of paths to be considered for handcrafted extensions of the generated code
 *                        defaults to $projectDir/src/main/java
 *   - modelPath        - list of paths to be considered for loading models, grammars etc.
 *                        defaults to $projectDir/src/main/grammars
 *   - templatePath     - list of paths to be considered for handcrafted templates
 *                        defaults to $projectDir/src/main/resources
 *   - configTemplate   - template to configure the integration of handwritten templates
 *                        defaults to empty resulting in MontiCore's standard generation
 *   - script           - the script to be used for the generation
 *                        defaults to monticore_noemf.groovy
 *   - groovyHook1      - groovy script that is hooked into the workflow of the standard script at hook point one
 *                        defaults to empty
 *   - groovyHook2      - groovy script that is hooked into the workflow of the standard script at hook point two
 *                        defaults to empty
 *   - addGrammarConfig - boolean that specifies whether the configuration called grammar should
 *                        be added to the model path
 *                        defaults to true
 *   - includeConfigs   - list of names of configurations that should be added to the model path
 *                        defaults to empty list
 */
abstract public class MCTask extends DefaultTask {
  
  MCTask() {
    // set the task group name, in which all instances of MCTask will appear
    group = 'MC'
    // always add the files from the configuration 'grammar' to the config files
    grammarConfigFiles.setFrom(project.configurations.getByName("grammar").getFiles())
    dependsOn(project.configurations.getByName("grammar"))

    buildInfoFile.set(project.layout.buildDirectory.get().dir("resources").dir("main").file("buildInfo.properties"))
  }
  
  final RegularFileProperty grammar = project.objects.fileProperty()
  
  final DirectoryProperty outputDir = project.objects.directoryProperty()

  final RegularFileProperty buildInfoFile = project.objects.fileProperty()

  // this attributes enables to defines super grammars for a grammar build task
  // is super grammar gets updated the task itself is rebuild as well
  final ConfigurableFileCollection superGrammars = project.objects.fileCollection()
  
  final ConfigurableFileCollection grammarConfigFiles = project.objects.fileCollection()
  
  boolean addGrammarConfig = true
  
  List<String> handcodedPath = []
  
  List<String> modelPath = []

  List<String> handcodedModelPath = []

  List<String> templatePath = []
  
  List<String> includeConfigs = []
  
  String configTemplate;
  
  String script

  String toolName;
  
  String groovyHook1;
  
  String groovyHook2;
  
  boolean help = false
  
  boolean dev = false

  boolean dstlGen = false

  
  @OutputDirectory
  DirectoryProperty getOutputDir() {
    return outputDir
  }

  @OutputFile
  RegularFileProperty getBuildInfoFile() {
    return buildInfoFile
  }

  @Incremental
  @InputFile
  RegularFileProperty getGrammar() {
    return grammar
  }
  
  @InputFile
  @Optional
  File customLog
  
  @InputFiles
  @Incremental
  @Optional
  ConfigurableFileCollection getSuperGrammars() {
    return superGrammars
  }
  
  @Input
  @Optional
  List<String> getHandcodedPath() {
    return handcodedPath
  }
  
  @Input
  @Optional
  List<String> getModelPath() {
    return modelPath
  }

  @Input
  @Optional
  List<String> getHandcodedModelPath() {
    return handcodedModelPath
  }

  @Input
  @Optional
  List<String> getTemplatePath() {
    return templatePath
  }
  
  @InputFiles
  @Optional
  List<String> getIncludeConfigs() {
    return includeConfigs
  }
  
  @InputFiles
  @Optional
  ConfigurableFileCollection getGrammarConfigFiles() {
    return grammarConfigFiles
  }
  
  @Input
  @Optional
  String getConfigTemplate() {
    return configTemplate
  }
  
  @Input
  @Optional
  String getScript() {
    return script
  }

  @Input
  @Optional
  String getToolName() {
    return toolName;
  }

  @Input
  @Optional
  String getGroovyHook1() {
    return groovyHook1
  }
  
  @Input
  @Optional
  String getGroovyHook2() {
    return groovyHook2
  }
  
  @Input
  boolean getDev() {
    return dev
  }
  
  @Input
  boolean getHelp() {
    return help
  }
  
  @Input
  boolean getAddGrammarConfig() {
    return addGrammarConfig
  }

  @Input
  boolean getDstlGen() {
    return dstlGen
  }

  public void handcodedPath(String... paths) {
    getHandcodedPath().addAll(paths)
  }
  
  public void modelPath(String... paths) {
    getModelPath().addAll(paths)
  }

  public void handcodedModelPath(String... paths) {
    getHandcodedModelPath().addAll(paths)
  }

  public void templatePath(String... paths) {
    getTemplatePath().addAll(paths)
  }
  
  public void includeConfigs(String... configurations) {
    getIncludeConfigs().addAll(configurations)
  }
  
  @TaskAction
  void execute(InputChanges inputs) {
    logger.info(inputs.isIncremental() ? "CHANGED inputs considered out of date"
            : "ALL inputs considered out of date");

    // generate build info properties file into target resources directory
    generateBuildInfo()

    // if no path for hand coded classes is specified use $projectDir/src/main/java as default
    if (handcodedPath.isEmpty()) {
      File hcp = project.layout.projectDirectory.file("src/main/java").getAsFile()
      if(hcp.exists()) {
        handcodedPath.add(hcp.toString())
      }
    }
    
    // if no model path is specified use $projectDir/src/main/grammars as default
    if (modelPath.isEmpty()) {
      File mp = project.layout.projectDirectory.file("src/main/grammars").getAsFile()
      if(mp.exists()) {
        modelPath.add(mp.toString())
      }
    }

    // if no grammar path is specified use $projectDir/src/main/grammars as default
    if (handcodedModelPath.isEmpty()) {
      File mhcp = project.layout.projectDirectory.file("src/main/grammarsHC").asFile
      if (mhcp.exists()) {
        handcodedModelPath.add(mhcp.toString())
      }
    }

    // if no template path is specified use $projectDir/src/main/resources as default
    if (templatePath.isEmpty()) {
      File tp = project.layout.projectDirectory.file("src/main/resources").getAsFile()
      if(tp.exists()) {
        templatePath.add(tp.toString())
      }
    }
    
    if (!inputs.getFileChanges(grammar).isEmpty()) {
      // execute MontiCore if task is out of date
      logger.info("Rebuild because " + grammar.get().getAsFile().getName() + " itself has changed.")
      rebuildGrammar()
    } else if (!inputs.getFileChanges(superGrammars).isEmpty()) {
      // rebuild if superGrammars got updated
      logger.info("Rebuild because local superGrammar has changed.")
      rebuildGrammar()
    }
  }
  
  void rebuildGrammar() {
    List<String> mp = new ArrayList()
    logger.info("out of date: " + grammar.get().getAsFile().getName())
    // if not disabled put grammar configuration on model path
    if (addGrammarConfig) {
      project.configurations.getByName("grammar").each { mp.add it }
    }
    // if specified by the user put further configurations on model path
    for (c in includeConfigs) {
      project.configurations.getByName(c).each { mp.add it }
    }
    
    mp.addAll(modelPath)
    // construct string array from configuration to pass it to MontiCore
    List<String> params = ["-g", grammar.get().asFile.toString(),
                           "-o", outputDir.get().asFile.toString()]
    if (!mp.isEmpty()) {
      params.add("-mp")
      params.addAll(mp)
    }
    if (toolName != null) {
      params.add("-tn")
      params.add(toolName)
    }
    if (!handcodedPath.isEmpty()) {
      params.add("-hcp")
      params.addAll(handcodedPath)
    }
    if (!templatePath.isEmpty()) {
      params.add("-fp")
      params.addAll(templatePath)
    }
    if (!templatePath.isEmpty()) {
      params.add("-fp")
      params.addAll(templatePath)
    }
    if (!handcodedModelPath.isEmpty()) {
      params.add("-hcg")
      params.addAll(handcodedModelPath)
    }
    params.add("-dstlGen")
    params.add(Boolean.toString(dstlGen))
    if (configTemplate != null) {
      params.add("-ct")
      if (configTemplate.endsWith(".ftl")){
        configTemplate = configTemplate.substring(0, configTemplate.length()-4)
      }
      params.add(configTemplate)
    }
    if (script != null) {
      params.add("-sc")
      params.add(script)
    }
    if (groovyHook1 != null) {
      params.add("-gh1")
      params.add(groovyHook1)
    }
    if (groovyHook2 != null) {
      params.add("-gh2")
      params.add(groovyHook2)
    }
    if (dev) {
      params.add("-d")
    }
    if (customLog != null) {
      params.add("-cl")
      params.add(customLog)
    }
    if (help) {
      params.add("-h")
    }
    def p = params.toArray() as String[]

    System.setSecurityManager(new SecurityManager()
    {
      @Override public void checkExit(int status) {
        throw new MCTaskError()
      }
      
      @Override public void checkPermission(Permission perm) {
        // Allow other activities by default
      }
    })
    try {
      // execute Monticore with the given parameters
      MontiCoreTool.main(p)
      MCBasicsMill.globalScope().getSymbolPath().close();
    } catch(MCTaskError e){
      // in case of failure print the error and fail
      String error = Log.getFindings().stream().
              filter({f -> f.getType().equals(Finding.Type.ERROR)})
              .map({f -> f.getMsg()})
              .collect(Collectors.joining("\n"))
      MCBasicsMill.globalScope().getSymbolPath().close();
      ant.fail(error)
    }
  }
  
  /**
   *
   * @param grammar - the grammar relative to the surrounding folder structure.
   *                  e.g. if the grammar Automaton.mc4 is located in
   *                  the folder ${projectDir}/src/main/grammar/
   *                  and has the package aut
   *                  the grammar parameter should be "aut/Automaton.mc4"
   * @return true -  if handcrafted extensions of generated files are up to date which means
   *                  no generation necessary
   */
  boolean incCheck(String grammar) {
    String outAsString = outputDir.get().asFile.toString()
    String grammarWithoutExt = grammar.replace(".mc4", "").toLowerCase()
    def inout = new File(outAsString + "/" + grammarWithoutExt + "/IncGenGradleCheck.txt")
    return IncChecker.incCheck(inout, grammar, logger, "mc4")
  }
/**
   * Returns the path to the TR grammar.
   * Please ensure that the outputDir is previously set
   * @param originalGrammar the original grammar file
   * @return the TR grammar
   */
  File getTRFile(File originalGrammar) {
    return new File(outputDir.get().asFile.toString()
            + "/"
            + DSTLPathUtil.getTRGrammar(
            modelPath.isEmpty() ? [project.layout.projectDirectory.file("src/main/grammars").toString()] : modelPath,
            originalGrammar).toString())
  }
  protected File fromBasePath(String filePath) {
    File file = new File(filePath);
    return !file.isAbsolute()
            ? new File(project.getProjectDir(), filePath)
            : file;
  }



  protected void generateBuildInfo() {
    File file = buildInfoFile.get().asFile
    file.mkdirs()
    file.delete()
    file.createNewFile()
    file.write("version = ${project.version}")
  }

}

/**
 * This is only needed to prevent elongated stacktraces
 */
class MCTaskError extends Throwable {
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
