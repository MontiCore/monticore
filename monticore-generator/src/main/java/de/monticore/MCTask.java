/* (c) https://github.com/MontiCore/monticore */
package de.monticore;


import com.google.common.collect.Iterables;
import de.monticore.cli.MontiCoreTool;
import de.monticore.gradle.GradleTaskStatistic;
import de.monticore.gradle.UserJsonString;
import de.monticore.mcbasics.MCBasicsMill;
import de.monticore.dstlgen.util.DSTLPathUtil;
import de.monticore.symboltable.serialization.json.*;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
@CacheableTask
public abstract class MCTask extends DefaultTask implements GradleTaskStatistic {
  
  public MCTask() {
    Project project = getProject();
    // set the task group name, in which all instances of MCTask will appear
    setGroup("MC");
    // always add the files from the configuration 'grammar' to the config files
    Configuration grammarConfiguration = project.getConfigurations().getByName(MCPlugin.GRAMMAR_CONFIGURATION_NAME);
    grammarConfigFiles.from(grammarConfiguration);
    dependsOn(grammarConfiguration);

    getBuildInfoFile().convention(project.getLayout().getBuildDirectory().file("resources/main/buildInfo.properties"));
    getReportDir().convention(project.getLayout().getBuildDirectory().dir("reports"));
  }
  
  public final RegularFileProperty grammar = getProject().getObjects().fileProperty();

  public final RegularFileProperty customLog = getProject().getObjects().fileProperty();

  public final DirectoryProperty outputDir = getProject().getObjects().directoryProperty();

  public final RegularFileProperty buildInfoFile = getProject().getObjects().fileProperty();

  // this attributes enables to defines super grammars for a grammar build task
  // is super grammar gets updated the task itself is rebuild as well
  public final ConfigurableFileCollection superGrammars = getProject().getObjects().fileCollection();

  public final ConfigurableFileCollection grammarConfigFiles = getProject().getObjects().fileCollection();

  public boolean addGrammarConfig = true;

  public List<String> handcodedPath = new ArrayList<>();

  public List<String> modelPath = new ArrayList<>();

  public List<String> handcodedModelPath = new ArrayList<>();

  public List<String> templatePath = new ArrayList<>();
  
  public List<String> includeConfigs = new ArrayList<>();

  public String configTemplate;

  public String script;

  public String toolName;

  public String groovyHook1;

  public String groovyHook2;

  public boolean help = false;

  public boolean dev = false;

  public boolean dstlGen = false;

  
  @OutputDirectory
  public DirectoryProperty getOutputDir() {
    return outputDir;
  }

  @OutputDirectory
  public abstract DirectoryProperty getReportDir();


  @OutputFile
  public RegularFileProperty getBuildInfoFile() {
    return buildInfoFile;
  }

  @Incremental
  @InputFile
  @PathSensitive(PathSensitivity.RELATIVE)
  public RegularFileProperty getGrammar() {
    return grammar;
  }
  
  @InputFile
  @Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  public RegularFileProperty getCustomLog(){
    return customLog;
  }
  
  @InputFiles
  @Incremental
  @Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  public ConfigurableFileCollection getSuperGrammars() {
    return superGrammars;
  }
  
  @Input
  @Optional
  public List<String> getHandcodedPath() {
    return handcodedPath;
  }
  
  @Input
  @Optional
  public List<String> getModelPath() {
    return modelPath;
  }

  @Input
  @Optional
  public List<String> getHandcodedModelPath() {
    return handcodedModelPath;
  }

  @Input
  @Optional
  public List<String> getTemplatePath() {
    return templatePath;
  }
  
  @InputFiles
  @Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  public List<String> getIncludeConfigs() {
    return includeConfigs;
  }
  
  @InputFiles
  @Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  public ConfigurableFileCollection getGrammarConfigFiles() {
    return grammarConfigFiles;
  }
  
  @Input
  @Optional
  public String getConfigTemplate() {
    return configTemplate;
  }
  
  @Input
  @Optional
  public String getScript() {
    return script;
  }

  @Input
  @Optional
  public String getToolName() {
    return toolName;
  }

  @Input
  @Optional
  public String getGroovyHook1() {
    return groovyHook1;
  }
  
  @Input
  @Optional
  public String getGroovyHook2() {
    return groovyHook2;
  }
  
  @Input
  public boolean getDev() {
    return dev;
  }
  
  @Input
  public boolean getHelp() {
    return help;
  }
  
  @Input
  public boolean getAddGrammarConfig() {
    return addGrammarConfig;
  }

  @Input
  public boolean getDstlGen() {
    return dstlGen;
  }

