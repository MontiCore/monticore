/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen;

import com.google.common.base.Joiner;
import de.monticore.dstlgen.grammartransformation.DSL2TransformationLanguageVisitor;
import de.monticore.dstlgen.grammartransformation.TFLanguageOverrideVisitor;
import de.monticore.dstlgen.ruletranslation.CollectGrammarInformationVisitor;
import de.monticore.dstlgen.ruletranslation.DSTLGenAttributeHelper;
import de.monticore.dstlgen.ruletranslation.DSTLGenInheritanceHelper;
import de.monticore.dstlgen.util.DSTLPrettyPrinter;
import de.monticore.dstlgen.util.DSTLService;
import de.monticore.expressions.assignmentexpressions._prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.bitexpressions._prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.javaclassexpressions._prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.expressions.uglyexpressions._prettyprint.UglyExpressionsPrettyPrinter;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.concepts.antlr.antlr._prettyprint.AntlrPrettyPrinter;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.GrammarTransformer;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammar_withconcepts._prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.io.paths.MCPath;
import de.monticore.javalight._prettyprint.JavaLightPrettyPrinter;
import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.mcexceptionstatements._prettyprint.MCExceptionStatementsPrettyPrinter;
import de.monticore.statements.mcreturnstatements._prettyprint.MCReturnStatementsPrettyPrinter;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * This class contains the remnants of the DSTLGenCLI,
 * which now has been included into the standard monticore CLI.
 */
public class DSTLGenScript {

  protected final String LOG_ID = "DSTLGenScript";
  protected final String modelFileExtension = "mtr";


