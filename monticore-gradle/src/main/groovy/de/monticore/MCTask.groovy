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

public class MCTask extends DefaultTask {
  @InputFile
  RegularFileProperty grammar = project.objects.fileProperty()
  
  @OutputDirectory
  DirectoryProperty outputDir = project.objects.directoryProperty()
  
  
  @Input
  private Configuration grammarConfig = project.configurations.getByName("grammar")
  
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
  
  @Input @Optional
  String group = "MC"
  
  
  @TaskAction
  void execute(IncrementalTaskInputs inputs) {
    def List<String> mp = new ArrayList()
    if(handcodedPath.isEmpty()){
      handcodedPath.add(project.layout.projectDirectory.file("src/main/java"))
    }
    if(modelPath.isEmpty()){
      modelPath.add(project.layout.projectDirectory.file("src/main/grammars"))
    }
    if(templatePath.isEmpty()){
      templatePath.add(project.layout.projectDirectory.file("src/main/resources"))
    }
    if(addGrammarConfig) {
      grammarConfig.each { mp.add it }
    }
    for(c in includeConfigs){
      project.configurations.getByName(c).each { mp.add it}
    }
    mp.addAll(modelPath)
    logger.info(inputs.isIncremental() ? "CHANGED inputs considered out of date"
            : "ALL inputs considered out of date");
    inputs.outOfDate({ InputFileDetails change ->
      logger.info("out of date: "+change.file.name)
      List<String> params = [grammar.get().asFile.toString(), "-o", outputDir.get().asFile.toString(), "-f", "-mp"]
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
      MontiCoreCLI.main(p)
    })
  }
  
  
  boolean incCheck (String incReport) {
    def inout = new File (incReport)
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
      logger.info( "No previous generation report $incReport found")
      return false
    }
    return true
  }
  
}
