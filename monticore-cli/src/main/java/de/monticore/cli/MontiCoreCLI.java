/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import de.monticore.MontiCoreConfiguration;
import de.monticore.MontiCoreScript;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * Command line interface for MontiCore.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public final class MontiCoreCLI {
  
  public static final String LOGBACK_USER_CONFIG = "user.logging.xml";
  
  public static final String LOGBACK_DEVELOPER_CONFIG = "developer.logging.xml";
  
  static final String MC_OUT = "MC_OUT";
  
  static final String LOGBACK_CONFIGURATIONFILE = "logback.configurationFile";
  
  /**
   * Main method.
   * 
   * @param args the CLI arguments
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      // the only required input are the grammar file(s)/directories
      System.out
          .println("MontiCore CLI Usage: java -jar monticore-cli.jar <grammar files> <options>");
      return;
    }
    // check if the input model(s) are specified without option and add it for
    // further processing
    if (!args[0].startsWith("-")) {
      ArrayList<String> fixedArgs = new ArrayList<String>(Arrays.asList(args));
      fixedArgs.add(0, "-" + MontiCoreConfiguration.Options.GRAMMARS_SHORT.toString());
      args = fixedArgs.toArray(new String[fixedArgs.size()]);
    }
    
    CLIArguments arguments = CLIArguments.forArguments(args);
    MontiCoreCLIConfiguration configuration = MontiCoreCLIConfiguration.fromArguments(arguments);
    
    // this will be CLI's default model path if none is specified
    Iterable<String> mp = Arrays.asList("monticore-cli.jar");
    Iterable<String> mpArg = arguments.asMap().get(
        MontiCoreConfiguration.Options.MODELPATH.toString());
    Iterable<String> mpShortArg = arguments.asMap().get(
        MontiCoreConfiguration.Options.MODELPATH_SHORT.toString());
    if ((mpArg == null || Iterables.isEmpty(mpArg))
        && (mpShortArg == null || Iterables.isEmpty(mpShortArg))) {
      // prepare args which contain the fixed model path
      Map<String, Iterable<String>> wrappedArgs = new HashMap<>();
      wrappedArgs.put(MontiCoreConfiguration.Options.MODELPATH.toString(), mp);
      wrappedArgs.putAll(arguments.asMap());
      // use this fixed configuration
      configuration = MontiCoreCLIConfiguration.fromMap(wrappedArgs);
    }
    
    // we store the requested output directory as a system variable such that we
    // can inject it into the logback configuration
    System.setProperty(MC_OUT, configuration.getInternal().getOut().getAbsolutePath());
    
    // this should always happen first in order to use any custom configurations
    if (System.getProperty(LOGBACK_CONFIGURATIONFILE) == null) {
      initLogging(configuration);
    }
    
    // this needs to be called after the statement above; otherwise logback will
    // ignore custom configurations supplied via system property
    Slf4jLog.init();
    
    if (System.getProperty(LOGBACK_CONFIGURATIONFILE) != null) {
      Log.debug(
          "Using system property logback configuration "
              + System.getProperty(LOGBACK_CONFIGURATIONFILE), MontiCoreCLI.class.getName());
    }
    
    // before we launch MontiCore we check if there are any ".mc4" files in the
    // input argument (source path)
    Iterator<Path> inputPaths = configuration.getInternal().getGrammars().getResolvedPaths();
    if (!inputPaths.hasNext()) {
      System.clearProperty(MC_OUT);
      Log.error("0xA1000 There are no \".mc4\" files to parse. Please check the \"grammars\" option.");
      return;
    }
    
    try {
      // since this is the default we load the default script
      ClassLoader l = MontiCoreScript.class.getClassLoader();
      String script = Resources.asCharSource(l.getResource("de/monticore/monticore_emf.groovy"),
          Charset.forName("UTF-8")).read();
      
      // BUT if the user specifies another script to use, we check if it is
      // there and load its content
      if (configuration.getScript().isPresent()) {
        Reporting.reportFileExistenceChecking(Lists.newArrayList(),
            configuration.getScript().get().toPath().toAbsolutePath());
        if (!configuration.getScript().get().exists()) {
          System.clearProperty(MC_OUT);
          Log.error("0xA1001 Custom script \"" + configuration.getScript().get().getPath()
              + "\" not found!");
          return;
        }
        script = Files.toString(configuration.getScript().get(), Charset.forName("UTF-8"));
      }
      
      // execute the scripts (either default or custom)
      new MontiCoreScript().run(script, configuration.getInternal());
    }
    catch (IOException e) {
      System.clearProperty(MC_OUT);
      Log.error("0xA1002 Failed to load Groovy script.", e);
    }
  }
  
  /**
   * Initializes the logging configuration based on the CLI arguments.
   * 
   * @param configuration the MontiCore CLI configuration
   */
  static void initLogging(MontiCoreCLIConfiguration configuration) {
    
    // pick detailed developer logging if specified
    if (configuration.getDev()) {
      useDeveloperLoggingConfiguration();
      return;
    }
    
    if (configuration.getCustomLog().isPresent()) {
      String userFile = configuration.getCustomLog().get();
      // instead of silently failing custom configuration (e.g. not existing
      // configuration file) we test if it is present and fall back to default
      File userLogFile = new File(userFile);
      Reporting.reportFileExistenceChecking(Lists.newArrayList(),
          userLogFile.toPath().toAbsolutePath());
      if (!userLogFile.exists() || !userLogFile.isFile()) {
        // fall back to user configuration
        useUserLoggingConfiguration();
        Log.warn("0xA1030 Failed to load specified custom logback configuration: \"" + userFile
            + "\". Falling back to built-in user logging configuration.");
        return;
      }
      else {
        // apparently the requested file is there, let's try it
        try {
          useLogbackConfiguration(new FileInputStream(userLogFile));
        }
        catch (JoranException | FileNotFoundException e) {
          // fall back to user configuration
          useUserLoggingConfiguration();
          Log.warn("0xA1031 Failed to load specified custom logback configuration: \"" + userFile
              + "\". Falling back to built-in user logging configuration.", e);
        }
        return;
      }
    }
    
    // this is the default
    useUserLoggingConfiguration();
  }
  
  public static void useUserLoggingConfiguration() {
    try {
      useLogbackConfiguration(MontiCoreScript.class.getClassLoader().getResourceAsStream(
          LOGBACK_USER_CONFIG));
    }
    catch (JoranException e) {
      // e.printStackTrace();
      // this should not happen as we use this mechanism for the built-in
      // configurations only (i.e. user.logging.xml and developer.logging.xml)
      System.err.println("Failed to load default logback configuration for users.");
    }
  }
  
  public static void useDeveloperLoggingConfiguration() {
    try {
      useLogbackConfiguration(MontiCoreScript.class.getClassLoader().getResourceAsStream(
          LOGBACK_DEVELOPER_CONFIG));
    }
    catch (JoranException e) {
      // e.printStackTrace();
      // this should not happen as we use this mechanism for the built-in
      // configurations only (i.e. user.logging.xml and developer.logging.xml)
      System.err.println("Failed to load default logback configuration for developers.");
    }
  }
  
  /**
   * Programmatically load and configure logback with the given logback XML input stream.
   * 
   * @param config
   */
  protected static final void useLogbackConfiguration(InputStream config) throws JoranException {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    
    context.reset();
    configurator.doConfigure(config);
  }
  
  private MontiCoreCLI() {
  }
  
}
