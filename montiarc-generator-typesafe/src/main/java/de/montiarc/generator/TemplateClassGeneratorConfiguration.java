/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;

/**
 * Configuration of MontiArc generator.
 *
 * @author Robert Heim
 */
public class TemplateClassGeneratorConfiguration implements Configuration {

  public static final String CONFIGURATION_PROPERTY = "_configuration";
  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";
  

  
  /**
   * The names of the specific MontiArc options used in this configuration.
   */
  public enum Options {
    
    MODELPATH("modelPath"), MODELPATH_SHORT("mp"),
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
  
  /**
   * Constructor for {@link TemplateClassGeneratorConfiguration}
   */
  private TemplateClassGeneratorConfiguration(Configuration internal) {
    
    // ConfigurationSystemPropertiesContributor systemPropertiesContributor =
    // ConfigurationSystemPropertiesContributor.withPrefix("montiarc");
    
    this.configuration = ConfigurationContributorChainBuilder.newChain()
        // .add(systemPropertiesContributor)
        .add(DelegatingConfigurationContributor.with(internal))
        .build();
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
  
//  /**
//   * Getter for the list of model path elements (files and directories) stored in this
//   * configuration.
//   * 
//   * @return list of model path files
//   */
//  public List<File> getModelPath() {
//    Optional<List<String>> modelPath = getAsStrings(Options.MODELPATH);
//    if (modelPath.isPresent()) {
//      return toFileList(modelPath.get());
//    }
//    modelPath = getAsStrings(Options.MODELPATH_SHORT);
//    if (modelPath.isPresent()) {
//      return toFileList(modelPath.get());
//    }
//    // default model path is empty
//    return Collections.emptyList();
//  }
  
  public File getModelPath() {
    Optional<String> modelPath = getAsString(Options.MODELPATH);
    if(modelPath.isPresent()){
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    modelPath = getAsString(Options.MODELPATH_SHORT);
    if(modelPath.isPresent()){
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    return null;
  }
  

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "out".
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
    // fallback default is "out"
    return new File(DEFAULT_OUTPUT_DIRECTORY);
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