  public void handcodedPath(String... paths) {
    getHandcodedPath().addAll(List.of(paths));
  }
  
  public void modelPath(String... paths) {
    getModelPath().addAll(List.of(paths));
  }

  public void handcodedModelPath(String... paths) {
    getHandcodedModelPath().addAll(List.of(paths));
  }

  public void templatePath(String... paths) {
    getTemplatePath().addAll(List.of(paths));
  }
  
  public void includeConfigs(String... configurations) {
    getIncludeConfigs().addAll(List.of(configurations));
  }
  
  @TaskAction
  public void execute(InputChanges inputs) throws IOException {
    getLogger().info(inputs.isIncremental() ? "CHANGED inputs considered out of date"
            : "ALL inputs considered out of date");

    // generate build info properties file into target resources directory
    generateBuildInfo();

    // if no path for hand coded classes is specified use $projectDir/src/main/java as default
    if (handcodedPath.isEmpty()) {
      File hcp = getProject().getLayout().getProjectDirectory().file("src/main/java").getAsFile();
      if(hcp.exists()) {
        handcodedPath.add(hcp.toString());
      }
    }
    
    // if no model path is specified use $projectDir/src/main/grammars as default
    if (modelPath.isEmpty()) {
      File mp = getProject().getLayout().getProjectDirectory().file("src/main/grammars").getAsFile();
      if(mp.exists()) {
        modelPath.add(mp.toString());
      }
    }

    // if no grammar path is specified use $projectDir/src/main/grammars as default
    if (handcodedModelPath.isEmpty()) {
      File mhcp = getProject().getLayout().getProjectDirectory().file("src/main/grammarsHC").getAsFile();
      if (mhcp.exists()) {
        handcodedModelPath.add(mhcp.toString());
      }
    }

    // if no template path is specified use $projectDir/src/main/resources as default
    if (templatePath.isEmpty()) {
      File tp = getProject().getLayout().getProjectDirectory().file("src/main/resources").getAsFile();
      if(tp.exists()) {
        templatePath.add(tp.toString());
      }
    }
    if (!Iterables.isEmpty(inputs.getFileChanges(grammar))) {
      // execute MontiCore if task is out of date
      getLogger().info("Rebuild because " + grammar.get().getAsFile().getName() + " itself has changed.");
      rebuildGrammar();
    } else if (!Iterables.isEmpty(inputs.getFileChanges(superGrammars))) {
      // rebuild if superGrammars got updated
      getLogger().info("Rebuild because local superGrammar has changed.");
      rebuildGrammar();
    }
  }

  @Internal
  protected String[] getParameters(){
    return getParameters(File::getAbsolutePath);
  }

  protected String[] getParameters(Function<File, String> printPath){
    List<String> mp = new ArrayList<>();
    // if not disabled put grammar configuration on model path
    if (addGrammarConfig) {
      getProject().getConfigurations().getByName(MCPlugin.GRAMMAR_CONFIGURATION_NAME).forEach(it -> mp.add(it.toString()));
    }
    // if specified by the user put further configurations on model path
    for (String c : includeConfigs) {
      getProject().getConfigurations().getByName(c).forEach(it -> mp.add(it.toString()));
    }

    mp.addAll(modelPath);
    // construct string array from configuration to pass it to MontiCore
    List<String> params = new ArrayList<>(Arrays.asList("-g", printPath.apply(grammar.get().getAsFile()),
        "-o", printPath.apply(outputDir.get().getAsFile())));
    // Note 1: toString needs to be called on most elements, since Gradle can put a File to a String field
    if(getReportDir().isPresent()){
      params.add("-r");
      params.add(printPath.apply(getReportDir().get().getAsFile()));
    }
    if(getReportDir().isPresent()){
      params.add("-rb");
      params.add(printPath.apply(this.getProject().getProjectDir().toPath().toFile()));
    }
    if (!mp.isEmpty()) {
      params.add("-mp");
      // See Note 1
      for (Object it : mp) {
        Path p = Paths.get(it.toString());
        params.add(printPath.apply(p.toFile()));
      }
    }
    if (toolName != null) {
      params.add("-tn");
      params.add(toolName.toString());
    }
    if (!handcodedPath.isEmpty()) {
      params.add("-hcp");
      // See Note 1
      for (Object it : handcodedPath) {
        Path p = Paths.get(it.toString());
        params.add(printPath.apply(p.toFile()));
      }
    }
    if (!templatePath.isEmpty()) {
      params.add("-fp");
      // See Note 1
      for (Object it : templatePath) {
        Path p = Paths.get(it.toString());
        params.add(printPath.apply(p.toFile()));
      }
    }
    if (!handcodedModelPath.isEmpty()) {
      params.add("-hcg");
      // See Note 1
      for (Object it : handcodedModelPath) {
        Path p = Paths.get(it.toString());
        params.add(printPath.apply(p.toFile()));
      }
    }
    params.add("-dstlGen");
    params.add(Boolean.toString(dstlGen));
    if (configTemplate != null) {
      String cfgTemplateStr = configTemplate.toString();
      params.add("-ct");
      if (cfgTemplateStr.endsWith(".ftl")){
        cfgTemplateStr = cfgTemplateStr.substring(0, cfgTemplateStr.length()-4);
        configTemplate = cfgTemplateStr;
      }
      params.add(cfgTemplateStr);
    }
    if (script != null) {
      params.add("-sc");
      params.add(script.toString());
    }
    if (groovyHook1 != null) {
      params.add("-gh1");
      params.add(groovyHook1.toString());
    }
    if (groovyHook2 != null) {
      params.add("-gh2");
      params.add(groovyHook2.toString());
    }
    if (dev) {
      params.add("-d");
    }
    if (customLog.isPresent()) {
      params.add("-cl");
      params.add(printPath.apply(customLog.get().getAsFile()));
    }
    if (help) {
      params.add("-h");
    }

    return params.toArray(new String[0]);
  }

