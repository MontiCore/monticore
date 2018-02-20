/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.templateclassgenerator.codegen.TemplateClassGeneratorConstants;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;

/**
 * Configuration of TemplateClass Generator.
 *
 * @author Jerome Pfeiffer
 */
public class TemplateClassGeneratorConfiguration implements Configuration {
  
  public static final String CONFIGURATION_PROPERTY = "_configuration";
  
  /**
   * The names of the specific MontiArc options used in this configuration.
   */
  public enum Options {
    
    TEMPLATEPATH("templatepath"), TEMPLATEPATH_SHORT("tp"),
    OUT("out"), OUT_SHORT("o");
    
    String name;
    
    Options(String name) {
      this.name = name;
    }
    
    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return this.name;
    }
    
  }
  
  private final Configuration configuration;
  
  /**
   * Factory method for {@link TemplateClassGeneratorConfiguration}.
   */
  public static TemplateClassGeneratorConfiguration withConfiguration(Configuration configuration) {
    return new TemplateClassGeneratorConfiguration(configuration);
  }
  
  public static TemplateClassGeneratorConfiguration fromArguments(CLIArguments arguments) {
    return new TemplateClassGeneratorConfiguration(arguments);
  }
  
  /**
   * Constructor for {@link TemplateClassGeneratorConfiguration}
   */
  private TemplateClassGeneratorConfiguration(Configuration internal) {
    this.configuration = ConfigurationContributorChainBuilder.newChain()
        .add(DelegatingConfigurationContributor.with(internal))
        .build();
  }
  
  private TemplateClassGeneratorConfiguration(CLIArguments arguments) {
    Configuration internal = ConfigurationPropertiesMapContributor.fromSplitMap(arguments.asMap());
    this.configuration = TemplateClassGeneratorConfiguration.withConfiguration(internal);
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBoolean(java.lang.String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {
    return this.configuration.getAsBoolean(key);
  }
  
  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBooleans(java.lang.String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {
    return this.configuration.getAsBooleans(key);
  }
  
  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDouble(java.lang.String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    return this.configuration.getAsDouble(key);
  }
  
  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDoubles(java.lang.String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    return this.configuration.getAsDoubles(key);
  }
  
  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsInteger(java.lang.String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    return this.configuration.getAsInteger(key);
  }
  
  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsIntegers(java.lang.String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    return this.configuration.getAsIntegers(key);
  }
  
  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsString(java.lang.String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    return this.configuration.getAsString(key);
  }
  
  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsStrings(java.lang.String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    return this.configuration.getAsStrings(key);
  }
  
  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValue(java.lang.String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    return this.configuration.getValue(key);
  }
  
  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValues(java.lang.String)
   */
  @Override
  public Optional<List<Object>> getValues(String key) {
    return this.configuration.getValues(key);
  }
  
  public Optional<List<Object>> getValues(Enum<?> key) {
    return getValues(key.toString());
  }
  
  /**
   * Getter for the template path stored in this configuration. A fallback
   * default is looked up in {@link TemplateClassGeneratorConstants}
   * 
   * @return template path as File
   */
  public File getTemplatePath() {
    Optional<String> modelPath = getAsString(Options.TEMPLATEPATH);
    if (modelPath.isPresent()) {
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    modelPath = getAsString(Options.TEMPLATEPATH_SHORT);
    if (modelPath.isPresent()) {
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    
    return Paths.get(TemplateClassGeneratorConstants.DEFAULT_TEMPLATEPATH).toAbsolutePath()
        .toFile();
  }
  
  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is looked up in {@link TemplateClassGeneratorConstants}.
   * 
   * @return output directory File
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
    // fallback default is "gen"
    return Paths.get(TemplateClassGeneratorConstants.DEFAULT_OUTPUT_FOLDER).toAbsolutePath()
        .toFile();
  }
  
  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().collect(Collectors.mapping(file -> new File(file), Collectors.toList()));
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#hasProperty(java.lang.String)
   */
  @Override
  public boolean hasProperty(String key) {
    return this.configuration.hasProperty(key);
  }
}