  /**
   * Parse a single grammar
   *
   * @param grammar
   * @return
   */
  public ASTMCGrammar parseGrammar(String grammar) {
    Log.debug("Start parsing of the grammar " + grammar, LOG_ID);
    try {
      Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
      Optional<ASTMCGrammar> ast = parser.parse(grammar);
      if (!parser.hasErrors() && ast.isPresent()) {
        Log.debug("Grammar " + grammar + " parsed successfully", LOG_ID);
        GrammarTransformer.transform(ast.get());
      } else {
        Log.error("0xA5C03 There are parsing errors while parsing of the model " + grammar);
      }
      return ast.get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public IGrammar_WithConceptsGlobalScope createMCGlobalScope(MCPath modelPath, String fileExt) {
    IGrammar_WithConceptsGlobalScope scope = Grammar_WithConceptsMill.globalScope();
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
    trGrammarNames.add(grammar.getName() + "TRHC.mc4");

    Path trGrammarPath = Paths.get(trGrammarNames.stream().collect(Collectors.joining(File.separator)));
    Optional<URL> hwGrammar = paths.find(trGrammarPath.toString());
    if (hwGrammar.isPresent()) {
      return Optional.of(parseGrammar(MCPath.toPath(hwGrammar.get()).get().toString()));
    }
    return Optional.empty();
  }

  public GlobalExtensionManagement initGlex(ASTMCGrammar grammar){
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    setUpDSTLValues(glex, grammar.getSymbol());
    setUpDSTLUtils(glex);
    setUpDSTLTemplates(glex);

    return glex;
  }

  /**
   * Initialize generator
   *
   * @param glex
   * @param outputDirectory
   */
  public GeneratorEngine initGenerator(GlobalExtensionManagement glex, File outputDirectory) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    setup.setGlex(glex);
    return new GeneratorEngine(setup);
  }

  public void generateDSTL(ASTMCGrammar grammar, Optional<ASTMCGrammar> grammarExt, GlobalExtensionManagement glex, File outDirectory) {
    DSL2TransformationLanguageVisitor dsl2TfLang = new DSL2TransformationLanguageVisitor();
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(dsl2TfLang);
    grammar.accept(traverser);
    ASTMCGrammar tfLanguage = dsl2TfLang.getTfLang();

    // override with handwritten tf rules
    if (grammarExt.isPresent()) {
      TFLanguageOverrideVisitor overrideVisitor = new TFLanguageOverrideVisitor(tfLanguage, grammarExt.get());
      traverser = Grammar_WithConceptsMill.traverser();
      traverser.add4Grammar(overrideVisitor);
      grammarExt.get().accept(traverser);
    }

    // compute name of output file
    String directories = outDirectory.getPath().replace('\\', '/') + "/" + Joiner
            .on("/").join(tfLanguage.getPackageList());

    // write output
    IndentPrinter printer = new IndentPrinter();

    // Prepare the Pretty Printer as a traverer
    traverser = Grammar_WithConceptsMill.traverser();

    Grammar_WithConceptsPrettyPrinter grammar_withConceptsPrettyPrinter
            = new Grammar_WithConceptsPrettyPrinter(printer, true);
    traverser.setGrammar_WithConceptsHandler(grammar_withConceptsPrettyPrinter);
    traverser.add4Grammar_WithConcepts(grammar_withConceptsPrettyPrinter);
    AntlrPrettyPrinter antlrPrettyPrinter = new AntlrPrettyPrinter(printer, true);
    traverser.setAntlrHandler(antlrPrettyPrinter);
    traverser.add4Antlr(antlrPrettyPrinter);
    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new AssignmentExpressionsPrettyPrinter(
            printer, true);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer, true);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(printer, true);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
    JavaClassExpressionsPrettyPrinter javaClassExpressionsPrettyPrinter = new JavaClassExpressionsPrettyPrinter(
            printer, true);
    traverser.setJavaClassExpressionsHandler(javaClassExpressionsPrettyPrinter);
    traverser.add4JavaClassExpressions(javaClassExpressionsPrettyPrinter);
    UglyExpressionsPrettyPrinter uglyExpressionsPrettyPrinter = new UglyExpressionsPrettyPrinter(
            printer, true);
    traverser.setUglyExpressionsHandler(uglyExpressionsPrettyPrinter);
    traverser.add4UglyExpressions(uglyExpressionsPrettyPrinter);
    BitExpressionsPrettyPrinter bitExpressionsPrettyPrinter = new BitExpressionsPrettyPrinter(printer, true);
    traverser.setBitExpressionsHandler(bitExpressionsPrettyPrinter);
    traverser.add4BitExpressions(bitExpressionsPrettyPrinter);
    MCCommonLiteralsPrettyPrinter mcCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer, true);
    traverser.setMCCommonLiteralsHandler(mcCommonLiteralsPrettyPrinter);
    traverser.add4MCCommonLiterals(mcCommonLiteralsPrettyPrinter);
    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer, true);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(printer, true);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter = new MCSimpleGenericTypesPrettyPrinter(
            printer, true);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
    MCExceptionStatementsPrettyPrinter mcExceptionStatementsPrettyPrinter = new MCExceptionStatementsPrettyPrinter(
            printer, true);
    traverser.setMCExceptionStatementsHandler(mcExceptionStatementsPrettyPrinter);
    traverser.add4MCExceptionStatements(mcExceptionStatementsPrettyPrinter);
    MCReturnStatementsPrettyPrinter mcReturnStatementsPrettyPrinter = new MCReturnStatementsPrettyPrinter(printer, true);
    traverser.setMCReturnStatementsHandler(mcReturnStatementsPrettyPrinter);
    traverser.add4MCReturnStatements(mcReturnStatementsPrettyPrinter);
    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(printer, true);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
    JavaLightPrettyPrinter javaLightPrettyPrinter = new JavaLightPrettyPrinter(printer, true);
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
   */
  public void generateDSTLCoCos(ASTMCGrammar grammar, GeneratorEngine generator, MCPath handcodedPath, GlobalExtensionManagement glex) {
    String dstlPackagePrefix = getDSTLPackagePrefix(grammar);
    String packageName = Names.getPathFromPackage(dstlPackagePrefix + "."
                                                          + grammar.getName().toLowerCase() + "tr"
                                                          + "._cocos.");
    List<ASTProd> productions = (List<ASTProd>) glex.getGlobalVar("productions");
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) glex.getGlobalVar("symbolTable");

    String classname = getSimpleTypeNameToGenerate("NoOptWithinNotCoCo", packageName, handcodedPath);
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
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "RepElemVisitor", generator, handcodedPath);

    classname = getSimpleTypeNameToGenerate(grammar.getName() + "NegElemVisitor", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.NegElemVisitor", filePath,
                       grammar, classname, dstlPackagePrefix);
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "NegElemVisitor", generator, handcodedPath);

    classname = getSimpleTypeNameToGenerate(grammar.getName() + "CollectRHSVariablesVisitor", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.CollectRHSVariablesVisitor", filePath,
                       grammar, classname, dstlPackagePrefix);
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "CollectRHSVariablesVisitor", generator, handcodedPath);

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
    generateVisitorBuilder(grammar, packageName, dstlPackagePrefix, classname, "NoDeleteWithoutParentVisitor", generator, handcodedPath);

    classname = getSimpleTypeNameToGenerate("TransCoCos", packageName, handcodedPath);
    filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.TransCoCos", filePath,
                       grammar, classname, dstlPackagePrefix);
  }

  private void generateVisitorBuilder(ASTMCGrammar grammar, String packageName, String dstlPackagePrefix, String visitorClassName, String visitorName, GeneratorEngine generator, MCPath handcodedPath){
    String classname = getSimpleTypeNameToGenerate(grammar.getName() + visitorName + "Builder", packageName, handcodedPath);
    Path filePath = Paths.get(packageName + classname + ".java");
    generator.generate("dstlgen.cocos.CoCoVisitorBuilder", filePath,
                       grammar, visitorClassName, dstlPackagePrefix, visitorName);
  }

  /**
   * Generate DSTL to ODRule Translator
   *
   */
  public void generateTranslator(ASTMCGrammar grammar, GeneratorEngine generator, MCPath handcodedPath) {
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
  public void generateTFGenToolClass(ASTMCGrammar grammar, GeneratorEngine generator, MCPath handcodedPath) {
    if (!grammar.isComponent()) {
      String packageName = Names.getPathFromPackage(getDSTLPackagePrefix(grammar) +".");
      String className = getSimpleTypeNameToGenerate(grammar.getName() + "TFGenTool", packageName, handcodedPath);
      Path filePath = Paths.get(packageName + className + ".java");
      generator.generate("dstlgen.MainClass", filePath,
                         grammar, className, modelFileExtension, grammar.getName()
                                 + "TR", getDSTLPackagePrefix(grammar), grammar.getName(), Names.constructQualifiedName(grammar.getPackageList()));
    }

  }



  protected void setUpDSTLTemplates(GlobalExtensionManagement glex) {
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

  protected void setUpDSTLUtils(GlobalExtensionManagement glex) {
    glex.setGlobalValue("inheritanceHelper", DSTLGenInheritanceHelper.getInstance());
    glex.setGlobalValue("attributeHelper", new DSTLGenAttributeHelper());
  }

  protected void setUpDSTLValues(GlobalExtensionManagement glex, MCGrammarSymbol grammarSymbol) {
    // collect information
    CollectGrammarInformationVisitor informationVisitor = new CollectGrammarInformationVisitor(grammarSymbol);
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(informationVisitor);
    grammarSymbol.getAstGrammar().get().accept(traverser);


    glex.setGlobalValue("productions", informationVisitor.getCollectedParserProds());
    glex.setGlobalValue("grammarInfo", informationVisitor);
    glex.setGlobalValue("symbolTable", grammarSymbol);
    glex.setGlobalValue("grammarNameLower", grammarSymbol.getName().toLowerCase());
    glex.setGlobalValue("grammarName", grammarSymbol.getName());
    glex.setGlobalValue("package", Joiners.DOT.join(grammarSymbol.getAstNode().getPackageList()));
    glex.setGlobalValue("service", new DSTLService(grammarSymbol.getAstNode()));
  }

  private String getSimpleTypeNameToGenerate(String simpleName, String packageName, MCPath targetPath) {
    if (GeneratorEngine.existsHandwrittenClass(new MCPath(targetPath.getEntries()), packageName + "." + simpleName)) {
      return simpleName + GeneratorSetup.GENERATED_CLASS_SUFFIX;
    }
    return simpleName;
  }



}
