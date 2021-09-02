package de.monticore.dstlgen;

import com.google.common.base.Joiner;
import de.monticore.dstlgen.grammartransformation.DSL2TransformationLanguageVisitor;
import de.monticore.dstlgen.grammartransformation.TFLanguageOverrideVisitor;
import de.monticore.dstlgen.ruletranslation.CollectGrammarInformationVisitor;
import de.monticore.dstlgen.ruletranslation.DSTLGenAttributeHelper;
import de.monticore.dstlgen.ruletranslation.DSTLGenInheritanceHelper;
import de.monticore.dstlgen.util.DSTLPrettyPrinter;
import de.monticore.dstlgen.util.DSTLService;
import de.monticore.expressions.prettyprint.*;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._parser.GrammarTransformer;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._parser.GrammarFamilyParser;
import de.monticore.grammar.grammarfamily._symboltable.GrammarFamilyPhasedSTC;
import de.monticore.grammar.grammarfamily._symboltable.IGrammarFamilyGlobalScope;
import de.monticore.grammar.grammarfamily._visitor.GrammarFamilyTraverser;
import de.monticore.grammar.prettyprint.AntlrPrettyPrinter;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.io.paths.MCPath;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.JavaLightPrettyPrinter;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCExceptionStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCReturnStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DSTLGenCLI {

  /**
   * Exemplary calls
   *
   * @param args
   */
  public static void main(String[] args) {
    DSTLGenCLI cli = new DSTLGenCLI();

    Log.init();
    GrammarFamilyMill.init();
    cli.run(args);
    GrammarFamilyMill.reset();
  }

  private final String HC_SUFFIX = "HC";
  final String LOG_ID = "DSTLGenScript";
  private GlobalExtensionManagement glex;
  private GeneratorEngine generator;
  private File outDirectory;
  private MCPath handcodedPath;
  private String modelFileExtension = "mtr";

  public void run(String[] args) {
    Options options = initOptions();

    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      // help: when --help
      if (cmd.hasOption("h") || !cmd.hasOption("g")) {
        printHelp(options);
        // do not continue, when help is printed
        return;
      }
      if (cmd.hasOption("g")) {
        List<Path> modelPathFile = new ArrayList<>();
        if (cmd.hasOption("mp")) {
          for (String mp : cmd.getOptionValues("mp")) {
            modelPathFile.addAll(Arrays.stream(mp.split(File.pathSeparator))
                                         .map(x -> new File(x).toPath()).collect(Collectors.toList()));
          }
        }
        MCPath modelPath = new MCPath(modelPathFile);

        outDirectory = new File(cmd.getOptionValue("o", "target/generated-sources"));
        modelFileExtension = cmd.getOptionValue("fe", "mtr");

        MCPath modelsHC = new MCPath();
        if (cmd.hasOption("hcg") && cmd.getOptionValues("hcg") != null) {
          modelsHC = new MCPath(toPathList(Arrays.asList(cmd.getOptionValues("hcg"))));
        }

        handcodedPath = new MCPath();
        if (cmd.hasOption("hcp") && cmd.getOptionValues("hcp") != null) {
          handcodedPath = new MCPath(toPathList(Arrays.asList(cmd.getOptionValues("hcp"))));
        }

        doGenDSTL(cmd.getOptionValue("g"), modelPath, modelsHC);
      }
    } catch (ParseException e) {
      // ann unexpected error from the apache CLI parser:
      Log.error("0xA5C02 Could not process CLI parameters: " + e.getMessage());
    }
  }

  protected static List<File> toFileList(List<String> files) {
    return files.stream().map(file -> new File(file)).collect(Collectors.toList());
  }

  protected static List<Path> toPathList(List<String> files) {
    return files.stream().map(file -> Paths.get(file)).collect(Collectors.toList());
  }


  protected void doGenDSTL(String input, MCPath modelPath, MCPath modelsHC) {
    Log.debug("--------------------------------", LOG_ID);
    Log.debug("Generating DSTLs", LOG_ID);
    Log.debug("--------------------------------", LOG_ID);
    Log.debug("Input grammar     : " + input, LOG_ID);
    Log.debug("Model path        : " + modelPath.toString(), LOG_ID);
    Log.debug("HC Model Ext path : " + modelsHC, LOG_ID);
    Log.debug("Output dir        : " + outDirectory, LOG_ID);
    Log.debug("Handcoded sources : " + handcodedPath, LOG_ID);
    Log.debug("Model File extension    : " + modelFileExtension, LOG_ID);

    // Parse Grammar
    // Grammar is given as relative file (not packed within a jar)
    ASTMCGrammar g = parseGrammar(input);

    // Initialize symbol table
    initSymbolTable(g, modelPath);

    // Initialize generator
    initGenerator(g, outDirectory);

    // Parse TF grammar extension
    Optional<ASTMCGrammar> gext = parseGrammarHC(g, modelsHC);

    // Generate grammar
    generateDSTL(g, gext);

    // Generate context conditions
    generateCoCos(g);

    // Generate DSTL to ODRule translator
    generateTranslator (g);

    // Generate main class
    generateMainClass(g);

  }


  /**
   * Parse a single grammar
   *
   * @param grammar
   * @return
   */
  public ASTMCGrammar parseGrammar(String grammar) {
    Log.info("Start parsing of the grammar " + grammar, LOG_ID);
    try {
      GrammarFamilyParser parser = GrammarFamilyMill.parser();
      Optional<ASTMCGrammar> ast = parser.parse(grammar);
      if (!parser.hasErrors() && ast.isPresent()) {
        Log.info("Grammar " + grammar + " parsed successfully", LOG_ID);
        GrammarTransformer.transform(ast.get());
      } else {
        Log.error("0xA5C03 There are parsing errors while parsing of the model " + grammar);
      }
      return ast.get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Initialize symbol table
   *
   * @param grammar
   * @param modelPath
   */
  public void initSymbolTable(ASTMCGrammar grammar, MCPath modelPath) {
    IGrammarFamilyGlobalScope globalScope = createMCGlobalScope(modelPath, "mc4");

    String qualifiedGrammarName = Names.constructQualifiedName(grammar.getPackageList(), grammar.getName());

    new GrammarFamilyPhasedSTC(globalScope).createFromAST(grammar);
    globalScope.addLoadedFile(qualifiedGrammarName);
  }

  public IGrammarFamilyGlobalScope createMCGlobalScope(MCPath modelPath, String fileExt) {
    IGrammarFamilyGlobalScope scope = GrammarFamilyMill.globalScope();
    // reset global scope
    scope.clear();

    // Set Fileextension and ModelPath
    scope.setFileExt(fileExt);
    scope.setSymbolPath(modelPath);
    return scope;
  }

  /**
   * Search for handwritten TRGrammar in paths which extends generated TRGrammar of grammar
   *
   * @param grammar
   * @param paths
   */
  public Optional<ASTMCGrammar> parseGrammarHC(ASTMCGrammar grammar, MCPath paths) {
    List<String> trGrammarNames = new ArrayList<>(grammar.getPackageList());
    trGrammarNames.add("tr");
    trGrammarNames.add(grammar.getName() + "TR" + HC_SUFFIX + ".mc4");

    Path trGrammarPath = Paths.get(trGrammarNames.stream().collect(Collectors.joining(File.separator)));
    Optional<URL> hwGrammar = paths.find(trGrammarPath.toString());
    if (hwGrammar.isPresent()) {
      return Optional.of(parseGrammar(MCPath.toPath(hwGrammar.get()).get().toString()));
    }
    return Optional.empty();
  }

  /**
   * Initialize generator
   *
   * @param grammar
   * @param outputDirectory
   */
  public void initGenerator(ASTMCGrammar grammar, File outputDirectory) {
    this.outDirectory = outputDirectory;
    this.glex = new GlobalExtensionManagement();
    setUpValues(glex, grammar.getSymbol());
    setUpUtils(glex);
    setUpTemplates(glex);

    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    setup.setGlex(glex);
    generator = new GeneratorEngine(setup);
  }

  public void generateDSTL(ASTMCGrammar grammar, Optional<ASTMCGrammar> grammarExt) {
    this.glex.setGlobalValue("service", new DSTLService(grammar));
    //    CommonVisitor.run(new RemoveCommentsVisitor(), dslAst);
    DSL2TransformationLanguageVisitor dsl2TfLang = new DSL2TransformationLanguageVisitor();
    GrammarFamilyTraverser traverser = GrammarFamilyMill.traverser();
    traverser.add4Grammar(dsl2TfLang);
    grammar.accept(traverser);
    ASTMCGrammar tfLanguage = dsl2TfLang.getTfLang();

    // override with handwritten tf rules
    if (grammarExt.isPresent()) {
      TFLanguageOverrideVisitor overrideVisitor = new TFLanguageOverrideVisitor(tfLanguage, grammarExt.get());
      traverser = GrammarFamilyMill.traverser();
      traverser.add4Grammar(overrideVisitor);
      grammarExt.get().accept(traverser);
    }

    // compute name of output file
    String directories = outDirectory.getPath().replace('\\', '/') + "/" + Joiner
            .on("/").join(tfLanguage.getPackageList());

    // write output
    IndentPrinter printer = new IndentPrinter();

    // Prepare the Pretty Printer as a traverer
    traverser = GrammarFamilyMill.traverser();

    Grammar_WithConceptsPrettyPrinter grammar_withConceptsPrettyPrinter
            = new Grammar_WithConceptsPrettyPrinter(printer);
    traverser.setGrammar_WithConceptsHandler(grammar_withConceptsPrettyPrinter);
    traverser.add4Grammar_WithConcepts(grammar_withConceptsPrettyPrinter);
    AntlrPrettyPrinter antlrPrettyPrinter = new AntlrPrettyPrinter(printer);
    traverser.setAntlrHandler(antlrPrettyPrinter);
    traverser.add4Antlr(antlrPrettyPrinter);
    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new AssignmentExpressionsPrettyPrinter(
            printer);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
    JavaClassExpressionsPrettyPrinter javaClassExpressionsPrettyPrinter = new JavaClassExpressionsPrettyPrinter(
            printer);
    traverser.setJavaClassExpressionsHandler(javaClassExpressionsPrettyPrinter);
    traverser.add4JavaClassExpressions(javaClassExpressionsPrettyPrinter);
    BitExpressionsPrettyPrinter bitExpressionsPrettyPrinter = new BitExpressionsPrettyPrinter(printer);
    traverser.setBitExpressionsHandler(bitExpressionsPrettyPrinter);
    traverser.add4BitExpressions(bitExpressionsPrettyPrinter);
    MCCommonLiteralsPrettyPrinter mcCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(mcCommonLiteralsPrettyPrinter);
    traverser.add4MCCommonLiterals(mcCommonLiteralsPrettyPrinter);
    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter = new MCSimpleGenericTypesPrettyPrinter(
            printer);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
    MCExceptionStatementsPrettyPrinter mcExceptionStatementsPrettyPrinter = new MCExceptionStatementsPrettyPrinter(
            printer);
    traverser.setMCExceptionStatementsHandler(mcExceptionStatementsPrettyPrinter);
    traverser.add4MCExceptionStatements(mcExceptionStatementsPrettyPrinter);
    MCReturnStatementsPrettyPrinter mcReturnStatementsPrettyPrinter = new MCReturnStatementsPrettyPrinter(printer);
    traverser.setMCReturnStatementsHandler(mcReturnStatementsPrettyPrinter);
    traverser.add4MCReturnStatements(mcReturnStatementsPrettyPrinter);
    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(printer);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
    JavaLightPrettyPrinter javaLightPrettyPrinter = new JavaLightPrettyPrinter(printer);
    traverser.setJavaLightHandler(javaLightPrettyPrinter);
    traverser.add4JavaLight(javaLightPrettyPrinter);

    // Use the {@link de.monticore.tf.util.DSTLPrettyPrinter}
    DSTLPrettyPrinter dstlPrettyPrinter = new DSTLPrettyPrinter(printer);
    traverser.setGrammarHandler(dstlPrettyPrinter);
    traverser.add4Grammar(dstlPrettyPrinter);

    tfLanguage.accept(traverser);
    String output = printer.getContent();

    try {
      Files.createDirectories(Paths.get(directories));
      Files.write(Paths.get(directories + "/" + tfLanguage.getName() + ".mc4"), output.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(
              "0xF2000 Cannot write grammar to " + directories + "/" + tfLanguage.getName() + ".mc4");
    }
  }
  /**
   * Generate Context Conditions
   *
   * @param grammar
   */
  public void generateCoCos(ASTMCGrammar grammar) {
    String dstlPackagePrefix = getDSTLPackagePrefix(grammar);
    String packageName = Names.getPathFromPackage(dstlPackagePrefix + "."
                                                          + grammar.getName().toLowerCase() + "tr"
                                                          + "._cocos.");
    List<ASTProd> productions = (List<ASTProd>) glex.getGlobalVar("productions");
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) glex.getGlobalVar("symbolTable");

    String classname = getSimpleTypeNameToGenerate("NoOptWithinNotCoCo", packageName, this.handcodedPath);
    Path filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.NoOptWithinNotCoCo", filePath,
                       grammar, classname, dstlPackagePrefix);
    for(ASTProd prod : productions) {
      classname = getSimpleTypeNameToGenerate("NoOptWithinNotCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.NoOptWithinNotCoCoSub", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
    }

    classname = getSimpleTypeNameToGenerate("NoOptOnRHSCoCo", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.NoOptOnRHSCoCo", filePath,
                       grammar, classname, dstlPackagePrefix);
    for(ASTProd prod : productions) {
      classname = getSimpleTypeNameToGenerate("NoOptOnRHSCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.NoOptOnRHSCoCoSub", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
    }

    classname = getSimpleTypeNameToGenerate(grammar.getName() + "RepElemVisitor", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.RepElemVisitor", filePath,
                       grammar, classname, dstlPackagePrefix);
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "RepElemVisitor");

    classname = getSimpleTypeNameToGenerate(grammar.getName() + "NegElemVisitor", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.NegElemVisitor", filePath,
                       grammar, classname, dstlPackagePrefix);
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "NegElemVisitor");

    classname = getSimpleTypeNameToGenerate(grammar.getName() + "CollectRHSVariablesVisitor", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.CollectRHSVariablesVisitor", filePath,
                       grammar, classname, dstlPackagePrefix);
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "CollectRHSVariablesVisitor");

    classname = getSimpleTypeNameToGenerate("AssignedOnlyOnceCoCo", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.AssignedOnlyOnceCoCo", filePath,
                       grammar, classname, dstlPackagePrefix);

    for(ASTProd prod : productions) {
      classname = getSimpleTypeNameToGenerate("NoEmptyRepCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.NoEmptyRepCoCo", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
      classname = getSimpleTypeNameToGenerate("NoNegElemChangeCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.NoNegElemChangeCoCo", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
      classname = getSimpleTypeNameToGenerate("NoNegElemCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.NoNegElemCoCo", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
      classname = getSimpleTypeNameToGenerate("NoNegElemNestCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.NoNegElemNestCoCo", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
      classname = getSimpleTypeNameToGenerate("NoRepOnRHSCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.NoRepOnRHSCoCo", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
      classname = getSimpleTypeNameToGenerate("SchemaVarNamingCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.SchemaVarNamingCoCo", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
      classname = getSimpleTypeNameToGenerate("ReplacementOpCoCo_" + prod.getName(), packageName, handcodedPath);
      filePath = Paths.get(packageName + classname + ".java");
      generator.generate("dstlgen.cocos.ReplacementOpCoco", filePath,
                         grammar, classname, prod, dstlPackagePrefix);
    }

    classname = getSimpleTypeNameToGenerate("SchemaVarRepRhsCoCo", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.SchemaVarRepRhsCoCo", filePath,
                       grammar, classname, dstlPackagePrefix);
    classname = getSimpleTypeNameToGenerate("NoSchemaVarAnonRhsCoCo", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.NoSchemaVarAnonRhsCoCo", filePath,
                       grammar, classname, dstlPackagePrefix);
    classname = getSimpleTypeNameToGenerate("DiffSchemaVarTypeCoCo", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.DiffSchemaVarTypeCoCo", filePath,
                       grammar, classname, dstlPackagePrefix);
    classname = getSimpleTypeNameToGenerate("DiffSchemaVarTypeVisitor", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.DiffSchemaVarTypeVisitor", filePath,
                       grammar, classname, dstlPackagePrefix);
    // TODO: DiffSchemaVarTypeVisitor uses a different naming convention

    classname = getSimpleTypeNameToGenerate("NoDeleteWithoutParentCoCo", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.NoDeleteWithoutParentCoCo", filePath,
                       grammar, classname, dstlPackagePrefix);

    classname = getSimpleTypeNameToGenerate(grammar.getName() + "NoDeleteWithoutParentVisitor", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.NoDeleteWithoutParentVisitor", filePath,
                       grammar, classname, dstlPackagePrefix);
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "NoDeleteWithoutParentVisitor");

    classname = getSimpleTypeNameToGenerate("TransCoCos", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.TransCoCos", filePath,
                       grammar, classname, dstlPackagePrefix);
  }

  private void generateVisitorBuilder(ASTMCGrammar grammar, String packageName, String dstlPackagePrefix, String visitorClassName, String visitorName){
    String classname = getSimpleTypeNameToGenerate(grammar.getName() + visitorName + "Builder", packageName, handcodedPath);
    Path filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.CoCoVisitorBuilder", filePath,
                       grammar, visitorClassName, dstlPackagePrefix, visitorName);
  }

  /**
   * Generate DSTL to ODRule Translator
   *
   * @param grammar
   */
  public void generateTranslator(ASTMCGrammar grammar) {
    String _package = Names.getPathFromPackage(getDSTLPackagePrefix(grammar) +".translation.");
    String classname = getSimpleTypeNameToGenerate(grammar.getName() + "Rule2ODVisitor", _package, handcodedPath);

    // generate Rule2ODVisitor
    Path filePath = Paths.get(_package + classname + ".java");
    generator.generate("dstlgen.rule2od.Rule2ODVisitor", filePath, grammar, classname,getDSTLPackagePrefix(grammar));

    // generate Rule2OD
    classname = getSimpleTypeNameToGenerate(grammar.getName() + "Rule2OD", _package, handcodedPath);
    filePath = Paths.get(_package + classname + ".java");
    generator.generate("dstlgen.rule2od.Rule2OD", filePath, grammar, classname,getDSTLPackagePrefix(grammar));


    // generate RuleCollectVariablesVisitor
    _package = Names.getPathFromPackage(getDSTLPackagePrefix(grammar) + ".translation.");
    classname = getSimpleTypeNameToGenerate(grammar.getName() + "RuleCollectVariablesVisitor", _package, handcodedPath);
    filePath = Paths.get(_package + classname + ".java");
    generator.generate("dstlgen.variables.RuleCollectVariablesVisitor", filePath, grammar, classname, getDSTLPackagePrefix(grammar));

    // generate RuleCollectVariables
    _package = Names.getPathFromPackage(getDSTLPackagePrefix(grammar) + ".translation.");
    classname = getSimpleTypeNameToGenerate(grammar.getName() + "RuleCollectVariables", _package, handcodedPath);
    filePath = Paths.get(_package + classname + ".java");
    generator.generate("dstlgen.variables.RuleCollectVariables", filePath, grammar, classname, getDSTLPackagePrefix(grammar));

  }


  protected String getDSTLPackagePrefix(ASTMCGrammar grammar) {
    String dslPackage = Joiners.DOT.join(grammar.getPackageList());
    if (!dslPackage.isEmpty()) {
      dslPackage += ".";
    }

    return dslPackage + "tr";
  }

  /**
   * Generate Main Class so that generated language JAR is CLI executable
   */
  public void generateMainClass(ASTMCGrammar grammar) {
    if (!grammar.isComponent()) {
      String packageName = Names.getPathFromPackage(getDSTLPackagePrefix(grammar) +".");
      String className = getSimpleTypeNameToGenerate(grammar.getName() + "CLI", packageName, handcodedPath);
      Path filePath = Paths.get(packageName + className + ".java");
      generator.generate("dstlgen.MainClass", filePath,
                         grammar, className, modelFileExtension, grammar.getName()
                                 + "TR", getDSTLPackagePrefix(grammar), grammar.getName(), Names.constructQualifiedName(grammar.getPackageList()));
    }

  }



  protected void setUpTemplates(GlobalExtensionManagement glex) {
    //Set up template short cuts
    // Rule2OD
    glex.setGlobalValue("visit_pattern", "dstlgen.rule2od.VisitNTPattern");
    glex.setGlobalValue("visit_pattern_external", "dstlgen.rule2od.VisitNTPatternExternal");
    glex.setGlobalValue("traverse_replacement", "dstlgen.rule2od.TraverseNTReplacement");
    glex.setGlobalValue("visit_list", "dstlgen.rule2od.VisitNTList");

    glex.setGlobalValue("visit_negation", "dstlgen.VisitNTNegation");
    glex.setGlobalValue("visit_optional", "dstlgen.VisitNTOptional");

    glex.setGlobalValue("process_string_attrs", "dstlgen.rule2od.StringAttrs");
    glex.setGlobalValue("process_stringlist_attrs", "dstlgen.rule2od.StringListAttrs");
    glex.setGlobalValue("process_boolean_alt_attrs_pattern", "dstlgen.rule2od.BooleanAltAttrs");
    glex.setGlobalValue("process_boolean_attrs_pattern", "dstlgen.rule2od.BooleanAttrs");
    glex.setGlobalValue("process_component_lists_pattern", "dstlgen.rule2od.ComponentLists");
    glex.setGlobalValue("process_component_nodes_pattern", "dstlgen.rule2od.ComponentNodes");

    // RuleCollectVariables
    glex.setGlobalValue("var_visit_pattern", "dstlgen.variables.VarVisitNTPattern");
    glex.setGlobalValue("var_traverse_replacement", "dstlgen.variables.VarTraverseNTReplacement");
    glex.setGlobalValue("collect_string_attrs", "dstlgen.variables.VarStringAttrs");
    glex.setGlobalValue("collect_stringlist_attrs", "dstlgen.variables.VarStringListAttrs");

    glex.setGlobalValue("collect_rhs_vars", "dstlgen.cocos.CollectRHSVars");
    glex.setGlobalValue("collect_rhs_listvars", "dstlgen.cocos.CollectRHSListVars");
  }

  protected void setUpUtils(GlobalExtensionManagement glex) {
    glex.setGlobalValue("inheritanceHelper", new DSTLGenInheritanceHelper());
    glex.setGlobalValue("attributeHelper", new DSTLGenAttributeHelper());
  }

  protected void setUpValues(GlobalExtensionManagement glex, MCGrammarSymbol grammarSymbol) {
    // collect information
    CollectGrammarInformationVisitor informationVisitor = new CollectGrammarInformationVisitor(grammarSymbol);
    Grammar_WithConceptsTraverser traverser = GrammarFamilyMill.traverser();
    traverser.add4Grammar(informationVisitor);
    grammarSymbol.getAstGrammar().get().accept(traverser);


    glex.setGlobalValue("productions", informationVisitor.getCollectedParserProds());
    glex.setGlobalValue("grammarInfo", informationVisitor);
    glex.setGlobalValue("symbolTable", grammarSymbol);
    glex.setGlobalValue("grammarNameLower", grammarSymbol.getName().toLowerCase());
    glex.setGlobalValue("grammarName", grammarSymbol.getName());
    glex.setGlobalValue("package", Joiners.DOT.join(grammarSymbol.getAstNode().getPackageList()));
  }

  private String getSimpleTypeNameToGenerate(String simpleName, String packageName, MCPath targetPath) {
    if (GeneratorEngine.existsHandwrittenClass(new MCPath(targetPath.getEntries()), packageName + "." + simpleName)) {
      return simpleName + GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    return simpleName;
  }

  public void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("DSTLGenCLI", options);
  }

  protected Options initOptions() {
    Options options = new Options();

    // help dialog
    options.addOption(Option.builder("h")
                              .longOpt("help")
                              .desc("Prints this help dialog")
                              .build());

    // gen dstl
    options.addOption(Option.builder("g")
                              .longOpt("grammar")
                              .desc("grammar file")
                              .numberOfArgs(1)
                              .build());


    options.addOption(Option.builder("mp")
                              .longOpt("modelPath")
                              .desc("modelPath")
                              .hasArgs()
                              .build());


    options.addOption(Option.builder("o")
                              .longOpt("out")
                              .desc("outputDir")
                              .numberOfArgs(1)
                              .build());

    options.addOption(Option.builder("hcg")
                              .longOpt("modelsHC")
                              .desc("modelPath")
                              .hasArgs()
                              .build());

    options.addOption(Option.builder("hcp")
                              .longOpt("handcodedPath")
                              .desc("Optional list of directories to look for handwritten code to integrate")
                              .hasArgs()
                              .build());

    options.addOption(Option.builder("fe")
                              .longOpt("fileExtension")
                              .desc("fileExtension")
                              .numberOfArgs(1)
                              .build());

    return options;
  }


}
