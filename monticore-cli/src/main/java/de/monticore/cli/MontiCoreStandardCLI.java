/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import de.monticore.MontiCoreConfiguration;
import de.monticore.MontiCoreScript;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * Command line interface for the MontiCore generator engine. Defines, handles,
 * and executes the corresponding command line options and arguments, such as
 * --help
 */
public class MontiCoreStandardCLI {
  
  /**
   * Dedicated XML file that configures the logging for users (i.e. users of 
   * the MontiCore tool) --> standard
   */
  public static final String LOGBACK_USER_CONFIG = "user.logging.xml";
  
  /**
   * Dedicated XML file that configures the logging for MontiCore developers
   * (i.e. developers of the MontiCore tool) --> selectable via -dev This
   * logging config prints many internal messages
   */
  public static final String LOGBACK_DEVELOPER_CONFIG = "developer.logging.xml";
  
  /**
   * System variable to load already defined logging configuration.
   */
  static final String LOGBACK_CONFIGURATIONFILE = "logback.configurationFile";
  
  /*=================================================================*/
  /* Part 1: Handling the arguments and options
  /*=================================================================*/
  
  /**
   * Main method that is called from command line and runs the MontiCore tool.
   * 
   * @param args The input parameters for configuring the MontiCore tool
   */
  public static void main(String[] args) {
    Log.init();
    Grammar_WithConceptsMill.init();
    new MontiCoreStandardCLI().run(args);
  }
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   * 
   * @param args The input parameters for configuring the MontiCore tool.
   */
  public void run(String[] args) {
  
    Options options = initOptions();
  
    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);
      
