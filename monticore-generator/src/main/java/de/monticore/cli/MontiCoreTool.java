/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cli;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.monticore.MontiCoreConfiguration;
import de.monticore.MontiCoreScript;
import de.monticore.StatisticsHandlerFix;
import de.monticore.cli.updateChecker.UpdateCheckerRunnable;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static de.monticore.MontiCoreConfiguration.*;

/**
 * Command line interface for the MontiCore generator engine. Defines, handles,
 * and executes the corresponding command line options and arguments, such as
 * --help
 */
public class MontiCoreTool {

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
    new MontiCoreTool().run(args);
    StatisticsHandlerFix.shutdown();
  }

  /**
   * Method that can be called to load some of the required classes for the tool.
   */
  public static void preLoad() {
    Grammar_WithConceptsMill.init();
    Grammar_WithConceptsMill.traverser();
  }

  /**
   * Starts a thread to check whether a newer version of monticore
   * was released.
   */
  protected void runUpdateCheck() {
    Runnable updateCheckerRunnable = new UpdateCheckerRunnable();
    Thread updateCheckerThread = new Thread(updateCheckerRunnable);
    updateCheckerThread.start();
  }
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   * 
   * @param args The input parameters for configuring the MontiCore tool.
   */
  public void run(String[] args) {

    runUpdateCheck();
  
    Options options = initOptions();
  
    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      
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

      MontiCoreConfiguration configuration = MontiCoreConfiguration.withCLI(cmd);
      
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
          + System.getProperty(LOGBACK_CONFIGURATIONFILE), MontiCoreTool.class.getName());
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
      Log.error("0xA1055 Failed to load default logback configuration " + path + ".", e);
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
  protected String loadScript(CommandLine cmd) {
    String script = StringUtils.EMPTY;
    try {
      // if the user specifies a custom script to use, we check if it is
      // available and load its content
      if (cmd.hasOption(SCRIPT)) {
        File f = new File(cmd.getOptionValue(SCRIPT, StringUtils.EMPTY));
        Reporting.reportFileExistenceChecking(Lists.newArrayList(),
            f.toPath().toAbsolutePath());

        if (f.exists() && f.isFile()) {
          script = Files.asCharSource(f, StandardCharsets.UTF_8).read();
        } else {
          ClassLoader l = MontiCoreScript.class.getClassLoader();
          if (l.getResource(cmd.getOptionValue(SCRIPT, StringUtils.EMPTY)) != null) {
            script = Resources.asCharSource(l.getResource(cmd.getOptionValue(SCRIPT, StringUtils.EMPTY)),
                    StandardCharsets.UTF_8).read();
          } else {
            Log.error("0xA1056 Custom script \"" + f.getAbsolutePath() + "\" not found!");
          }
        }
      } else {
        // otherwise, we load the default script
        ClassLoader l = MontiCoreScript.class.getClassLoader();
        script = Resources.asCharSource(l.getResource("de/monticore/monticore_standard.groovy"),
            StandardCharsets.UTF_8).read();
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
    options.addOption(Option.builder(GRAMMAR)
        .longOpt(GRAMMAR_LONG)
        .argName("filelist")
        .hasArgs()
        .desc("Processes the source grammars (mandatory) and triggers the MontiCore generation.")
        .build());
    
    // specify custom output directory
    options.addOption(Option.builder(OUT)
        .longOpt(OUT_LONG)
        .argName("path")
        .hasArg()
        .desc("Optional output directory for all generated artifacts.")
        .build());

    // specify the tool jar's name
    options.addOption(Option.builder(TOOL_JAR_NAME)
        .longOpt(TOOL_JAR_NAME_LONG)
        .argName("name")
        .hasArg()
        .desc("Optional tool jar name used in generated launch scripts.")
        .build());

    // specify model path
    options.addOption(Option.builder(MODELPATH)
        .longOpt(MODELPATH_LONG)
        .argName("pathlist")
        .hasArgs()
        .desc("Optional list of directories or files to be included for importing other grammars.")
        .build());
    
    // specify hand-written artifacts
    options.addOption(Option.builder(HANDCODEDPATH)
        .longOpt(HANDCODEDPATH_LONG)
        .argName("pathlist")
        .hasArgs()
        .desc("Optional list of directories to look for handwritten code to integrate.")
        .build());

    // specify hand-written model path
    options.addOption(Option.builder(HANDCODEDMODELPATH)
            .longOpt(HANDCODEDMODELPATH_LONG)
            .argName("pathlist")
            .hasArgs()
            .desc("Optional list of directories or files to be included for importing other TR grammars.")
            .build());
    
    // specify custom output
    options.addOption(Option.builder(SCRIPT)
        .longOpt(SCRIPT_LONG)
        .argName("file.groovy")
        .hasArg()
        .desc("Optional Groovy script to control the generation workflow.")
        .build());

    // specify custom script for hook point one
    options.addOption(Option.builder(GROOVYHOOK1)
        .longOpt(GROOVYHOOK1_LONG)
        .argName("file.groovy")
        .hasArg()
        .desc("Optional Groovy script that is hooked into the workflow of the standard script at hook point one.")
        .build());

    // specify custom script for hook point two
    options.addOption(Option.builder(GROOVYHOOK2)
        .longOpt(GROOVYHOOK2_LONG)
        .argName("file.groovy")
        .hasArg()
        .desc("Optional Groovy script that is hooked into the workflow of the standard script at hook point two.")
        .build());

    // specify template path
    options.addOption(Option.builder(TEMPLATEPATH)
        .longOpt(TEMPLATEPATH_LONG)
        .argName("pathlist")
        .hasArgs()
        .desc("Optional list of directories to look for handwritten templates to integrate.")
        .build());

    // specify template config
    options.addOption(Option.builder(CONFIGTEMPLATE)
        .longOpt(CONFIGTEMPLATE_LONG)
        .argName("config.ftl")
        .hasArg()
        .desc("Optional template to configure the integration of handwritten templates.")
        .build());
    
    // developer level logging
    options.addOption(Option.builder(DEV)
        .longOpt(DEV_LONG)
        .desc("Specifies whether developer level logging should be used (default is false)")
        .build());
    
    // change logback conf
    options.addOption(Option.builder(CUSTOMLOG)
        .longOpt(CUSTOMLOG_LONG)
        .argName("file.xml")
        .hasArg()
        .desc("Changes the logback configuration to a customized file.")
        .build());
    
    // specify report path
    options.addOption(Option.builder(REPORT)
        .longOpt(REPORT_LONG)
        .argName("path")
        .hasArg(true)
        .desc("Specifies the directory for printing reports based on the given MontiCore grammars.")
        .build());

    // Base Path for Relative Reporter Output
    options.addOption(Option.builder(REPORT_BASE)
        .longOpt(REPORT_BASE_LONG)
        .argName("path")
        .hasArg(true)
        .desc("Base path for paths printed in the Reports.")
        .build());

    // toggle dstl generation
    options.addOption(Option.builder(GENDST_LONG)
            .argName("boolean")
            .hasArg(true)
            .desc("Specifies if transformation infrastructure should be generated for the given TR grammar.")
            .build());

    // toggle tagging generation
    options.addOption(Option.builder(GENTAG_LONG)
            .argName("boolean")
            .hasArg(true)
            .desc("Specifies if tagging infrastructure should be generated for the given tagging grammar.")
            .build());

    // help dialog
    options.addOption(Option.builder(HELP)
        .longOpt(HELP_LONG)
        .desc("Prints this help dialog")
        .build());
    
    return options;
  }
}
