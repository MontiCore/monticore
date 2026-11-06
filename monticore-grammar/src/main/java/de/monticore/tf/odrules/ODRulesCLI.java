/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.paths.MCPath;
import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules._parser.ODRulesParser;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Generates transformations from object diagram rules ({@code .mtod} files).
 * <p>
 * Note that this tool must not call System.exit,
 * as it is executed within the Gradle process by MontiTransExec.
 */
public class ODRulesCLI {

  static final String LOG_ID = "ODRules";

  static final String MODEL_FILE_EXTENSION = ".mtod";

  static final String DEFAULT_OUTPUT_DIR = "out";

  /**
   * FreeMarker caches parsed templates within its configuration. Sharing the engine avoids
   * re-parsing the templates for each rule, also across successive main calls within the same
   * classloader (as done by the MontiTransExec Gradle task). The configuration of the engine
   * only depends on the classloader, so sharing it is safe.
   */
  private static FreeMarkerTemplateEngine sharedTemplateEngine;

  /**
   * Main method.
   *
   * @param args the CLI arguments
   */
  public static void main(String[] args) {
    Log.ensureInitialization();
    new ODRulesCLI().run(args);
  }

  protected void run(String[] args) {

    Options options = initOptions();

    CommandLine cmd;
    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliParser = new DefaultParser();
      cmd = cliParser.parse(options, args);
    } catch (ParseException e) {
      // an unexpected error from the Apache CLI parser:
      Log.error("0xA6151 Could not process CLI parameters: " + e.getMessage());
      return;
    }

    // help: when --help
    if (cmd.hasOption("h")) {
      printHelp(options);
      // do not continue, when help is printed
      return;
    }

    if (!cmd.hasOption("i")) {
      Log.error("0xA1000 There is no \".mtod\" file to parse. Please check the \"input\" option.");
      // do not continue, when this error is logged
      return;
    }

    List<Path> models = collectModelFiles(cmd.getOptionValues("i"));
    if (models.isEmpty()) {
      Log.error("0xA6159 No \".mtod\" files found in " + Arrays.toString(cmd.getOptionValues("i")));
      return;
    }

    Log.debug("----- Starting ODRules Generation -----", LOG_ID);
    Log.debug("Input files  : " + models, LOG_ID);
    Log.debug("Output dir   : " + cmd.getOptionValue("o", DEFAULT_OUTPUT_DIR), LOG_ID);

