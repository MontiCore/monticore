<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "fileExtension", "dstlName", "package", "grammarName", "grammarPackage")}
package ${package};

import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import de.monticore.io.paths.MCPath;
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
import org.apache.commons.cli.*;

<#assign service = glex.getGlobalVar("service")>

public class ${className} {
  
  public static void main(String[] args) {
    Log.init();
    new ${grammarName}TFGenTool().run(args);
  }
  
  protected void run(String[] args){
  
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
  
      if (!cmd.hasOption("i")) {
        Log.error(
            "0xA1001${service.getGeneratedErrorCode(classname)} There is no \".mtr\" file to parse. Please check the \"input\" option.");
        // do not continue, when this error is logged
        return;
      }
  
      ${dstlName}Mill.init();
  
      Log.debug("----- Starting Transformation Generation -----", LOG_ID);
      Log.debug("Input file   : " + cmd.getOptionValue("i"), LOG_ID);
      Log.debug("Output dir    : " + cmd.getOptionValue("o", "out"), LOG_ID);
  
      Path model = Paths.get(cmd.getOptionValue("i"));
  
      // Parse rule
      AST${grammarName}TFRule rule = parseRule(model).get();
  
      // check CoCos
      checkCoCos(rule);
  
      ASTODRule odrule;
  
      //create od rule
      odrule = createODRule(rule);
      // generate
      generate(odrule, Paths.get(cmd.getOptionValue("o", "out")).toFile());
    } catch ( ParseException e) {
        // an unexpected error from the Apache CLI parser:
        Log.error("0xA6153${service.getGeneratedErrorCode(classname)} Could not process CLI parameters: " + e.getMessage());
      }
  }
  
  public Optional<AST${grammarName}TFRule> parseRule(Path model) {
    ${dstlName}Mill.init();
    Log.debug("Start parsing of the model " + model, LOG_ID);
    try {
      ${dstlName}Parser parser = new ${dstlName}Parser();
      Optional<AST${grammarName}TFRule> ast = parser.parse${grammarName}TFRule(model.toString());
      if (!parser.hasErrors() && ast.isPresent()) {
        Log.debug("Model " + model + " parsed successfully", LOG_ID);
        String name = model.toString().replace(".mtr", "");
        ast.get().getTFRule().setName(name.substring(model.toString().lastIndexOf(File.separator) + 1, name.length()));
      }
      else {
        Log.error(
            "0xA6152${service.getGeneratedErrorCode(classname)} There are parsing errors while parsing of the model " + model);
      }
      return ast;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public void checkCoCos(AST${grammarName}TFRule ast) {
    Log.debug("Starting coco checking ", LOG_ID);
    TransCoCos.getCheckerForAllCoCos().checkAll(ast);
    Log.debug("Finished coco checking ", LOG_ID);
  }
  
  public ASTODRule createODRule(AST${grammarName}TFRule ast) {
    Log.debug("Starting odrule generation ", LOG_ID);
    ModelTraversal<${dstlName}Traverser> mt = ModelTraversalFactory.getInstance().create((java.util.function.Supplier)${dstlName}Mill::inheritanceTraverser);
    ast.accept(mt.getTraverser());
    Log.debug("Starting rule2odstate ", LOG_ID);
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), mt.getParents());
    state.getGenRule().setGrammarPackageList(Arrays.asList("${grammarPackage}".split("\\.")));
    state.getGenRule().setGrammarName("${grammarName}");
    ${grammarName}RuleCollectVariables variables = new ${grammarName}RuleCollectVariables(state);
    ${grammarName}Rule2OD rule2OD = new ${grammarName}Rule2OD(state);
    
    Log.debug("Switching to OD ", LOG_ID);
    // Switch language to OD
    ODRulesMill.init();
    
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
  
  public void generate(ASTODRule ast, File outputDirectory) {
    Log.debug("Generate Transformation for " + ast.getName(), LOG_ID);
    ODRuleCodeGenerator.generate(ast, outputDirectory);
  }
  
    /**
     * Initializes the available CLI options for the TFGen tool.
     *
     * @return The CLI options with arguments.
     */
    protected Options initOptions() {
      Options options = new Options();
    
      // parse input grammars
      options.addOption(Option.builder("i")
          .longOpt("input")
          .argName("file")
          .hasArg()
          .desc("Processes the given grammar and triggers the transformation generation.")
          .build());
    
      // specify custom output directory
      options.addOption(Option.builder("o")
          .longOpt("out")
          .argName("path")
          .hasArg()
          .desc("Output directory for all generated artifacts.")
          .build());
  
      // help dialog
      options.addOption(Option.builder("h")
          .longOpt("help")
          .desc("Prints this help dialog")
          .build());
    
      return options;
    }
  
  
  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param options The input parameters and options.
   */
  protected void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("${dstlName}TFGenTool", options);
  }
  
  static final String LOG_ID = "${dstlName}";

}

