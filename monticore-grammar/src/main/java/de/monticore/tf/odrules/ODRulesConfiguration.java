/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import com.google.common.collect.Sets;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides access to the aggregated configuration of a MontiCore instance
 * derived from (1) its command line arguments, and (2) system properties (not
 * implemented yet).
 *
 */
//TODO: Use the default CLI
public final class ODRulesConfiguration implements Configuration {

  public static final String MC4_EXTENSION = "mc4";

  public static final String MODEL_EXTENSION = "mtod";

  public static final String JAVA_EXTENSION = "java";

  public static final String FTL_EXTENSION = "ftl";

  public static final Set<String> MC4_EXTENSIONS = Sets.newHashSet(MC4_EXTENSION);

  public static final Set<String> MODEL_EXTENSIONS = Sets.newHashSet(MODEL_EXTENSION);

  public static final Set<String> HWC_EXTENSIONS = Sets.newHashSet(JAVA_EXTENSION);

  public static final Set<String> FTL_EXTENSIONS = Sets.newHashSet(FTL_EXTENSION);

  public static final String CONFIGURATION_PROPERTY = "_configuration";

  public static final String DEFAULT_OUTPUT_DIRECTORY = "target/generated-sources";

  public static final String DEFAULT_REPORT_DIRECTORY = "target/reports";

  /**
   * The names of the specific ODRules options used in this configuration.
   */
  public enum Options {

    MODEL("models"), MODEL_SHORT("m"), GRAMMAR("grammar"), GRAMMARS_SHORT("g"), MODELPATH("modelPath"), MODELPATH_SHORT("mp"),
    OUT("out"), OUT_SHORT("o"),REPORT("report"), REPORT_SHORT("r"), TARGETPATH("targetPath"), TARGETPATH_SHORT("tp"), NODEFACTORY("nodeFactory"),
    TEMPLATEPATH("templatePath"), TEMPLATEPATH_SHORT("fp");

    String name;

    Options(String name) {
      this.name = name;
    }

    /**
     * @see Enum#toString()
     */
    @Override
    public String toString() {
      return this.name;
    }

  }

  @Override
  public boolean hasProperty(String key) {
    return this.configuration.hasProperty(key);
  }

  public boolean hasProperty(Enum<?> key) {
    return hasProperty(key.toString());
  }

  /**
   * Factory method for {@link ODRulesConfiguration}.
   */
  public static ODRulesConfiguration withConfiguration(Configuration configuration) {
    return new ODRulesConfiguration(configuration);
  }

  private final Configuration configuration;

  /**
   * Constructor for {@link ODRulesConfiguration}
   */
  private ODRulesConfiguration(Configuration internal) {

    // ConfigurationSystemPropertiesContributor systemPropertiesContributor =
    // ConfigurationSystemPropertiesContributor.withPrefix("monticore");

    this.configuration = ConfigurationContributorChainBuilder.newChain()
        // .add(systemPropertiesContributor)
        .add(DelegatingConfigurationContributor.with(internal))
        .build();

  }

  /**
   * @see Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }

  /**
   * @see Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }

  /**
   * @see Configuration#getAsBoolean(String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {
    return this.configuration.getAsBoolean(key);
  }

  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }

  /**
   * @see Configuration#getAsBooleans(String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {
    return this.configuration.getAsBooleans(key);
  }

  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }

  /**
   * @see Configuration#getAsDouble(String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    return this.configuration.getAsDouble(key);
  }

  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }

  /**
   * @see Configuration#getAsDoubles(String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    return this.configuration.getAsDoubles(key);
  }

  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }

  /**
   * @see Configuration#getAsInteger(String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    return this.configuration.getAsInteger(key);
  }

  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }

  /**
   * @see Configuration#getAsIntegers(String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    return this.configuration.getAsIntegers(key);
  }

  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }

  /**
   * @see Configuration#getAsString(String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    return this.configuration.getAsString(key);
  }

  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }

  /**
   * @see Configuration#getAsStrings(String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    return this.configuration.getAsStrings(key);
  }

  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }

  /**
   * @see Configuration#getValue(String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    return this.configuration.getValue(key);
  }

  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }

  /**
   * @see Configuration#getValues(String)
   */
  @Override
  public Optional<List<Object>> getValues(String key) {
    return this.configuration.getValues(key);
  }

  public Optional<List<Object>> getValues(Enum<?> key) {
    return getValues(key.toString());
  }


  public MCPath getModels() {
    Optional<List<String>> models = getAsStrings(Options.MODEL);
    if (models.isPresent()) {
      return new MCPath(toPathList(models.get()));
    }
    models = getAsStrings(Options.MODEL_SHORT);
    if (models.isPresent()) {
      return new MCPath(toPathList(models.get()));
    }
    // no default; must specify grammar files/directories to process
    Log.error("0xF0003 Please specify the model file(s).");
    return new MCPath();
  }


  public List<String> getModelsAsStrings() {
    Optional<List<String>> models = getAsStrings(Options.MODEL);
    if (models.isPresent()) {
      return models.get();
    }
    models = getAsStrings(Options.MODEL_SHORT);
    if (models.isPresent()) {
      return models.get();
    }
    // no default; must specify grammar files/directories to process
    Log.error("0xF0005 Please specify the model file(s).");
    return Collections.emptyList();
  }