    // one setup for all models
    GeneratorSetup setup = createGeneratorSetup(cmd);
    for (Path model : models) {
      processModel(model, setup);
    }
  }

  /**
   * Runs the whole pipeline (parse, create symbol table, generate) for a single model.
   * No code is generated for erroneous models.
   *
   * @return true iff the model has been generated without errors
   */
  public boolean processModel(Path model, GeneratorSetup setup) {
    long errorsBefore = Log.getErrorCount();
    try {
      ODRulesMill.reset();
      ODRulesMill.init();

      // Parse OD-Rules
      Optional<ASTODRule> astRule = parseODRule(model);
      if (astRule.isEmpty() || Log.getErrorCount() > errorsBefore) {
        return false;
      }

      ODRulesMill.scopesGenitorDelegator().createFromAST(astRule.get());

      generate(astRule.get(), setup, deriveRuleName(model));
      return Log.getErrorCount() == errorsBefore;
    } finally {
      ODRulesMill.reset();
      ODRuleGenerationMill.reset();
    }
  }

  /**
   * Expands directories to the .mtod files they contain (sorted, for a deterministic order)
   * and removes duplicates.
   */
  protected List<Path> collectModelFiles(String[] inputs) {
    Set<Path> result = new LinkedHashSet<>();
    for (String input : inputs) {
      Path path = Paths.get(input);
      if (Files.isDirectory(path)) {
        try (Stream<Path> files = Files.walk(path)) {
          files.filter(Files::isRegularFile)
              .filter(f -> f.getFileName().toString().endsWith(MODEL_FILE_EXTENSION))
              .sorted()
              .forEach(result::add);
        } catch (IOException e) {
          Log.error("0xA6160 Could not read the directory " + path + ": " + e.getMessage(), e);
        }
      } else {
        result.add(path);
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * Creates the generator setup, which is shared by all models of a run.
   */
  protected GeneratorSetup createGeneratorSetup(CommandLine cmd) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(Paths.get(cmd.getOptionValue("o", DEFAULT_OUTPUT_DIR)).toFile());
    if (cmd.hasOption("hwc")) {
      List<Path> hwcPaths = new ArrayList<>();
      for (String hwc : cmd.getOptionValues("hwc")) {
        hwcPaths.add(Paths.get(hwc));
      }
      setup.setHandcodedPath(new MCPath(hwcPaths));
    }
    shareTemplateEngine(setup);
    return setup;
  }

  /**
   * The first setup creates the template engine, all later setups reuse it.
   */
  protected static void shareTemplateEngine(GeneratorSetup setup) {
    if (sharedTemplateEngine == null) {
      sharedTemplateEngine = setup.getFreeMarkerTemplateEngine();
    } else {
      setup.setFreeMarkerTemplateEngine(sharedTemplateEngine);
    }
  }

  public Optional<ASTODRule> parseODRule(Path model) {
    Log.debug("Start parsing of the model " + model, LOG_ID);
    ODRulesParser parser = ODRulesMill.parser();
    Optional<ASTODRule> ast;
    try {
      ast = parser.parse(model.toString());
    } catch (IOException e) {
      Log.error("0xA6161 Could not read the model " + model + ": " + e.getMessage(), e);
      return Optional.empty();
    }
    if (parser.hasErrors() || ast.isEmpty()) {
      Log.error("0xA6162 There are parsing errors while parsing of the model " + model);
      return Optional.empty();
    }
    Log.debug("Model " + model + " parsed successfully", LOG_ID);
    return ast;
  }

  /**
   * The rule (and thus the generated class) is named after the file, without its extension.
   */
  protected String deriveRuleName(Path model) {
    String fileName = model.getFileName().toString();
    return fileName.endsWith(MODEL_FILE_EXTENSION)
        ? fileName.substring(0, fileName.length() - MODEL_FILE_EXTENSION.length())
        : fileName;
  }

  public void generate(ASTODRule ast, GeneratorSetup setup, String filename) {
    Log.debug("Generate Transformation for " + filename, LOG_ID);
    ast.setName(filename);
    initODRuleGenerationMill();
    ODRuleCodeGenerator.generate(ast, setup);
  }

  public void generate(ASTODRule ast, GlobalExtensionManagement glex, File outputDirectory, String filename) {
    Log.debug("Generate Transformation for " + filename, LOG_ID);
    ast.setName(filename);
    initODRuleGenerationMill();
    ODRuleCodeGenerator.generate(ast, glex, outputDirectory, filename);
  }

  /**
   * The code generator builds its intermediate model with the ODRuleGenerationMill.
   */
  protected void initODRuleGenerationMill() {
    ODRuleGenerationMill.reset();
    ODRuleGenerationMill.init();
  }

  /**
   * Initializes the available CLI options for the ODRules tool.
   *
   * @return The CLI options with arguments.
   */
  protected Options initOptions() {
    Options options = new Options();

    // parse input models
    options.addOption(Option.builder("i")
        .longOpt("input")
        .argName("files")
        .hasArgs()
        .desc("Processes the given \".mtod\" model(s) and triggers the transformation generation. "
            + "Directories are searched recursively for \".mtod\" files.")
        .get());

    // specify custom output directory
    options.addOption(Option.builder("o")
        .longOpt("out")
        .argName("path")
        .hasArg()
        .desc("Output directory for all generated artifacts (default: \"" + DEFAULT_OUTPUT_DIR + "\").")
        .get());

    // hwc
    options.addOption(Option.builder("hwc")
        .longOpt("handwrittencode")
        .argName("hwcpath")
        .hasArgs()
        .desc("Where handwritten classes are located.")
        .get());

    // help dialog
    options.addOption(Option.builder("h")
        .longOpt("help")
        .desc("Prints this help dialog")
        .get());

    return options;
  }

  protected void printHelp(Options options) {
    HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
    try {
      formatter.printHelp(this.getClass().getSimpleName(), "", options, "", true);
    } catch (IOException e) {
      // If the help-output could not be written to the help appendable
      Log.error("0xA6163 Could not print the help dialog: " + e.getMessage(), e);
    }
  }

}
