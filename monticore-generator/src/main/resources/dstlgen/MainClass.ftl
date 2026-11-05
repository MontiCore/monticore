<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "fileExtension", "dstlName", "package", "grammarName", "grammarPackage")}
package ${package};

import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.monticore.io.paths.MCPath;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrules.ODRuleCodeGenerator;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules._symboltable.ODRulesScopesGenitorDelegator;
import de.monticore.tf.rule2od.Variable2AttributeMap;
import de.monticore.tf.ruletranslation.Rule2ODState;
import de.monticore.tf.runtime.matching.ModelTraversal;
import de.monticore.tf.runtime.matching.ModelTraversalFactory;

import ${package}.${dstlName?lower_case}.${dstlName}Mill;
import ${package}.${dstlName?lower_case}._ast.AST${grammarName}TFRule;
import ${package}.${dstlName?lower_case}._cocos.TransCoCos;
import ${package}.${dstlName?lower_case}._parser.${dstlName}Parser;
import ${package}.${dstlName?lower_case}._visitor.${dstlName}Traverser;
import ${package}.translation.${grammarName}Rule2OD;
import ${package}.translation.${grammarName}RuleCollectVariables;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.cli.help.TextHelpAppendable;

<#assign service = glex.getGlobalVar("service")>

public class ${className} {

  static final String LOG_ID = "${dstlName}";

  static final String RULE_FILE_EXTENSION = ".mtr";

  static final String DEFAULT_OUTPUT_DIR = "out";

  /**
   * FreeMarker caches parsed templates within its configuration. Sharing the engine avoids
   * re-parsing the templates for each rule, also across successive main calls within the same
   * classloader (as done by the MontiTransExec Gradle task). The configuration of the engine
   * only depends on the classloader, so sharing it is safe.
   */
  private static FreeMarkerTemplateEngine sharedTemplateEngine;

  public static void main(String[] args) {
    Log.ensureInitialization();
    new ${grammarName}TFGenTool().run(args);
  }

  /**
   * Processes the CLI arguments. Note that this method must not call System.exit,
   * as the tool is executed within the Gradle process by MontiTransExec.
   */
  protected void run(String[] args){

    Options options = initOptions();

    CommandLine cmd;
    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliParser = new DefaultParser();
      cmd = cliParser.parse(options, args);
    } catch (ParseException e) {
      // an unexpected error from the Apache CLI parser:
      Log.error("0xA6153${service.getGeneratedErrorCode(classname)} Could not process CLI parameters: " + e.getMessage());
      return;
    }

    // help: when --help
    if (cmd.hasOption("h")) {
      printHelp(options);
      // do not continue, when help is printed
      return;
    }

    if (!cmd.hasOption("i")) {
      Log.error(
          "0xA1001${service.getGeneratedErrorCode(classname)} There is no \".mtr\" file to parse. Please check the \"input\" option.");
      // do not continue, when this error is logged
      return;
    }

    List<Path> rules = collectRuleFiles(cmd.getOptionValues("i"));
    if (rules.isEmpty()) {
      Log.error("0xA6155${service.getGeneratedErrorCode(classname)} No \".mtr\" files found in " + Arrays.toString(cmd.getOptionValues("i")));
      return;
    }

    Log.debug("----- Starting Transformation Generation -----", LOG_ID);
    Log.debug("Input files  : " + rules, LOG_ID);
    Log.debug("Output dir   : " + cmd.getOptionValue("o", DEFAULT_OUTPUT_DIR), LOG_ID);

