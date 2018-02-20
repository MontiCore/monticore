/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cli;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Multimap;

import de.monticore.MontiCoreConfiguration;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;

public final class MontiCoreCLIConfiguration implements Configuration {
  
  public enum Options {
    
    DEV("dev"), DEV_SHORT("d"), CUSTOMLOG("customLog"), CUSTOMLOG_SHORT("cl"), SCRIPT("script"),
    SCRIPT_SHORT("s"), HELP("help"), HELP_SHORT("h");
    
    String name;
    
    Options(String name) {
      this.name = name;
    }
    
    @Override
    public String toString() {
      return this.name;
    }
  }
  
  public static MontiCoreCLIConfiguration fromArguments(CLIArguments arguments) {
    return new MontiCoreCLIConfiguration(arguments);
  }
  
  public static MontiCoreCLIConfiguration fromMap(Multimap<String, String> arguments) {
    return new MontiCoreCLIConfiguration(arguments);
  }
  
  public static MontiCoreCLIConfiguration fromMap(Map<String, Iterable<String>> arguments) {
    return new MontiCoreCLIConfiguration(arguments);
  }
  
  private MontiCoreConfiguration configuration;
  
  private MontiCoreCLIConfiguration(CLIArguments arguments) {
    Configuration internal = ConfigurationPropertiesMapContributor.fromSplitMap(arguments.asMap());
    this.configuration = MontiCoreConfiguration.withConfiguration(internal);
  }
  
  private MontiCoreCLIConfiguration(Multimap<String, String> arguments) {
    Configuration internal = ConfigurationPropertiesMapContributor.fromSplitMap(arguments);
    this.configuration = MontiCoreConfiguration.withConfiguration(internal);
  }
  
  private MontiCoreCLIConfiguration(Map<String, Iterable<String>> arguments) {
    Configuration internal = ConfigurationPropertiesMapContributor.fromSplitMap(arguments);
    this.configuration = MontiCoreConfiguration.withConfiguration(internal);
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
   * Getter for the value of the script option.
   * 
   * @return the Groovy script file to execute
   */
  public Optional<String> getScript() {
    if (getAsString(Options.SCRIPT).isPresent()) {
      return Optional.of(getAsString(Options.SCRIPT).get());
    }
    if (getAsString(Options.SCRIPT_SHORT).isPresent()) {
      return Optional.of(getAsString(Options.SCRIPT_SHORT).get());
    }
    return Optional.empty();
  }
  
  /**
   * Getter for the value of the development logging option.
   * 
   * @return whether the development logging configuration should be used
   */
  public boolean getDev() {
    return hasProperty(Options.DEV) || hasProperty(Options.DEV_SHORT);
  }
  
  /**
   * Getter for the custom log configuration option value.
   * 
   * @return the file name of the custom log configuration to use, if set
   */
  public Optional<String> getCustomLog() {
    if (getAsString(Options.CUSTOMLOG).isPresent()) {
      return getAsString(Options.CUSTOMLOG);
    }
    if (getAsString(Options.CUSTOMLOG_SHORT).isPresent()) {
      return getAsString(Options.CUSTOMLOG_SHORT);
    }
    return Optional.empty();
  }
  
  /**
   * @return whether the given key is contained in this configuration
   */
  public boolean hasProperty(String key) {    
    return this.configuration.hasProperty(key);
  }
  
  /**
   * @return whether the given key is contained in this configuration
   */
  public boolean hasProperty(Enum<?> key) {
    return hasProperty(key.toString());
  }
  
  /**
   * Provides access to the internally used MontiCoreConfiguration.
   * 
   * @return
   */
  MontiCoreConfiguration getInternal() {
    return this.configuration;
  }
  
}
