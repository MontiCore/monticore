/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.gen;

import de.monticore.MontiCoreConfiguration;
import de.monticore.gradle.AMontiCoreConfiguration;
import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCSingleFileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This task is provided by the MontiCore generator gradle plugin
 * and invokes the MontiCore generator.
 * It consumes a collection of grammars (by default from src/main/grammars).
 *
 */
@CacheableTask // this task produces reproducible and relocatable output
public abstract class MCGenTask extends MCSingleFileTask {


  public MCGenTask() {
    // Do not specify a constant symbol path configuration (instead set it dynamically via the plugin)
    super("MCGenTask", null);

    // hand-written java code - used by the TOP decorator, etc.
    setIfPathExists(x -> this.getHandWrittenCodeDir().from(x), Path.of(
            getProject().getProjectDir().getAbsolutePath(), "src", "main", "java"));
    // hand-written grammars - used by the grammar TOP decorator
    setIfPathExists(x -> this.getHandWrittenGrammarDir().from(x), Path.of(
            getProject().getProjectDir().getAbsolutePath(), "src", "main", "grammarsHC"));
    // Grammars are loaded from this path (cf. symbol path)
    setIfPathExists(x -> this.getModelPath().from(x), Path.of(
            getProject().getProjectDir().getAbsolutePath(), "src", "main", "grammars"));
    // Input files to this task
    setIfPathExists(x -> this.getGrammar().from(x), Path.of(
            getProject().getProjectDir().getAbsolutePath(), "src", "main", "grammars"));
    // Templates (such as the config templates) are loaded from there
    setIfPathExists(this.getTmplDir()::set, Path.of(
            getProject().getProjectDir().getAbsolutePath(), "src", "main", "resources"));

    // Gradle does not support a default value for ConfigurableFileCollection
    // Thus, we have to set a default value this way (and possibly, override it via setFrom)
  }

  @Input
  @Optional
  public abstract Property<Boolean> getGenDST();

  @Input
  @Optional
  public abstract Property<Boolean> getGenTag();

  @Input
  @Optional
  public abstract Property<String> getCustomLog();

  /**
   * Groovy script that is hooked into the workflow of the standard script at hook point one, which is called after initialization, before the actual workflow begins
   */
  @InputFile
  @PathSensitive(PathSensitivity.RELATIVE)
  @Optional
  public abstract RegularFileProperty getGroovyHook1();

