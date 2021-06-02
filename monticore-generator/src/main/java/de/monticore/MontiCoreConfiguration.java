/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.transform;

/**
 * Provides access to the aggregated configuration of a MontiCore instance
 * derived from (1) its command line arguments, and (2) system properties (not
 * implemented yet).
 */
public final class MontiCoreConfiguration implements Configuration {

  public static final String MC4_EXTENSION = "mc4";

  public static final String JAVA_EXTENSION = "java";

  public static final String FTL_EXTENSION = "ftl";

  public static final Set<String> MC4_EXTENSIONS = Sets.newHashSet(MC4_EXTENSION);

  public static final Set<String> HWC_EXTENSIONS = Sets.newHashSet(JAVA_EXTENSION);

  public static final Set<String> FTL_EXTENSIONS = Sets.newHashSet(FTL_EXTENSION);

  public static final String CONFIGURATION_PROPERTY = "_configuration";

  public static final String DEFAULT_OUTPUT_PATH = "out";
  
  public static final String DEFAULT_HANDCODED_JAVA_PATH = "java";

  public static final String DEFAULT_REPORT_PATH = "reports";

  public static final String DEFAULT_HANDCODED_TEMPLATE_PATH = "resource";

  public static final String DEFAULT_GRAMMAR_PATH = "grammars";


  /**
   * Constants for the allowed CLI options in their long and short froms.
   * Stored in constants as they are used multiple times in MontiCore.
   */
  public static final String GRAMMAR = "g";
  public static final String OUT = "o";
  public static final String MODELPATH = "mp";
  public static final String HANDCODEDPATH = "hcp";
  public static final String SCRIPT = "sc";
  public static final String GROOVYHOOK1 = "gh1";
  public static final String GROOVYHOOK2 = "gh2";
  public static final String TEMPLATEPATH = "fp";
  public static final String CONFIGTEMPLATE = "ct";
  public static final String DEV = "d";
  public static final String CUSTOMLOG = "cl";
  public static final String REPORT = "r";
  public static final String HELP = "h";

  public static final String GRAMMAR_LONG = "grammar";
  public static final String OUT_LONG = "out";
  public static final String MODELPATH_LONG = "modelPath";
  public static final String HANDCODEDPATH_LONG = "handcodedPath";
  public static final String SCRIPT_LONG = "script";
  public static final String GROOVYHOOK1_LONG = "groovyHook1";
  public static final String GROOVYHOOK2_LONG = "groovyHook2";
  public static final String TEMPLATEPATH_LONG = "templatePath";
  public static final String CONFIGTEMPLATE_LONG = "configTemplate";
  public static final String DEV_LONG = "dev";
  public static final String CUSTOMLOG_LONG = "customLog";
  public static final String REPORT_LONG = "report";
  public static final String HELP_LONG = "help";

  /**
   * Factory method for {@link MontiCoreConfiguration}.
   */
  public static MontiCoreConfiguration withConfiguration(Configuration configuration) {
    return new MontiCoreConfiguration(((MontiCoreConfiguration) configuration).getOptions());
  }

  /**
   * Factory method for {@link MontiCoreConfiguration}.
   */
  public static MontiCoreConfiguration withOptions(CommandLine options) {
    return new MontiCoreConfiguration(options);
  }

  private final Configuration configuration;
  private final CommandLine options;

  /**
   * Constructor for {@link MontiCoreConfiguration}
   */
  private MontiCoreConfiguration(Configuration internal) {

    // ConfigurationSystemPropertiesContributor systemPropertiesContributor =
    // ConfigurationSystemPropertiesContributor.withPrefix("monticore");

    this.configuration = ConfigurationContributorChainBuilder.newChain()
        // .add(systemPropertiesContributor)
        .add(DelegatingConfigurationContributor.with(internal))
        .build();
    this.options = null;

  }

