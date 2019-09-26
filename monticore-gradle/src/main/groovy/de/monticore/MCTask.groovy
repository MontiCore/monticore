/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import de.monticore.cli.MontiCoreCLI
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.incremental.InputFileDetails

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
  @InputFile
  RegularFileProperty grammar = project.objects.fileProperty()
  
  @OutputDirectory
  DirectoryProperty outputDir = project.objects.directoryProperty()
  
  
  @Input
  protected Configuration grammarConfig = project.configurations.getByName("grammar")
  
  @Input @Optional
  boolean addGrammarConfig = true
  
  @Input @Optional
  List<File> handcodedPath = []
  
  @Input @Optional
  List<File> modelPath = []
  
  @Input @Optional
  List<File> templatePath = []
  
  @Input @Optional
  List<String> includeConfigs = []
  
  @Input @Optional
  File script
  
  @Input @Optional
  boolean dev = false
  
  @Input@Optional
  File customLog
  
  @Input @Optional
  public void handcodedPath(File... paths){
    handcodedPath.addAll(paths)
  }
  
  @Input @Optional
  public void modelPath(File... paths){
    modelPath.addAll(paths)
  }
  
  @Input @Optional
  public void templatePath(File... paths){
    templatePath.addAll(paths)
  }
  
  @Input @Optional
  boolean help = false
  
  @Input @Optional
  public void includeConfigs(String... configurations){
    includeConfigs.addAll(configurations)
  }
  
  String group = "MC"
  
  
  @TaskAction
  void execute(IncrementalTaskInputs inputs) {
    logger.info(inputs.isIncremental() ? "CHANGED inputs considered out of date"
            : "ALL inputs considered out of date");
    
    List<String> mp = new ArrayList()
    
    // if no path for hand coded classes is specified use $projectDir/src/main/java as default
    if(handcodedPath.isEmpty()){
      handcodedPath.add(project.layout.projectDirectory.file("src/main/java"))
    }
    
    // if no model path is specified use $projectDir/src/main/grammars as default
    if(modelPath.isEmpty()){
      modelPath.add(project.layout.projectDirectory.file("src/main/grammars"))
    }
    
    // if no template path is specified use $projectDir/src/main/resources as default
    if(templatePath.isEmpty()){
      templatePath.add(project.layout.projectDirectory.file("src/main/resources"))
    }
    
    // execute MontiCore if task is out of date
    inputs.outOfDate({ InputFileDetails change ->
      logger.info("out of date: "+change.file.name)
      
      // if not disabled put grammar configuration on model path
      if(addGrammarConfig) {
        grammarConfig.each { mp.add it; }
      }
      
      // if specified by the user put further configurations on model path
      for(c in includeConfigs){
        project.configurations.getByName(c).each { mp.add it}
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
      if(script != null){
        params.add("-s")
        params.add(script)
      }
      if(dev){
        params.add("-d")
      }
      if(customLog != null){
        params.add("-cl")
        params.add(customLog)
      }
      if(help){
        params.add("-h")
      }
      def p = params.toArray() as String[]
      
      // execute Monticore with the given parameters
      MontiCoreCLI.main(p)
    })
  }
  
  /**
   *
   * @param grammar - the grammar relative to the surrounding folder structure.
   *                  e.g. if the grammar Automaton.mc4 is located in
   *                  the folder ${projectDir}/src/main/grammar/
   *                  and has the package aut
   *                  the grammar parameter should be "aut/Automaton.mc4"
   * @return  true -  if handcrafted extensions of generated files are up to date which means
   *                  no generation necessary
   */
  boolean incCheck (String grammar) {
    String outAsString = outputDir.get().asFile.toString()
    String grammarWithoutExt = grammar.replace(".mc4","").toLowerCase()
    def inout = new File (outAsString + "/" + grammarWithoutExt + "/IncGenGradleCheck.txt")
    if(inout.exists()) { // check whether report exists
      // check for new files to consider
      def newfiles = inout.filterLine {
        line -> line.startsWith('gen:') && new File(line.toString().substring(4)).exists()
      }
      def added = newfiles.toString()
      if(!added.isEmpty()) { // added files -> generate
        logger.info( 'Added files:\n' + added)
        return false
      }
      // check for considered but deleted files
      def removedFiles = inout.filterLine {
        line -> line.startsWith('hwc:') && !new File(line.toString().substring(4)).exists()
      }
      def removed = removedFiles.toString()
      if(!removed.isEmpty()) { // deleted files -> generate
        logger.info( 'Removed files:\n' + removed)
        return false
      }
    } else { // no report -> generate
      logger.info( "No previous generation report $inout found")
      return false
    }
    return true
  }
  
}
