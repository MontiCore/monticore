/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo.extra;

import java.io.File;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Represents an executable (e.g., a shell script) and the arguments being
 * passed to it; for configuration in POM.
 * 
 * @author (last commit) $Author: ahorst $
 * 2014) $
 */
public class Executable {
  
  /* The actual executable (e.g., shell script) file. */
  File pathToExecutable;
  
  /* The arguments to pass to the executable. */
  List<String> arguments = Lists.newArrayList();
  
  /**
   * Constructor for de.monticore.mojo.extra.Executable
   */
  public Executable() {
  }
  
  /**
   * Constructor for de.monticore.mojo.extra.Executable
   * 
   * @param pathToExecutable the actual executable file
   */
  public Executable(File pathToExecutable) {
    this.setPathToExecutable(pathToExecutable);
  }
  
  /**
   * Constructor for de.monticore.mojo.extra.Executable
   * 
   * @param pathToExecutable the actual executable file
   * @param arguments the arguments to pass to the executable
   */
  public Executable(File pathToExecutable, List<String> arguments) {
    this(pathToExecutable);
    this.setArguments(arguments);
  }
  
  /**
   * @return the actual executable file
   */
  public File getPathToExecutable() {
    return this.pathToExecutable;
  }
  
  /**
   * @return the arguments to pass to the executable
   */
  public List<String> getArguments() {
    return ImmutableList.copyOf(this.arguments);
  }
  
  /**
   * Setter for the actual executable file (e.g., a shell script).
   * 
   * @param pathToExecutable must not be null
   */
  void setPathToExecutable(File pathToExecutable) {
    if (pathToExecutable == null) {
      throw new IllegalArgumentException("0xA4082 Path to executable must not be null!");
    }
    this.pathToExecutable = pathToExecutable;
  }
  
  /**
   * Setter for the arguments to pass to the executable.
   * 
   * @param arguments must not be null
   */
  void setArguments(List<String> arguments) {
    if (arguments == null) {
      throw new IllegalArgumentException("0xA4083 Arguments must not be null!");
    }
    this.arguments = arguments;
  }
  
  @Override
  public String toString() {
    String result = "Executable: ".concat(this.pathToExecutable.getAbsolutePath()).concat(" ");
    result = result.concat("Arguments: ");
    for (String arg : this.arguments) {
      result = result.concat(arg).concat(" ");
    }
    return result;
  }
  
}