  public MontiCoreConfiguration(CommandLine options) {
    this.options = options;
    this.configuration = null;
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    // ToDo
    return this.configuration.getAllValues();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    // ToDo
    return this.configuration.getAllValuesAsStrings();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBoolean(java.lang.String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {

    String property = null;
    if (options.hasOption(key)) {
      property = options.getOptionValue(key);
    }
    return property != null
            ? Optional.ofNullable(Boolean.valueOf(property))
            : Optional.empty();

    //return this.configuration.getAsBoolean(key);
  }

  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBooleans(java.lang.String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {

    List<String> properties = new ArrayList<>();
    if (options.hasOption(key)) {
      properties = Arrays.asList(options.getOptionValues(key));
    }

    return Optional.ofNullable(properties.stream()
            .map(p -> Boolean.valueOf(p))
            .collect(Collectors.toList()));

//    return this.configuration.getAsBooleans(key);
  }

  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDouble(java.lang.String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    String property = null;
    if (options.hasOption(key)) {
      property = options.getOptionValue(key);
    }
    return property != null
            ? Optional.ofNullable(Doubles.tryParse(property))
            : Optional.empty();

    //return this.configuration.getAsDouble(key);
  }

  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDoubles(java.lang.String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    List<String> properties = new ArrayList<>();
    if (options.hasOption(key)) {
      properties = Arrays.asList(options.getOptionValues(key));
    }

    return Optional.ofNullable(properties.stream()
            .map(p -> Doubles.tryParse(p))
            .collect(Collectors.toList()));

    //return this.configuration.getAsDoubles(key);
  }

  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsInteger(java.lang.String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    String property = null;
    if (options.hasOption(key)) {
      property = options.getOptionValue(key);
    }

    return property != null
            ? Optional.of(Ints.tryParse(property))
            : Optional.empty();
    //return this.configuration.getAsInteger(key);
  }

  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsIntegers(java.lang.String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    List<String> properties = new ArrayList<>();
    if (options.hasOption(key)) {
      properties = Arrays.asList(options.getOptionValues(key));
    }

    return Optional.ofNullable(properties.stream()
            .map(p -> Ints.tryParse(p))
            .collect(Collectors.toList()));
    //return this.configuration.getAsIntegers(key);
  }

  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsString(java.lang.String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    if (options.hasOption(key)) {
      return Optional.ofNullable(options.getOptionValue(key));
    }
    return Optional.empty();
    // return this.configuration.getAsString(key);
  }

  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsStrings(java.lang.String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    if (options.hasOption(key)) {
      return Optional.ofNullable(Arrays.asList(options.getOptionValues(key)));
    }
    return Optional.empty();
    // return this.configuration.getAsStrings(key);
  }

  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValue(java.lang.String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    if (options.hasOption(key)) {
      return Optional.ofNullable(options.getOptionValue(key));
    }
    return Optional.empty();
    // return this.configuration.getValue(key);
  }

  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValues(java.lang.String)
   */
  @Override
  public Optional<List<Object>> getValues(String key) {
    if (options.hasOption(key)) {
      return Optional.ofNullable(Arrays.asList(options.getOptionValues(key)));
    }
    return Optional.empty();
    // return this.configuration.getValues(key);
  }

  public Optional<List<Object>> getValues(Enum<?> key) {
    return getValues(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#hasProperty(java.lang.String)
   */
  @Override
  public boolean hasProperty(String key) {
    return options.hasOption(key);
    // return this.configuration.hasProperty(key);
  }

  public boolean hasProperty(Enum<?> key) {
    return hasProperty(key.toString());
  }

  private boolean checkPath(List<String> grammars) {
    for (String g: grammars) {
      Path p = Paths.get(g);
      if (!Files.exists(p)) {
        Log.error("0xA1019 The requested path " + p.toString() + " does not exist.");
        return false;
      }
    }
    return true;
  }
  /**
   * Getter for the {@link IterablePath} consisting of grammar files stored in
   * this configuration.
   *
   * @return iterable grammar files
   */
  public IterablePath getGrammars() {
    Optional<List<String>> grammars = getAsStrings(GRAMMAR);
    if (grammars.isPresent() && checkPath(grammars.get())) {
      return IterablePath.from(toFileList(grammars.get()), MC4_EXTENSIONS);
    }
    // no default; must specify grammar files/directories to process
    Log.error("0xA1013 Please specify the grammar file(s).");
    return IterablePath.empty();
  }

  /**
   * Getter for the actual value of the grammar argument. This is not the
   * prepared {@link IterablePath} as in
   * {@link MontiCoreConfiguration#getGrammars()} but the raw input arguments.
   *
   * @return
   */
  public List<String> getGrammarsAsStrings() {
    Optional<List<String>> grammars = getAsStrings(GRAMMAR);
    if (grammars.isPresent()) {
      return grammars.get();
    }
    // no default; must specify grammar files/directories to process
    Log.error("0xA1014 Please specify the grammar file(s).");
    return Collections.emptyList();
  }

  /**
   * Getter for the list of model path elements (files and directories) stored
   * in this configuration.
   *
   * @return list of model path files
   */
  public ModelPath getModelPath() {
    Optional<ModelPath> modelPath = getAsStrings(MODELPATH)
        .map(this::convertEntryNamesToModelPath);
    if (modelPath.isPresent()) {
      return modelPath.get();
    }
    // default model path is empty
    return new ModelPath();
  }

  private ModelPath convertEntryNamesToModelPath(List<String> modelPathEntryNames) {
    List<File> modelPathFiles = toFileList(modelPathEntryNames);
    List<Path> modelPathEntries = modelPathFiles.stream()
        .map(File::toPath)
        .map(Path::toAbsolutePath)
        .collect(Collectors.toList());
    return new ModelPath(modelPathEntries);
  }

  /**
   * Getter for the actual value of the model path argument. This is not the
   * prepared {@link ModelPath} as in
   * {@link MontiCoreConfiguration#getModelPath()} but the raw input arguments.
   *
   * @return
   */
  public List<String> getModelPathAsStrings() {
    Optional<List<String>> modelPath = getAsStrings(MODELPATH);
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
    Optional<String> out = getAsString(OUT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    // fallback default is "out"
    return new File(DEFAULT_OUTPUT_PATH);
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "monticore/sourcecode".
   *
   * @return output directory file
   */
  public File getReport() {
    Optional<String> report = getAsString(REPORT);
    if (report.isPresent()) {
      return new File(report.get());
    }
    // fallback default is "out/report"
    return new File(getOut(), DEFAULT_REPORT_PATH);
  }

  /**
   * Getter for the handcoded path directories stored in this configuration.
   *
   * @return iterable handcoded files
   */
  public IterablePath getHandcodedPath() {
    Optional<List<String>> handcodedPath = getAsStrings(HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return IterablePath.from(toFileList(handcodedPath.get()), HWC_EXTENSIONS);
    }
    // default handcoded path is "java"
    File defaultFile = new File(DEFAULT_HANDCODED_JAVA_PATH);
    if (!defaultFile.exists()) {
      return IterablePath.empty();
    }
    return IterablePath.from(new File(DEFAULT_HANDCODED_JAVA_PATH), HWC_EXTENSIONS);
  }

  /**
   * Getter for the actual value of the handcoded path argument. This is not the
   * prepared {@link IterablePath} as in
   * {@link MontiCoreConfiguration#getHandcodedPath()} but the raw input
   * arguments.
   *
   * @return
   */
  public List<String> getHandcodedPathAsStrings() {
    Optional<List<String>> handcodedPath = getAsStrings(HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return handcodedPath.get();
    }
    // default handcoded path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the target path directories stored in this configuration.
   *
   * @return iterable template files
   */
  public IterablePath getTemplatePath() {
    Optional<List<String>> templatePath = getAsStrings(TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return IterablePath.from(toFileList(templatePath.get()), FTL_EXTENSIONS);
    }
    // default handcoded template path is "resource"
    File defaultFile = new File(DEFAULT_HANDCODED_TEMPLATE_PATH);
    if (!defaultFile.exists()) {
      return IterablePath.empty();
    }
    return IterablePath.from(new File(DEFAULT_HANDCODED_TEMPLATE_PATH), FTL_EXTENSIONS);
  }

  /**
   * Getter for the optional config template.
   *
   * @return Optional of the config template
   */
  public Optional<String> getConfigTemplate() {
    Optional<String> configTemplate = getAsString(CONFIGTEMPLATE);
    if (configTemplate.isPresent()) {
      return configTemplate;
    }
    return Optional.empty();
  }

  /**
   * Getter for the optional groovy script for hook point one.
   *
   * @return Optional path to the script
   */
  public Optional<String> getGroovyHook1() {
    Optional<String> script = getAsString(GROOVYHOOK1);
    if (script.isPresent()) {
      return script;
    }
    return Optional.empty();
  }

  /**
   * Getter for the optional groovy script for hook point two.
   *
   * @return Optional path to the script
   */
  public Optional<String> getGroovyHook2() {
    Optional<String> script = getAsString(GROOVYHOOK2);
    if (script.isPresent()) {
      return script;
    }
    return Optional.empty();
  }

  /**
   * Getter for the actual value of the template path argument. This is not the
   * prepared {@link IterablePath} as in
   * {@link MontiCoreConfiguration#getTemplatePath()} but the raw input
   * arguments.
   *
   * @return
   */
  public List<String> getTemplatePathAsStrings() {
    Optional<List<String>> templatePath = getAsStrings(TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    // default template path is empty
    return Collections.emptyList();
  }

  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().collect(
        Collectors.mapping(file -> new File(file).getAbsoluteFile(), Collectors.toList()));
  }

  public CommandLine getOptions() {
    return options;
  }
}