  /**
   * Getter for the list of model path elements (files and directories) stored
   * in this configuration.
   *
   * @return list of model path files
   */
  public MCPath getModelPath() {
  Optional<MCPath> modelPath = getAsStrings(Options.MODELPATH)
      .map(this::convertEntryNamesToModelPath);
  if (modelPath.isPresent()) {
    return modelPath.get();
  }
  modelPath = getAsStrings(Options.MODELPATH_SHORT).map(this::convertEntryNamesToModelPath);
  if (modelPath.isPresent()) {
    return modelPath.get();
  }
  // default model path is empty (but contains the output directory by
  // default and src/main/grammars)
  return new MCPath(Paths.get("src/main/grammars"));
}

  private MCPath convertEntryNamesToModelPath(List<String> modelPathEntryNames) {
    List<File> modelPathFiles = toFileList(modelPathEntryNames);
    List<Path> modelPathEntries = modelPathFiles.stream()
        .map(File::toPath)
        .map(Path::toAbsolutePath)
        .collect(Collectors.toList());
    return new MCPath(modelPathEntries);
  }

  public List<String> getModelPathAsStrings() {
    Optional<List<String>> modelPath = getAsStrings(Options.MODELPATH);
    if (modelPath.isPresent()) {
      List<String> result = new ArrayList<>(modelPath.get());
      return result;
    }
    modelPath = getAsStrings(Options.MODELPATH_SHORT);
    if (modelPath.isPresent()) {
      List<String> result = new ArrayList<>(modelPath.get());
      return result;
    }
    // default model path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "monticore/sourcecode".
   *
   * @return output directory file
   */
  public File getOut() {
    Optional<String> out = getAsString(Options.OUT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    out = getAsString(Options.OUT_SHORT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    // fallback default is "monticore/sourcecode"
    return new File(DEFAULT_OUTPUT_DIRECTORY);
  }

  /**
   * Getter for the report directory stored in this configuration. A fallback
   * default is "target/reports".
   *
   * @return output directory file
   */
  public File getReport() {
    Optional<String> report = getAsString(Options.REPORT);
    if (report.isPresent()) {
      return new File(report.get());
    }
    report = getAsString(Options.REPORT_SHORT);
    if (report.isPresent()) {
      return new File(report.get());
    }
    // fallback default is "monticore/sourcecode"
    return new File(DEFAULT_REPORT_DIRECTORY);
  }

  /**
   * Getter for the target path directories stored in this configuration.
   *
   * @return iterable target files
   */
  public MCPath getTargetPath() {
    Optional<List<String>> targetPath = getAsStrings(Options.TARGETPATH);
    if (targetPath.isPresent()) {
      return new MCPath(toPathList(targetPath.get()));
    }
    targetPath = getAsStrings(Options.TARGETPATH_SHORT);
    if (targetPath.isPresent()) {
      return new MCPath(toPathList(targetPath.get()));
    }
    // default target path is empty
    return new MCPath();
  }

  /**
   * Getter for the actual value of the target path argument. This is not the
   * prepared {@link MCPath} as in
   *
   * @return
   */
  public List<String> getTargetPathAsStrings() {
    Optional<List<String>> targetPath = getAsStrings(Options.TARGETPATH);
    if (targetPath.isPresent()) {
      return targetPath.get();
    }
    targetPath = getAsStrings(Options.TARGETPATH_SHORT);
    if (targetPath.isPresent()) {
      return targetPath.get();
    }
    // default target path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the target path directories stored in this configuration.
   *
   * @return iterable template files
   */
  public MCPath getTemplatePath() {
    Optional<List<String>> templatePath = getAsStrings(Options.TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return new MCPath(toPathList(templatePath.get()));
    }
    templatePath = getAsStrings(Options.TEMPLATEPATH_SHORT);
    if (templatePath.isPresent()) {
      return new MCPath(toPathList(templatePath.get()));
    }
    // default template path is empty
    return new MCPath();
  }

  /**
   * Getter for the actual value of the template path argument. This is not the
   * prepared {@link MCPath} as in
   * arguments.
   *
   * @return
   */
  public List<String> getTemplatePathAsStrings() {
    Optional<List<String>> templatePath = getAsStrings(Options.TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    templatePath = getAsStrings(Options.TEMPLATEPATH_SHORT);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    // default template path is empty
    return Collections.emptyList();
  }

  public String getNodeFactory() {
    Optional<List<String>> templatePath = getAsStrings(Options.NODEFACTORY);
    if (templatePath.isPresent()) {
      return templatePath.get().get(0);
    }
    // default template path is empty
    return "";
  }

  public String getGrammar() {
    Optional<List<String>> templatePath = getAsStrings(Options.GRAMMAR);
    if (templatePath.isPresent()) {
      return templatePath.get().get(0);
    }
    // default template path is empty
    return "";
  }

  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().collect(Collectors.mapping(file -> new File(file), Collectors.toList()));
  }

  protected static List<Path> toPathList(List<String> files) {
    return files.stream().collect(Collectors.mapping(file -> Paths.get(file), Collectors.toList()));
  }

}