  public void rebuildGrammar() {
    getLogger().info("out of date: " + grammar.get().getAsFile().getName());
    String[] p = getParameters();

    System.setSecurityManager(new SecurityManager()
    {
      @Override public void checkExit(int status) {
        throw new MCTaskError();
      }
      
      @Override public void checkPermission(Permission perm) {
        // Allow other activities by default
      }
    });
    try {
      // execute Monticore with the given parameters
      MontiCoreTool.main(p);
      MCBasicsMill.globalScope().getSymbolPath().close();
    } catch(MCTaskError e){
      // in case of failure print the error and fail
      String error = Log.getFindings().stream().
              filter(f -> f.getType().equals(Finding.Type.ERROR))
              .map(f -> f.getMsg())
              .collect(Collectors.joining("\n"));
      MCBasicsMill.globalScope().getSymbolPath().close();
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
  public boolean incCheck(String grammar) throws IOException {
    String outAsString = getReportDir().get().getAsFile().toString();
    String grammarWithoutExt = grammar
        .replace(".mc4", "")
        .replace(File.separator, ".")
        .replace('/', '.')
        .toLowerCase();
    File inout = new File(outAsString + File.separator +
          grammarWithoutExt + File.separator +  "/IncGenGradleCheck.txt");
    String base = this.getProject().getProjectDir().toPath().toAbsolutePath().toString();
    return IncChecker.incCheck(inout, grammar, getLogger(), "mc4", base);
  }
/**
   * Returns the path to the TR grammar.
   * Please ensure that the outputDir is previously set
   * @param originalGrammar the original grammar file
   * @return the TR grammar
   */
  public File getTRFile(File originalGrammar) {
    return new File(outputDir.get().getAsFile().toString()
            + "/"
            + DSTLPathUtil.getTRGrammar(
            modelPath.isEmpty() ? List.of(getProject().getLayout().getProjectDirectory().file("src/main/grammars").toString()) : modelPath,
            originalGrammar).toString());
  }
  protected File fromBasePath(String filePath) {
    File file = new File(filePath);
    return !file.isAbsolute()
            ? new File(getProject().getProjectDir(), filePath)
            : file;
  }



  protected void generateBuildInfo() throws IOException {
    File file = buildInfoFile.get().getAsFile();
    file.mkdirs();
    file.delete();
    file.createNewFile();
    FileUtils.writeStringToFile(file, "version = " + getProject().getVersion(), StandardCharsets.UTF_8);
  }

  @Override
  @Internal
  public JsonElement getGradleStatisticData(){
    JsonObject result = new JsonObject();

    {
      JsonArray params = new JsonArray();
      Path cwd = getProject().getProjectDir().toPath().toAbsolutePath();

      String[] usedParams = getParameters(f->{
        try {
          return cwd.relativize(f.toPath()).toString();
        } catch (IllegalArgumentException ignored){ // Can occur, if build is on external harddrive, and `~/.gradle` on internal harddrive
          return f.getPath();
        }
      });
      params.addAll(
          Arrays.stream(usedParams)
              .map(UserJsonString::new)
              .collect(Collectors.toList())
      );
      result.putMember("parameter", params);
    }

    return result;
  }

}
