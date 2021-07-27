/* (c) https://github.com/MontiCore/monticore */
/*
 *  (c)  https://github.com/MontiCore/monticore
 */

package de.monticore.tf.script;

import com.google.common.collect.Multimap;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DSTLCLIConfiguration implements Configuration {
  
  public enum Options {
    
     SCRIPT("script"),
    SCRIPT_SHORT("s"),
    HELP("help"),
    HELP_SHORT("h");
    
    String name;
    
    Options(String name) {
      this.name = name;
    }
    
    @Override
    public String toString() {
      return this.name;
    }
  }
  
  public static DSTLCLIConfiguration fromArguments(CLIArguments arguments) {
    return new DSTLCLIConfiguration(arguments);
  }
  
  public static DSTLCLIConfiguration fromMap(Multimap<String, String> arguments) {
    return new DSTLCLIConfiguration(arguments);
  }
  
  public static DSTLCLIConfiguration fromMap(Map<String, Iterable<String>> arguments) {
    return new DSTLCLIConfiguration(arguments);
  }
  
  private DSTLConfiguration configuration;
  
  private DSTLCLIConfiguration(CLIArguments arguments) {
    Configuration internal = ConfigurationPropertiesMapContributor.fromSplitMap(arguments.asMap());
    this.configuration = DSTLConfiguration.withConfiguration(internal);
  }
  
  private DSTLCLIConfiguration(Multimap<String, String> arguments) {
    Configuration internal = ConfigurationPropertiesMapContributor.fromSplitMap(arguments);
    this.configuration = DSTLConfiguration.withConfiguration(internal);
  }
  
  private DSTLCLIConfiguration(Map<String, Iterable<String>> arguments) {
    Configuration internal = ConfigurationPropertiesMapContributor.fromSplitMap(arguments);
    this.configuration = DSTLConfiguration.withConfiguration(internal);
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
  public DSTLConfiguration getInternal() {
    return this.configuration;
  }
}