  /**
   * Groovy script that is hooked into the workflow of the standard script at hook point two, which is called before the generation step.
   */
  @InputFile
  @PathSensitive(PathSensitivity.RELATIVE)
  @Optional
  public abstract RegularFileProperty getGroovyHook2();


  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".mc4");
  }


  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> args = super.createArgList(filePath, handlePath);

    // genDST
    args.add("-" + MontiCoreConfiguration.GENDST_LONG);
    args.add(Boolean.toString(getGenDST().getOrElse(false)));

    // genTag
    args.add("-" + MontiCoreConfiguration.GENTAG_LONG);
    args.add(Boolean.toString(getGenTag().getOrElse(false)));

    if (getCustomLog().isPresent()) {
      args.add("-" + MontiCoreConfiguration.CUSTOMLOG);
      args.add(this.getCustomLog().get());
    }

    // script
    if (this.getScript().isPresent()) {
      args.add("-" + MontiCoreConfiguration.SCRIPT);
      args.add(this.getScript().get());
    }


    // groovyHook1
    if (this.getGroovyHook1().isPresent()) {
      args.add("-" + MontiCoreConfiguration.GROOVYHOOK1);
      args.add(this.getGroovyHook1().get().getAsFile().getAbsolutePath());
    }

    // groovyHook2
    if (this.getGroovyHook2().isPresent()) {
      args.add("-" + MontiCoreConfiguration.GROOVYHOOK2);
      args.add(this.getGroovyHook2().get().getAsFile().getAbsolutePath());
    }

    return args;
  }

  @Override
  protected String getHandWrittenCodeOptionString() {
    return AMontiCoreConfiguration.HANDCODEDPATH;
  }

  @Override
  protected String getInputOptionString() {
    return MontiCoreConfiguration.GRAMMAR;
  }

  @Override
  protected File getIncGenFile(File inputFile) {
    assert (isInputFile(inputFile));

    // The MontiCore gradle reporter outputs the files in a package-like directory

    // We match based on a wildcard, because the generator will save the reports
    // in a location based on the package name defined within the grammar.
    // The package *should* be the same as the folder structure of the grammar,
    // but there is no guarantee for this
    Set<File> files = getReportDirOfFile(inputFile)
            .getAsFileTree().matching(ft -> ft.include("**/IncGenGradleCheck.txt"))
            .getFiles();
    if (files.size() > 1) throw new IllegalStateException("files " + files);
    // we assume
    return files.isEmpty() ? super.getIncGenFile(inputFile) : files.iterator().next();
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return MCToolInvoker::run;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return MCToolAction.class;
  }

  /**
   * Utility shortcut to access the generated TR grammars
   */
  @Internal
  public FileTree getTROutput() {
    return this.getOutputs().getFiles().getAsFileTree()
            .matching(patternFilterable -> patternFilterable.include( "**/*TR.mc4"));
  }

  /**
   * Utility shortcut to access the generated TagDefinition grammars
   */
  @Internal
  public FileTree getTagDefOutput() {
    return this.getOutputs().getFiles().getAsFileTree()
            .matching(patternFilterable -> patternFilterable.include( "**/*TagDefinition.mc4"));
  }

  /**
   * Utility shortcut to access the generated TagSchema grammars
   */
  @Internal
  public FileTree getTagSchemaOutput() {
    return this.getOutputs().getFiles().getAsFileTree()
            .matching(patternFilterable -> patternFilterable.include( "**/*TagSchema.mc4"));
  }

  // Alias for the MC Generator: The inputs are also known as grammars
  @Internal
  public ConfigurableFileCollection getGrammar() {
    return super.getInput();
  }

  public void setGrammar(File f) {
    input.from(f);
  }

  public void setGrammar(Iterable<File> f) {
    input.from(f);
  }

  public void setGrammar(Object... paths) {
    input.from(paths);
  }

  // Alias for the MC Generator:
  // The model path of the MC generator is technically similar enough to
  // a symbol path, that we can re-use its mechanisms for the model
  // Note: Semantically they are not equivalent
  @Internal
  public ConfigurableFileCollection getModelPath() {
    return this.getIncrementalSymbolPath();
  }

  // Note: setModelPath()/modelpath() now override all previous value as a breaking change
  // (It used to add to the previous value)
  // (Due to ConfigurableFileCollection not having a convention method in our Gradle version, this is required)
  // As this is a breaking change, we log an error in case it is used multiple times
  protected boolean isModelPathAlreadySet = false;

  public void setModelPath(File f) {
    if (isModelPathAlreadySet)
      getLogger().error("The task " + getName() + " sets the modelPath multiple times. This is no longer supported");
    getModelPath().setFrom(f);
    isModelPathAlreadySet = true;
  }

  public void setModelPath(Iterable<File> f) {
    if (isModelPathAlreadySet)
      getLogger().error("The task " + getName() + " sets the modelPath multiple times. This is no longer supported");

    getModelPath().setFrom(f);
    isModelPathAlreadySet = true;
  }

  public void setModelPath(Object... paths) {
    if (isModelPathAlreadySet)
      getLogger().error("The task " + getName() + " sets the modelPath multiple times. This is no longer supported");
    getModelPath().setFrom(paths);
    isModelPathAlreadySet = true;
  }

  public void modelPath(Object... paths) {
    setModelPath(paths);
  }

  protected boolean isHandcodedPathAlreadySet = false;


  public void setHandCodedPath(Object... paths) {
    if (isHandcodedPathAlreadySet)
      getLogger().error("The task " + getName() + " sets the handcodedPath multiple times. This is no longer supported");
    getHandWrittenCodeDir().setFrom(paths);
    isHandcodedPathAlreadySet = true;
  }

  public void handcodedPath(Object... paths) {
    setHandCodedPath(paths);
  }


  public void setTemplatePath(String path) {
    this.getTmplDir().set(getProject().file(path));
  }

}
