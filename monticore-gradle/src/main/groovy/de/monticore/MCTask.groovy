package de.monticore

import de.monticore.cli.MontiCoreCLI
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.incremental.InputFileDetails

public class MCTask extends DefaultTask {
  @InputFile
  File grammar
  
  @OutputDirectory
  File outputDir
  
  @Input @Optional
  boolean addGrammarConfig = true
  
  @Input @Optional
  List<String> handcodedPath = []
  
  @Input @Optional
  List<String> modelPath = []
  
  @Input @Optional
  List<String> templatePath = []
  
  @Input @Optional
  List<String> includeConfigs = []
  
  
  @Input @Optional
  public void handcodedPath(String... strings){
    handcodedPath.addAll(strings)
  }
  
  @Input @Optional
  public void includeConfigs(String... configurations){
    includeConfigs.addAll(configurations)
  }
  
  String group = "MC"
  
  
  @TaskAction
  void execute(IncrementalTaskInputs inputs) {
    def List<String> mp = new ArrayList()
    if(addGrammarConfig) {
      if (project.configurations.find { it.name == 'grammar' }) {
        project.configurations.getByName("grammar").each { mp.add it }
      }
    }
    for(c in includeConfigs){
      project.configurations.getByName(c).each { mp.add it}
    }
    mp.addAll(modelPath)
    logger.info(inputs.isIncremental() ? "CHANGED inputs considered out of date"
            : "ALL inputs considered out of date");
    inputs.outOfDate({ InputFileDetails change ->
      System.out.println("out of date: "+change.file.name)
      List<String> params = [grammar.toString(), "-o", outputDir.toString(), "-f", "-mp"]
      params.addAll(mp)
      params.add("-hcp")
      params.addAll(handcodedPath)
      params.add("-fp")
      params.addAll(templatePath)
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