      // help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        // do not continue, when help is printed
        return;
      }
  
      // if -g input is missing: log error and stop
      if (!cmd.hasOption("g")) {
        Log.error("0xA7150 There are no \".mc4\" files to parse. Please check the \"grammars\" option.");
        // do not continue, when this error is logged
        return;
      }
      
      // -options developer and custom logging
      initLogging(cmd);
      
      // put arguments into configuration
      CLIArguments arguments = CLIArguments.forArguments(args);
      MontiCoreConfiguration configuration = MontiCoreConfiguration.withConfiguration(
          ConfigurationPropertiesMapContributor.fromSplitMap(arguments.asMap()));
      
      // load custom or default script
      String script = loadScript(cmd);
      
      // execute MontiCore with loaded script and configuration
      new MontiCoreScript().run(script, configuration);
      
    } catch (ParseException e) {
      // an unexpected error from the Apache CLI parser:
      Log.error("0xA7151 Could not process CLI parameters: " + e.getMessage());
    }
    
  }

  /*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param options The input parameters and options.
   */
  protected void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("MontiCoreCLI", options);
  }
  
  /**
   * Initializes logging based on the input options and system configurations.
   * If the system has already a configured logback configuration file, it is
   * used. Otherwise, it is checked whether the use specified to user developer
   * logging or passed a custom logback configuration file. As a default, the
   * standard user logging is used.
   * 
   * @param cmd The command line instance containing the input options
   */
  protected void initLogging(CommandLine cmd) {
    // Case 1: Logging if already configured by system configuration
    if (System.getProperty(LOGBACK_CONFIGURATIONFILE) != null) {
      Log.debug("Using system property logback configuration " 
          + System.getProperty(LOGBACK_CONFIGURATIONFILE), MontiCoreStandardCLI.class.getName());
    }
    
    // Case 2: Use developer logging if configured
    else if (cmd.hasOption("d")) {
      useLogbackConfiguration(LOGBACK_DEVELOPER_CONFIG);
    }
    
    // Case 3: Use custom log file logging if configured and present
    else if (cmd.hasOption("cl")) {
      File userLogFile = new File(cmd.getOptionValue("cl", StringUtils.EMPTY));
      Reporting.reportFileExistenceChecking(Lists.newArrayList(), 
          userLogFile.toPath().toAbsolutePath());
        
      if (userLogFile.exists() && userLogFile.isFile()) {
        try {
          useLogbackConfiguration(new FileInputStream(userLogFile));
        } catch (FileNotFoundException | JoranException e) {
          // instead of silently failing custom configuration (e.g. not existing
          // configuration file) we test if it is present and fall back to default
          Log.warn("0xA1054 Failed to load specified custom logback configuration: \"" 
              + userLogFile.getAbsolutePath() 
              + "\". Falling back to built-in user logging configuration.");
        }
      }
    }
    
    // Case 4: Use user logging if previous option not applicable (default)
    else {
      useLogbackConfiguration(LOGBACK_USER_CONFIG);
    }
    
    // this needs to be called after the statement above; otherwise logback will
    // ignore custom configurations supplied via system property
    Slf4jLog.init();
  }
  
  /**
   * Programmatically load and configure logback with the given logback path.
   * 
   * @param path The input path for the configuration file
   */
  protected void useLogbackConfiguration(String path) {
    try {
      useLogbackConfiguration(MontiCoreScript.class.getClassLoader().getResourceAsStream(path));
    }
    catch (JoranException e) {
      // this should not happen as we use this mechanism for the built-in
      // configurations (i.e. user.logging.xml and developer.logging.xml) 
      Log.error("0xA1055 Failed to load default logback configuration " + path + ".");
    }
  }
  
  /**
   * Programmatically load and configure logback with the given logback XML
   * input stream.
   *
   * @param config The input stream for the configuration file
   */
  protected final void useLogbackConfiguration(InputStream config) throws JoranException {
    ILoggerFactory lf = LoggerFactory.getILoggerFactory();
    if(lf instanceof LoggerContext) {
      LoggerContext context = (LoggerContext) lf;
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
  
      context.reset();
      configurator.doConfigure(config);
    }
  }
  
  /**
   * Loads the custom groovy script if specified and present. Otherwise, loads
   * the default script for the generator engine.
   * 
   * @param cmd The command line instance containing the input options
   * @return The groovy configuration script as String
   */
  private String loadScript(CommandLine cmd) {
    String script = StringUtils.EMPTY;
    try {
      // if the user specifies a custom script to use, we check if it is
      // available and load its content
      if (cmd.hasOption("sc")) {
        File f = new File(cmd.getOptionValue("sc", StringUtils.EMPTY));
        Reporting.reportFileExistenceChecking(Lists.newArrayList(),
            f.toPath().toAbsolutePath());
        
        if (f.exists() && f.isFile()) {
          script = Files.asCharSource(f, Charset.forName("UTF-8")).read();
        } else {
          Log.error("0xA1056 Custom script \"" + f.getAbsolutePath() + "\" not found!");
        }
      } else {
        // otherwise, we load the default script
        ClassLoader l = MontiCoreScript.class.getClassLoader();
        script = Resources.asCharSource(l.getResource("de/monticore/monticore_noemf.groovy"),
            Charset.forName("UTF-8")).read();
      }
    }
    catch (IOException e) {
      Log.error("0xA1057 Failed to load Groovy script.", e);
    }
    return script;
  }

  /*=================================================================*/
  /* Part 3: Defining the options incl. help-texts
  /*=================================================================*/

  /**
   * Initializes the available CLI options for the MontiCore tool.
   * 
   * @return The CLI options with arguments.
   */
  protected Options initOptions() {
    Options options = new Options();
    
    // parse input grammars
    options.addOption(Option.builder("g")
        .longOpt("grammar")
        .argName("filelist")
        .hasArgs()
        .desc("Processes the source grammars (mandatory) and triggers the MontiCore generation.")
        .build());
    
    // specify custom output directory
    options.addOption(Option.builder("o")
        .longOpt("out")
        .argName("path")
        .hasArg()
        .desc("Optional output directory for all generated artifacts.")
        .build());
    
    // specify model path
    options.addOption(Option.builder("mp")
        .longOpt("modelPath")
        .argName("pathlist")
        .hasArgs()
        .desc("Optional list of directories or files to be included for importing other grammars.")
        .build());
    
    // specify hand-written artifacts
    options.addOption(Option.builder("hcp")
        .longOpt("handcodedPath")
        .argName("pathlist")
        .hasArgs()
        .desc("Optional list of directories to look for handwritten code to integrate.")
        .build());
    
    // specify custom output
    options.addOption(Option.builder("sc")
        .longOpt("script")
        .argName("file.groovy")
        .hasArg()
        .desc("Optional Groovy script to control the generation workflow.")
        .build());
    
    // specify template path
    options.addOption(Option.builder("fp")
        .longOpt("templatePath")
        .argName("pathlist")
        .hasArgs()
        .desc("Optional list of directories to look for handwritten templates to integrate.")
        .build());
    
    // developer level logging
    options.addOption(Option.builder("d")
        .longOpt("dev")
        .desc("Specifies whether developer level logging should be used (default is false)")
        .build());
    
    // change logback conf
    options.addOption(Option.builder("cl")
        .longOpt("customLog")
        .argName("file.xml")
        .hasArg()
        .desc("Changes the logback configuration to a customized file.")
        .build());
    
    // specify report path
    options.addOption(Option.builder("r")
        .longOpt("report")
        .argName("path")
        .hasArg(true)
        .desc("Specifies the directory for printing reports based on the given MontiCore grammars.")
        .build());
    
    // help dialog
    options.addOption(Option.builder("h")
        .longOpt("help")
        .desc("Prints this help dialog")
        .build());
    
    return options;
  }
}
