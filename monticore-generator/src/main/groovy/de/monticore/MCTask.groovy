/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import com.google.common.hash.Hashing
import com.google.common.io.Files
import de.monticore.cli.MontiCoreStandardCLI
import de.se_rwth.commons.logging.Finding
import de.se_rwth.commons.logging.Log
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
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
  }
  
  final RegularFileProperty grammar = project.objects.fileProperty()
  
  final DirectoryProperty outputDir = project.objects.directoryProperty()
  
  // this attributes enables to defines super grammars for a grammar build task
  // is super grammar gets updated the task itself is rebuild as well
  final ConfigurableFileCollection superGrammars = project.objects.fileCollection()
  
  final ConfigurableFileCollection grammarConfigFiles = project.objects.fileCollection()
  
  boolean addGrammarConfig = true
  
  List<String> handcodedPath = []
  
  List<String> modelPath = []
  
  List<String> templatePath = []
  
  List<String> includeConfigs = []
  
  String configTemplate;
  
  String script
  
  String groovyHook1;
  
  String groovyHook2;
  
  boolean help = false
  
  boolean dev = false
  
  
  @OutputDirectory
  DirectoryProperty getOutputDir() {
    return outputDir
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
  
  @InputFiles
  @Optional
  List<String> getModelPath() {
    return modelPath
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
  
  public void handcodedPath(String... paths) {
    getHandcodedPath().addAll(paths)
  }
  
  public void modelPath(String... paths) {
    getModelPath().addAll(paths)
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
    if (!handcodedPath.isEmpty()) {
      params.add("-hcp")
      params.addAll(handcodedPath)
    }
    if (!templatePath.isEmpty()) {
      params.add("-fp")
      params.addAll(templatePath)
    }
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
      MontiCoreStandardCLI.main(p)
    } catch(MCTaskError e){
      // in case of failure print the error and fail
      String error = Log.getFindings().stream().
              filter({f -> f.getType().equals(Finding.Type.ERROR)})
              .map({f -> f.getMsg()})
              .collect(Collectors.joining("\n"))
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
    if (inout.exists()) { // check whether report exists
      // check for new files to consider
      def newfiles = new StringWriter()
      inout.withReader {
        it.filterLine(newfiles) {
          line -> line.startsWith('gen:') && new File(line.toString().substring(4)).exists()
        }
      }
      def added = newfiles.toString()
      if (!added.isEmpty()) { // added files -> generate
        logger.info('Added files:\n' + added)
        return false
      }
      // check for considered but deleted files
      def removedFiles = new StringWriter()
      inout.withReader {
        it.filterLine(removedFiles) {
          line -> line.startsWith('hwc:') && !new File(line.toString().substring(4)).exists()
        }
      }
      def removed = removedFiles.toString()
      if (!removed.isEmpty()) { // deleted files -> generate
        logger.info('Removed files:\n' + removed)
        return false
      }
      // check whether local super grammars have changed
      def grammarsUpToDate = true
      inout.withReader {
        it.eachLine {
          line ->
            if (line.startsWith('mc4:')) {
              def (grammarString, checksum) = line.toString().substring(4).tokenize(' ')
              def grammarFile = new File(grammarString)
              if (!grammarFile.exists()) { // deleted grammar -> generate
                logger.info("Regenerating Code for " + grammar + " : Grammar " + grammarString + " does so longer exist.")
                grammarsUpToDate = false
                return
              } else if (!Files.asByteSource(grammarFile).hash(Hashing.md5()).toString().equals(checksum.trim())) {
                // changed grammar -> generate
                logger.info("Regenerating Code for " + grammar + " : Grammar " + grammarString + " has changed")
                grammarsUpToDate = false
                return
              }
            }
        }
      }
      if(!grammarsUpToDate) {return false}
    } else { // no report -> generate
      logger.info("No previous generation report $inout found")
      return false
    }
    return true
  }
  
  protected File fromBasePath(String filePath) {
    File file = new File(filePath);
    return !file.isAbsolute()
            ? new File(project.getProjectDir(), filePath)
            : file;
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