    // one setup for all rules
    GeneratorSetup setup = createGeneratorSetup(cmd);
    for (Path rule : rules) {
      processRule(rule, setup);
    }
  }

  /**
   * Runs the whole pipeline (parse, check, translate, generate) for a single rule.
   * No code is generated for erroneous rules.
   *
   * @return true iff the rule has been generated without errors
   */
  public boolean processRule(Path model, GeneratorSetup setup) {
    long errorsBefore = Log.getErrorCount();
    try {
      ${dstlName}Mill.reset();
      ${dstlName}Mill.init();

      // Parse rule
      Optional<AST${grammarName}TFRule> rule = parseRule(model);
      if (rule.isEmpty() || Log.getErrorCount() > errorsBefore) {
        return false;
      }

      // check CoCos
      checkCoCos(rule.get());
      if (Log.getErrorCount() > errorsBefore) {
        Log.error("0xA6154${service.getGeneratedErrorCode(classname)} Skipped the generation for " + model
            + " due to context condition violations.");
        return false;
      }

      //create od rule
      ASTODRule odrule = createODRule(rule.get());
      // generate
      generate(odrule, setup);
      return Log.getErrorCount() == errorsBefore;
    } finally {
      ${dstlName}Mill.reset();
      ODRulesMill.reset();
      ODRuleGenerationMill.reset();
    }
  }

  /**
   * Expands directories to the .mtr files they contain (sorted, for a deterministic order)
   * and removes duplicates.
   */
  protected List<Path> collectRuleFiles(String[] inputs) {
    Set<Path> result = new LinkedHashSet<>();
    for (String input : inputs) {
      Path path = Paths.get(input);
      if (Files.isDirectory(path)) {
        try (Stream<Path> files = Files.walk(path)) {
          files.filter(Files::isRegularFile)
              .filter(f -> f.getFileName().toString().endsWith(RULE_FILE_EXTENSION))
              .sorted()
              .forEach(result::add);
        } catch (IOException e) {
          Log.error("0xA6157${service.getGeneratedErrorCode(classname)} Could not read the directory " + path + ": " + e.getMessage(), e);
        }
      } else {
        result.add(path);
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * Creates the generator setup, which is shared by all rules of a run.
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

  public Optional<AST${grammarName}TFRule> parseRule(Path model) {
    Log.debug("Start parsing of the model " + model, LOG_ID);
    ${dstlName}Parser parser = ${dstlName}Mill.parser();
    Optional<AST${grammarName}TFRule> ast;
    try {
      ast = parser.parse${grammarName}TFRule(model.toString());
    }
    catch (IOException e) {
      Log.error("0xA6156${service.getGeneratedErrorCode(classname)} Could not read the model " + model + ": " + e.getMessage(), e);
      return Optional.empty();
    }
    if (parser.hasErrors() || ast.isEmpty()) {
      Log.error(
          "0xA6152${service.getGeneratedErrorCode(classname)} There are parsing errors while parsing of the model " + model);
      return Optional.empty();
    }
    ast.get().getTFRule().setName(deriveRuleName(model));
    Log.debug("Model " + model + " parsed successfully", LOG_ID);
    return ast;
  }

  /**
   * The rule (and thus the generated class) is named after the file, without its extension.
   */
  protected String deriveRuleName(Path model) {
    String fileName = model.getFileName().toString();
    return fileName.endsWith(RULE_FILE_EXTENSION)
        ? fileName.substring(0, fileName.length() - RULE_FILE_EXTENSION.length())
        : fileName;
  }

  /**
   * Checks all context conditions. Fail quick is disabled meanwhile,
   * so that all violations are reported instead of only the first one.
   */
  public void checkCoCos(AST${grammarName}TFRule ast) {
    Log.debug("Starting coco checking ", LOG_ID);
    boolean failQuick = Log.isFailQuickEnabled();
    Log.enableFailQuick(false);
    try {
      TransCoCos.getCheckerForAllCoCos().checkAll(ast);
    } finally {
      Log.enableFailQuick(failQuick);
    }
    Log.debug("Finished coco checking ", LOG_ID);
  }

  public ASTODRule createODRule(AST${grammarName}TFRule ast) {
    Log.debug("Starting odrule generation ", LOG_ID);
    // typed supplier, as create(Supplier) and create(E) are ambiguous for a method reference
    Supplier<${dstlName}Traverser> traverser = ${dstlName}Mill::inheritanceTraverser;
    ModelTraversal<${dstlName}Traverser> mt = ModelTraversalFactory.getInstance().create(traverser);
    ast.accept(mt.getTraverser());

    Log.debug("Switching to OD ", LOG_ID);
    // Switch language to OD
    ODRulesMill.reset();
    ODRulesMill.init();

    Log.debug("Starting rule2odstate ", LOG_ID);
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), mt.getParents());
<#if grammarPackage?has_content>
    state.getGenRule().setGrammarPackageList(new ArrayList<>(Arrays.asList("${grammarPackage}".split("\\."))));
<#else>
    state.getGenRule().setGrammarPackageList(new ArrayList<>());
</#if>
    state.getGenRule().setGrammarName("${grammarName}");
    ${grammarName}RuleCollectVariables variables = new ${grammarName}RuleCollectVariables(state);
    ${grammarName}Rule2OD rule2OD = new ${grammarName}Rule2OD(state);

    ast.accept(variables.getTraverser());

    ast.accept(rule2OD.getTraverser());
    ASTODRule astod = rule2OD.getOD();
    astod.setVariables(variables.getCollectedVariables());


    // create symbol table
    ODRulesScopesGenitorDelegator symbolTable = ODRulesMill.scopesGenitorDelegator();
    symbolTable.createFromAST(astod);


    if(!ast.getTFRule().getPackageList().isEmpty()) {
      astod.setPackageList(new ArrayList<>(ast.getTFRule().getPackageList()));
    }
    astod.setMCImportStatementList(new ArrayList<>(ast.getTFRule().getMCImportStatementList()));
    if(ast.getTFRule().isPresentName()){
      astod.setName(ast.getTFRule().getName());
    }

    return astod;
  }

  @Deprecated
  public void generate(ASTODRule ast, File outputDirectory) {
    Log.debug("Generate Transformation for " + ast.getName(), LOG_ID);
    initODRuleGenerationMill();
    ODRuleCodeGenerator.generate(ast, outputDirectory);
  }

  public void generate(ASTODRule ast, GeneratorSetup setup) {
    Log.debug("Generate Transformation for " + ast.getName(), LOG_ID);
    initODRuleGenerationMill();
    ODRuleCodeGenerator.generate(ast, setup);
  }

  /**
   * The code generator builds its intermediate model with the ODRuleGenerationMill.
   */
  protected void initODRuleGenerationMill() {
    ODRuleGenerationMill.reset();
    ODRuleGenerationMill.init();
  }

    /**
     * Initializes the available CLI options for the TFGen tool.
     *
     * @return The CLI options with arguments.
     */
    protected Options initOptions() {
      Options options = new Options();

      // parse input rules
      options.addOption(Option.builder("i")
          .longOpt("input")
          .argName("files")
          .hasArgs()
          .desc("Processes the given \".mtr\" rule file(s) and triggers the transformation generation. "
              + "Directories are searched recursively for \".mtr\" files.")
          .get());

      // specify custom output directory
      options.addOption(Option.builder("o")
          .longOpt("out")
          .argName("path")
          .hasArg()
          .desc("Output directory for all generated artifacts.")
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


  /**
   * Prints the help dialog.
   *
   * @param options The input parameters and options.
   */
  protected void printHelp(Options options) {
    TextHelpAppendable out = new TextHelpAppendable(System.out);
    out.setMaxWidth(80);
    HelpFormatter formatter = HelpFormatter.builder()
        .setHelpAppendable(out)
        .setShowSince(false)
        .get();
    try {
      formatter.printHelp("${grammarName}TFGenTool", "", options, "", true);
    } catch (IOException e) {
      Log.error("0xA6158${service.getGeneratedErrorCode(classname)} Could not print the help dialog: " + e.getMessage(), e);
    }
  }

}
