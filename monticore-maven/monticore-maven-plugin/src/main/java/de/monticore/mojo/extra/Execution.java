/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo.extra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * Represents the execution of an executable (e.g., a shell script). The result
 * is stored in a file named like the executable and located in the supplied
 * output directory.
 * 
 * @author (last commit) $Author: ahorst $
 * 2013) $
 */
public class Execution {
  
  /* The Maven log instance. */
  Log log;
  
  /* The file to store the output of the executable to. */
  File outputDirectory;
  
  /* The executable (e.g., a shell script) to execute with this execution. */
  Executable executable;
  
  /**
   * Constructor for de.monticore.mojo.extra.Execution
   * 
   * @param executable to execute
   * @param outputDirectory to store the output of the executable in
   * @param log for logging errors occurred during the execution
   */
  public Execution(Executable executable, File outputDirectory, Log log) {
    if (executable == null) {
      throw new IllegalArgumentException("0xA4084 Executable must not be null!");
    }
    if (outputDirectory == null) {
      throw new IllegalArgumentException("0xA4085 Output directory must not be null!");
    }
    if (log == null) {
      throw new IllegalArgumentException("0xA4086 Log must not be null!");
    }
    this.executable = executable;
    this.outputDirectory = outputDirectory;
    this.log = log;
  }
  
  /**
   * Executes the given executable of this execution and
   * 
   * @return the file containing the output of the executable.
   */
  protected File execute() {
    List<String> execution = Lists.newArrayList();
    execution.add(this.executable.getPathToExecutable().getPath());
    execution.addAll(this.executable.getArguments());
    
    ProcessBuilder processBuilder = new ProcessBuilder(execution);
    File result = new File(this.outputDirectory, Files
        .getNameWithoutExtension(this.executable.getPathToExecutable().getName()));
    processBuilder.redirectErrorStream(true);
    
    FileOutputStream resultWriter = null;
    
    try {
      Process process = processBuilder.start();
      
      resultWriter = new FileOutputStream(result);
      InputStream processOutput = process.getInputStream();
      int read = processOutput.read();
      while (read != -1) {
        resultWriter.write(read);
        read = processOutput.read();
      }
      
      if (process.waitFor() != 0) {
        this.log.error("0xA4060 Execution of script " + this.executable.getPathToExecutable().getName()
            + " returned "
            + process.exitValue());
      }
    }
    catch (IOException e) {
      this.log.warn(e);
    }
    catch (InterruptedException e) {
      this.log.error(e);
    }
    finally {
      try {
        if (resultWriter != null) {
          resultWriter.close();
        }
      }
      catch (IOException e) {
        // this would be bad
        this.log.error(e);
      }
    }
    return result;
  }
  
}
