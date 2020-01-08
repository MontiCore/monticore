/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import de.monticore.cli.MontiCoreCLI
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

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
 *   - script           - the script to be used for the generation
 *                        defaults to monticore_noemf.groovy
 *   - addGrammarConfig - boolean that specifies whether the configuration called grammar should
 *                        be added to the model path
 *                        defaults to true
 *   - includeConfigs   - list of names of configurations that should be added to the model path
 *                        defaults to empty list
 */
public class MCTask extends DefaultTask {

  MCTask() {
    // set the task group name, in which all instances of MCTask will appear
    group = 'MC'
    // already add the files from the in grammar specified configuration to the config files
    grammarConfigFiles.setFrom(project.configurations.getByName("grammar").getFiles())
  }

  final RegularFileProperty grammar = project.objects.fileProperty()

  final DirectoryProperty outputDir = project.objects.directoryProperty()

  final ConfigurableFileCollection superGrammars = project.objects.fileCollection()

  final ConfigurableFileCollection grammarConfigFiles = project.objects.fileCollection()

  boolean addGrammarConfig = true

  List<File> handcodedPath = []

  List<File> modelPath = []

  List<File> templatePath = []

  List<String> includeConfigs = []

  File script

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

  @InputFiles
  @Optional
  List<File> getHandcodedPath() {
    return handcodedPath
  }

  @InputFiles
  @Optional
  List<File> getModelPath() {
    return modelPath
  }

  @InputFiles
  @Optional
  List<File> getTemplatePath() {
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

  @InputFile
  @Optional
  File getScript() {
    return script
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

  public void handcodedPath(File... paths) {
    getHandcodedPath().addAll(paths)
  }

  public void modelPath(File... paths) {
    getModelPath().addAll(paths)
  }

  public void templatePath(File... paths) {
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
      handcodedPath.add(project.layout.projectDirectory.file("src/main/java"))
    }

    // if no model path is specified use $projectDir/src/main/grammars as default
    if (modelPath.isEmpty()) {
      modelPath.add(project.layout.projectDirectory.file("src/main/grammars"))
    }

    // if no template path is specified use $projectDir/src/main/resources as default
    if (templatePath.isEmpty()) {
      templatePath.add(project.layout.projectDirectory.file("src/main/resources"))
    }

    if (!inputs.getFileChanges(grammar).isEmpty()) {
      // execute MontiCore if task is out of date
      logger.info("Rebuild because " + grammar.get().getAsFile().getName()+ " itself has changed.")
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
      project.configurations.getByName("grammar").each {
        mp.add it
      }
    }

    // if specified by the user put further configurations on model path
    for (c in includeConfigs) {
      project.configurations.getByName(c).each {
        mp.add it
      }
    }

    mp.addAll(modelPath)
    // construct string array from configuration to pass it to MontiCore
    List<String> params = [grammar.get().asFile.toString(),
                           "-o", outputDir.get().asFile.toString(),
                           "-f",
                           "-mp"]
    params.addAll(mp)
    params.add("-hcp")
    params.addAll(handcodedPath)
    params.add("-fp")
    params.addAll(templatePath)
    if (script != null) {
      params.add("-s")
      params.add(script)
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

    // execute Monticore with the given parameters
    MontiCoreCLI.main(p)
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
      def newfiles = inout.filterLine {
        line -> line.startsWith('gen:') && new File(line.toString().substring(4)).exists()
      }
      def added = newfiles.toString()
      if (!added.isEmpty()) { // added files -> generate
        logger.info('Added files:\n' + added)
        return false
      }
      // check for considered but deleted files
      def removedFiles = inout.filterLine {
        line -> line.startsWith('hwc:') && !new File(line.toString().substring(4)).exists()
      }
      def removed = removedFiles.toString()
      if (!removed.isEmpty()) { // deleted files -> generate
        logger.info('Removed files:\n' + removed)
        return false
      }
    } else { // no report -> generate
      logger.info("No previous generation report $inout found")
      return false
    }
    return true
  }

}
