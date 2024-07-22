/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeScopesGenitorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.*;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._ast_emf.ASTEmfCDDecorator;
import de.monticore.codegen.cd2java._ast_emf.CDEmfGenerator;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTFullEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.DataEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.mutatordecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageImplDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast_emf.enums.EmfEnumDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoCheckerDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoInterfaceDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoService;
import de.monticore.codegen.cd2java._od.ODCDDecorator;
import de.monticore.codegen.cd2java._od.ODDecorator;
import de.monticore.codegen.cd2java._od.ODService;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableCDDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDelegatorDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.Symbols2JsonDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._tagging.*;
import de.monticore.codegen.cd2java._visitor.*;
import de.monticore.codegen.cd2java.cli.CDCLIDecorator;
import de.monticore.codegen.cd2java.cli.CLIDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.data.ListSuffixDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.codegen.cd2java.mill.CDAuxiliaryDecorator;
import de.monticore.codegen.cd2java.mill.CDMillDecorator;
import de.monticore.codegen.cd2java.mill.MillDecorator;
import de.monticore.codegen.cd2java.mill.MillForSuperDecorator;
import de.monticore.codegen.cd2java.typecd2java.TemplateHPService;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherDecorator;
import de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherInterfaceDecorator;
import de.monticore.codegen.mc2cd.MC2CD4CodeSymbolTableCompleter;
import de.monticore.codegen.mc2cd.MC2CDTransformation;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.scopeTransl.MC2CDScopeTranslation;
import de.monticore.codegen.mc2cd.symbolTransl.MC2CDSymbolTranslation;
import de.monticore.codegen.parser.Languages;
import de.monticore.codegen.parser.ParserGenerator;
import de.monticore.codegen.prettyprint.CDPrettyPrinterDecorator;
import de.monticore.codegen.prettyprint.PrettyPrinterGenerator;
import de.monticore.dstlgen.DSTLGenScript;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.freemarker.MontiCoreTemplateLoader;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.cocos.GrammarCoCos;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsPhasedSTC;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.tagging.TagGenerator;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.groovy.GroovyRunnerBase;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.MontiCoreConfiguration.*;

/**
 * The actual top level functional implementation of MontiCore. This is the
 * top-most interface of MontiCore. The static members of this class constitute
 * the functional API of MontiCore to be used from within Groovy scripts (by
 * default). They represent the main functional blocks which make up the
 * MontiCore functionality, i.e. parsing of grammar files, generation of ASTs,
 * generation of parsers, etc. They also provide logging methods for from within
 * a Groovy script.<br>
 * <br>
 * This class extends {@link Script} for the purpose of being used as a base
 * class for Groovy scripts. This allows to use Groovy scripts for controlling
 * the actual workflow(s) in contrast to statically compiled Java byte code.
 * Language developers can hence very easily implement their own language
 * processing workflows without having to recompile things.
 */
public class MontiCoreScript extends Script implements GroovyRunner {

  /* The logger name for logging from within a Groovy script. */
  static final String LOG_ID = "MAIN";

  /**
   * Executes the default MontiCore Groovy script (parses grammars, generates
   * ASTs, parsers, etc.).
   *
   * @param configuration of MontiCore for this execution
   * @see Configuration
   * @see Configuration
   */
  public void run(Configuration configuration) {
    try {
      ClassLoader l = MontiCoreScript.class.getClassLoader();
      String script = Resources.asCharSource(l.getResource("de/monticore/monticore_standard.groovy"),
              StandardCharsets.UTF_8).read();
      run(script, configuration);
    } catch(IOException e) {
      Log.error("0xA1015 Failed to default MontiCore script.", e);
    }
  }

  /**
   * Executes the default MontiCore Groovy script (parses grammars, generates
   * ASTs, parsers, etc.) with emf
   *
   * @param configuration of MontiCore for this execution
   * @see Configuration
   * @see Configuration
   */
  public void run_emf(Configuration configuration) {
    try {
      ClassLoader l = MontiCoreScript.class.getClassLoader();
      String script = Resources.asCharSource(l.getResource("de/monticore/monticore_emf.groovy"),
              StandardCharsets.UTF_8).read();
      run(script, configuration);
    } catch(IOException e) {
      Log.error("0xA1012 Failed to default EMF MontiCore script.", e);
    }
  }

  /**
   * Executes the given Groovy script with the given
   * {@link MontiCoreConfiguration}.
   *
   * @param configuration of MontiCore for this execution
   * @param script to execute (NOT file or path, the actual Groovy source code)
   * @see Configuration
   */
  @Override
  public void run(String script, Configuration configuration) {
    /* Note to coders: this method is implemented here to allow usage of this
     * class as Groovy runner; even though in fact the class
     * MontiCoreScript.Runner does all the work. Letting MontiCore script also
     * be an implementation of the GroovyRunner interface allows for better
     * integration with the se-groovy-maven-plugin. This method should do
     * nothing more than simple delegation to the MontiCoreScript.Runner. */
    new Runner().run(script, configuration);
  }

  /**
   * Parses the given grammar file.
   *
   * @param grammar - path to the grammar file
   * @return grammar AST
   */
  public Optional<ASTMCGrammar> parseGrammar(Path grammar) {
    if(!grammar.toFile().isFile()) {
      Log.error("0xA1016 Cannot read " + grammar.toString() + " as it is not a file.");
    }
    try {
      return Grammar_WithConceptsMill.parser().parse(grammar.toString());
    } catch (IOException e) {
      Log.error("0XA0115 IOException during parsing of " + grammar.toString());
    }
    return Optional.empty();
  }

  /**
   * Parses all grammars in the given {@link MCPath}.
   *
   * @param grammarPath set of file and directory entries which are/contain
   * grammar files to be parsed
   * @return list of all successfully created grammar ASTs
   */
  public List<ASTMCGrammar> parseGrammars(MCPath grammarPath) {
    List<ASTMCGrammar> result = Lists.newArrayList();

    Iterator<Path> grammarPathIt = grammarPath.getEntries().iterator();
    while(grammarPathIt.hasNext()) {
      Path it = grammarPathIt.next();
      Optional<ASTMCGrammar> ast = parseGrammar(it);
      if(!ast.isPresent()) {
        Log.error("0xA1017 Failed to parse " + it.toString());
      } else {
        result.add(ast.get());
      }
    }

    return result;
  }

  protected Map<ASTMCGrammar, ASTCDCompilationUnit> firstPassGrammars = new LinkedHashMap<>();

  protected void storeCDForGrammar(ASTMCGrammar grammar, ASTCDCompilationUnit cdAst) {
    this.firstPassGrammars.put(grammar, cdAst);
  }

  protected ASTCDCompilationUnit getCDOfParsedGrammar(ASTMCGrammar grammar) {
    return this.firstPassGrammars.get(grammar);
  }

  protected Map<ASTMCGrammar, ASTCDCompilationUnit> firstPassSymbolGrammars = new LinkedHashMap<>();

  protected void storeSymbolCDForGrammar(ASTMCGrammar grammar, ASTCDCompilationUnit cdAst) {
    this.firstPassSymbolGrammars.put(grammar, cdAst);
  }

  protected ASTCDCompilationUnit getSymbolCDOfParsedGrammar(ASTMCGrammar grammar) {
    return this.firstPassSymbolGrammars.get(grammar);
  }

  protected Map<ASTMCGrammar, ASTCDCompilationUnit> firstPassScopeGrammars = new LinkedHashMap<>();

  protected void storeScopeCDForGrammar(ASTMCGrammar grammar, ASTCDCompilationUnit cdAst) {
    this.firstPassScopeGrammars.put(grammar, cdAst);
  }

  protected ASTCDCompilationUnit getScopeCDOfParsedGrammar(ASTMCGrammar grammar) {
    return this.firstPassScopeGrammars.get(grammar);
  }

  protected Iterable<ASTMCGrammar> getParsedGrammars() {
    return this.firstPassGrammars.keySet();
  }

  /**
   * Generates the parser for the given grammar.
   *
   * @param glex The global extension management
   * @param cds List of class diagrams (will be only one in the future)
   * @param grammar The input grammar to generate a parser for
   * @param symbolTable The global scope
   * @param handcodedPath The path to hand-coded java artifacts
   * @param outputDirectory The output directory for generated Java code
   */
  public void generateParser(GlobalExtensionManagement glex, List<ASTCDCompilationUnit> cds, ASTMCGrammar grammar,
                             IGrammar_WithConceptsGlobalScope symbolTable, MCPath handcodedPath, MCPath templatePath,
                             File outputDirectory) {
    // first cd (representing AST package) is relevant
    // -> will be only one cd in the future
    ParserGenerator.generateFullParser(glex, cds.get(0), grammar, symbolTable, handcodedPath, templatePath, outputDirectory);
  }

  /**
   * Generates the parser for the given grammar.
   *
   * @param grammar to generate the parser for
   * @param symbolTable
   * @param outputDirectory output directory for generated Java code
   */
  public void generateParser(GlobalExtensionManagement glex, ASTCDCompilationUnit astClassDiagram, ASTMCGrammar grammar,
                             IGrammar_WithConceptsGlobalScope symbolTable, MCPath handcodedPath, MCPath templatePath,
                             File outputDirectory) {
    Log.errorIfNull(
            grammar,
            "0xA4107 Parser generation can't be processed: the reference to the grammar ast is null");
    ParserGenerator.generateFullParser(glex, astClassDiagram, grammar, symbolTable, handcodedPath, templatePath, outputDirectory);
  }

  /**
   * Generates the parser for the given grammar.
   *
   * @param grammar to generate the parser for
   * @param symbolTable
   * @param outputDirectory output directory for generated Java code
   */
  public void generateParser(GlobalExtensionManagement glex, ASTMCGrammar grammar, IGrammar_WithConceptsGlobalScope symbolTable,
                             MCPath handcodedPath, MCPath templatePath, File outputDirectory,
                             boolean embeddedJavaCode, Languages lang) {
    Log.errorIfNull(
            grammar,
            "0xA4108 Parser generation can't be processed: the reference to the grammar ast is null");
    ParserGenerator.generateParser(glex, grammar, symbolTable, handcodedPath, templatePath, outputDirectory, embeddedJavaCode, lang);
  }

  /**
   * Generates the PrettyPrinter and FullPrettyPrinter for the given grammar.
   * Technically this is both a derive step and a decorate step
   * Requires the grammar AST and derived AST CD
   *
   * @param glex {@link GlobalExtensionManagement}
   * @param grammar to generate the parser for
   * @return a CD for the _prettyprint package
   */
  public ASTCDCompilationUnit generatePrettyPrinter(GlobalExtensionManagement glex, ASTMCGrammar grammar) {
    Log.errorIfNull(
            grammar,
            "0xA4109 PrettyPrinter generation can't be processed: the reference to the grammar ast is null");
    return PrettyPrinterGenerator.generatePrettyPrinter(glex, grammar);
  }


  /**
   * @param ast
   * @return
   */
  public ASTMCGrammar createSymbolsFromAST(IGrammar_WithConceptsGlobalScope globalScope, ASTMCGrammar ast) {
    // Build grammar symbol table (if not already built)
    String qualifiedGrammarName = Names.getQualifiedName(ast.getPackageList(), ast.getName());
    Optional<MCGrammarSymbol> grammarSymbol = globalScope
        .resolveMCGrammarDown(qualifiedGrammarName);

    ASTMCGrammar result = ast;

    if(grammarSymbol.isPresent()) {
      result = grammarSymbol.get().getAstNode();
    } else {
      Grammar_WithConceptsPhasedSTC stCreator = new Grammar_WithConceptsPhasedSTC();
      stCreator.createFromAST(result);
    }

    MCGrammarSymbol symbol = result.getSymbol();
    for(MCGrammarSymbol it: MCGrammarSymbolTableHelper.getAllSuperGrammars(symbol)) {
      if(!it.getFullName().equals(symbol.getFullName())) {
        Reporting.reportOpenInputFile(Optional.empty(),
            Paths.get(it.getFullName().replaceAll("\\.", "/").concat(".mc4")));
        Reporting.reportOpenInputFile(Optional.empty(),
            Paths.get(it.getFullName().replaceAll("\\.", "/").concat(".cd")));

      }
    }

    return result;
  }

  /**
   * @param ast
   * @return
   */
  public ASTCDCompilationUnit createSymbolsFromAST(ICD4AnalysisGlobalScope globalScope,
                                                   ASTCDCompilationUnit ast) {
    // Build grammar symbol table (if not already built)

    final String qualifiedCDName = Names.getQualifiedName(ast.getMCPackageDeclaration().getMCQualifiedName().getPartsList(),
        ast.getCDDefinition().getName());
    Optional<DiagramSymbol> cdSymbol = globalScope.resolveDiagramDown(
        qualifiedCDName);

    ASTCDCompilationUnit result = ast;

    if(cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().isPresentAstNode()) {
      result = (ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode();
      Log.debug("Used present symbol table for " + cdSymbol.get().getFullName(), LOG_ID);
    } else {
      ICD4CodeArtifactScope artScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(result);
      MC2CD4CodeSymbolTableCompleter completer = new MC2CD4CodeSymbolTableCompleter();
      result.accept(completer.getTraverser());
    }

    return result;
  }

  /**
   * @param ast
   * @param scope
   */
  public void runGrammarCoCos(ASTMCGrammar ast, IGrammar_WithConceptsGlobalScope scope) {
    // Run context conditions
    Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
    checker.addChecker((new GrammarCoCos()).getCoCoChecker());
    checker.checkAll(ast);
    return;
  }

  /**
   * Transforms grammar AST to class diagram AST.
   *
   * @param astGrammar - grammar AST
   * @param glex - object for managing hook points, features and global
   * variables
   * @param symbolTable - cd symbol table
   */
  public ASTCDCompilationUnit getOrCreateCD(ASTMCGrammar astGrammar,
                                            GlobalExtensionManagement glex, ICD4AnalysisGlobalScope symbolTable) {
    // transformation
    return TransformationHelper.getCDforGrammar(symbolTable, astGrammar)
            .orElse(new MC2CDTransformation(glex)
                    .apply(astGrammar));
  }

  /**
   * Transforms grammar AST to class diagram AST.
   *
   * @param astGrammar - grammar AST
   * @param glex - object for managing hook points, features and global
   * variables
   * @param cdScope - grammar symbol table
   */
  public List<ASTCDCompilationUnit> deriveCD(ASTMCGrammar astGrammar,
                                             GlobalExtensionManagement glex,
                                             ICD4AnalysisGlobalScope cdScope) {
    List<ASTCDCompilationUnit> cds = new ArrayList<ASTCDCompilationUnit>();
    cds.add(deriveASTCD(astGrammar, glex, cdScope));
    cds.add(deriveSymbolCD(astGrammar, cdScope));
    cds.add(deriveScopeCD(astGrammar, cdScope));
    cds.add(generatePrettyPrinter(glex, astGrammar));
    return cds;
  }


  /**
   * Transforms grammar AST to class diagram AST.
   *
   * @param astGrammar - grammar AST
   * @param glex - object for managing hook points, features and global
   * variables
   * @param cdScope - grammar symbol table
   */
  public ASTCDCompilationUnit deriveASTCD(ASTMCGrammar astGrammar,
                                          GlobalExtensionManagement glex,
                                          ICD4AnalysisGlobalScope cdScope) {
    // transformation
    Optional<ASTCDCompilationUnit> ast = TransformationHelper.getCDforGrammar(cdScope, astGrammar);
    ASTCDCompilationUnit astCD = ast.orElse(transformAndCreateSymbolTable(astGrammar, glex, cdScope));
    createCDSymbolsForSuperGrammars(glex, astGrammar, cdScope);
    storeCDForGrammar(astGrammar, astCD);
    return astCD;
  }

  public ASTCDCompilationUnit deriveSymbolCD(ASTMCGrammar astGrammar,
                                             ICD4AnalysisGlobalScope cdScope) {
    Optional<ASTCDCompilationUnit> ast = TransformationHelper.getCDforGrammar(cdScope, astGrammar, "Symbols");
    ASTCDCompilationUnit astCD = ast.orElse(transformAndCreateSymbolTableForSymbolCD(astGrammar, cdScope));
    createCDSymbolsForSuperGrammarsForSymbolCD(astGrammar, cdScope);
    storeSymbolCDForGrammar(astGrammar, astCD);
    return astCD;
  }

  public ASTCDCompilationUnit deriveScopeCD(ASTMCGrammar astGrammar,
                                            ICD4AnalysisGlobalScope cdScope) {
    Optional<ASTCDCompilationUnit> ast = TransformationHelper.getCDforGrammar(cdScope, astGrammar, "Scope");
    ASTCDCompilationUnit astCD = ast.orElse(transformAndCreateSymbolTableForScopeCD(astGrammar, cdScope));
    createCDSymbolsForSuperGrammarsForScopeCD(astGrammar, cdScope);
    storeScopeCDForGrammar(astGrammar, astCD);
    return astCD;
  }

  /**
   * Prints Cd4Analysis AST to the CD-file (*.cd) in the reporting directory.
   *
   * @param cds The predefined list of cds for AST, symbols, and scopes.
   * @param outputDirectory The output directory to print to
   */
  public void reportCD(List<ASTCDCompilationUnit> cds, File outputDirectory) {
    // We precisely know the structure of the given list.
    // In future versions, this will be one combined CD only.
    reportCD(cds.get(0), outputDirectory);
  }

  /**
   * Prints Cd4Analysis AST to the CD-file (*.cd) in the reporting directory.
   *
   * @param cd The predefined list of cds for AST, symbols, and scopes.
   * @param outputDirectory The output directory to print to
   */
  public void reportDecoratedCD(ASTCDCompilationUnit cd, File outputDirectory) {
    reportCD(cd, outputDirectory);
  }


  /**
   * Prints Cd4Analysis AST to the CD-file (*.cd) in the reporting directory
   *
   * @param astCd - the top node of the Cd4Analysis AST
   * @param outputDirectory - output directory
   */
  public void reportCD(ASTCDCompilationUnit astCd,
                       ASTCDCompilationUnit symbolCd,
                       File outputDirectory) {
    // we also store the class diagram fully qualified such that we can later on
    // resolve it properly for the generation of sub languages
    String reportSubDir = Joiners.DOT.join(astCd.getCDPackageList());
    if(reportSubDir.isEmpty()) {
      reportSubDir = astCd.getCDDefinition().getName();
    } else if(reportSubDir.endsWith("._ast")) {
      reportSubDir = reportSubDir.substring(0, reportSubDir.length() - 5);
    }
    reportSubDir = reportSubDir.toLowerCase();

    // Clone CD for reporting
    ASTCDCompilationUnit astCdForReporting = astCd.deepClone();

    // Change Name
    astCdForReporting.getCDDefinition().setName("DataStructure_" + astCdForReporting.getCDDefinition().getName());

    // No star imports in reporting CDs
    astCdForReporting.getMCImportStatementList().forEach(s -> s.setStar(false));

    // Remove Builder
    List<ASTCDClass> builderClasses = Lists.newArrayList();
    astCdForReporting.getCDDefinition().getCDClassesList().forEach(c -> {
      if(c.getName().endsWith("Builder")) builderClasses.add(c);
    });
    builderClasses.forEach(c -> astCdForReporting.getCDDefinition().removeCDElement(c));

    // Add symbol classes
    for(ASTCDClass cl: symbolCd.getCDDefinition().getCDClassesList()) {
      if(!cl.getName().endsWith("Builder")) {
        ASTCDClass newCl = cl.deepClone();
        astCdForReporting.getCDDefinition().addCDElement(newCl);
      }
    }

    // Remove methods and constructors
    for(ASTCDClass clazz: astCdForReporting.getCDDefinition().getCDClassesList()) {
      List<ASTCDMember> toRemove = clazz.getCDMemberList().stream().filter(c -> c instanceof ASTCDMethod || c instanceof ASTCDConstructor).collect(Collectors.toList());
      clazz.removeAllCDMembers(toRemove);
    }
    for(ASTCDInterface interf: astCdForReporting.getCDDefinition().getCDInterfacesList()) {
      List<ASTCDMember> toRemove = interf.getCDMemberList().stream().filter(c -> c instanceof ASTCDMethod || c instanceof ASTCDConstructor).collect(Collectors.toList());
      interf.removeAllCDMembers(toRemove);
    }
    for(ASTCDEnum e: astCdForReporting.getCDDefinition().getCDEnumsList()) {
      List<ASTCDMember> toRemove = e.getCDMemberList().stream().filter(c -> c instanceof ASTCDMethod || c instanceof ASTCDConstructor).collect(Collectors.toList());
      e.removeAllCDMembers(toRemove);
    }

    new CDReporting().prettyPrintAstCd(astCdForReporting, outputDirectory, reportSubDir);
  }

  /**
   * Prints Cd4Analysis AST to the CD-file (*.cd) in the reporting directory
   *
   * @param astCd - the top node of the Cd4Analysis AST
   * @param outputDirectory - output directory
   */
  public void reportCD(ASTCDCompilationUnit astCd, File outputDirectory) {
    // we also store the class diagram fully qualified such that we can later on
    // resolve it properly for the generation of sub languages
    String reportSubDir = Joiners.DOT.join(astCd.getCDPackageList());
    reportSubDir = reportSubDir.isEmpty()
            ? astCd.getCDDefinition().getName()
            : reportSubDir.concat(".").concat(astCd.getCDDefinition().getName());
    reportSubDir = reportSubDir.toLowerCase();

    // Clone CD for reporting
    ASTCDCompilationUnit astCdForReporting = astCd.deepClone();
    // No star imports in reporting CDs
    astCdForReporting.getMCImportStatementList().forEach(s -> s.setStar(false));

    new CDReporting().prettyPrintAstCd(astCdForReporting, outputDirectory, reportSubDir);
  }

  /**
   * Executes the groovy script for the specified hook point if present.
   *
   * @param file The path to the groovy script file as String
   */
  public void hook(Optional<String> file, Object... args) {
    if(file.isPresent()) {
      String script = loadScript(file.get());

      GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
              .withImportCustomizer(new ImportCustomizer().addStarImports(Runner.DEFAULT_IMPORTS)
                      .addStaticStars(Runner.DEFAULT_STATIC_IMPORTS));

      builder.addVariable("args", args);

      GroovyInterpreter g = builder.build();
      g.evaluate(script);
    }
  }

  public void configureGenerator(GlobalExtensionManagement glex, ASTCDCompilationUnit cd, MCPath templatePath) {
    String configTemplate = glex.getGlobalVar(CONFIGTEMPLATE_LONG, StringUtils.EMPTY).toString();
    GeneratorSetup setup = new GeneratorSetup();
    setup.setAdditionalTemplatePaths(templatePath.getEntries().stream().map(p -> new File(p.toUri())).collect(Collectors.toList()));
    setup.setGlex(glex);
    CD4C.init(setup);
    CD4C.getInstance().setEmptyBodyTemplate("cd2java.EmptyBody");
    if(!configTemplate.isEmpty()) {
      TemplateController tc = setup.getNewTemplateController(configTemplate);
      TemplateHookPoint hp = new TemplateHookPoint(configTemplate);
      List<Object> args = Arrays.asList(setup.getGlex(), new TemplateHPService(), cd.getCDDefinition());
      hp.processValue(tc, cd.getCDDefinition(), args);
    }
  }

  /**
   * Loads the groovy script if present.
   *
   * @param file The path to the groovy script file as String
   * @return The groovy configuration script as String
   */
  protected String loadScript(String file) {
    String script = StringUtils.EMPTY;
    try {
      File f = new File(file);
      if(f.exists() && f.isFile()) {
        script = Files.asCharSource(f, StandardCharsets.UTF_8).read();
      } else {
        ClassLoader l = MontiCoreScript.class.getClassLoader();
        if(l.getResource(file) != null) {
          script = Resources.asCharSource(l.getResource(file), StandardCharsets.UTF_8).read();
        } else {
          Log.error("0xA1059 Custom script \"" + f.getAbsolutePath() + "\" not found!");
        }
      }
    } catch(IOException e) {
      Log.error("0xA1060 Failed to load Groovy script.", e);
    }
    return script;
  }

  /**
   * Decorates the class diagrams of a given language (specified via three input
   * class diagrams) for AST, symbol table, visitor, CoCos, OD, and mill.
   *
   * @param glex The global extension management
   * @param cdScope The common scope of the class diagrams
   * @param cds The class diagrams of the AST, symbols and scope of a language
   * @param handCodedPath The path to hand-coded java artifacts
   * @return The list of decorated class diagrams
   */
  public ASTCDCompilationUnit decorateCD(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                         List<ASTCDCompilationUnit> cds, MCPath handCodedPath) {
    cds = addListSuffixToAttributeName(cds);
    prepareCD(cdScope, cds.get(0));
    prepareCD(cdScope, cds.get(1));
    prepareCD(cdScope, cds.get(2));

    // we precisely know the strucutre of the given cd list
    // in a future version, we will only handle one single cd
    ASTCDCompilationUnit decoratedCD = decorateForASTPackage(glex, cdScope, cds.get(0), handCodedPath);
    decorateForSymbolTablePackage(glex, cdScope, cds.get(0),
            cds.get(1), cds.get(2), decoratedCD, handCodedPath);
    decorateTraverserForVisitorPackage(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateForCoCoPackage(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateForUtilsPackage(glex, cds.get(0), decoratedCD);
    decorateForODPackage(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decoratePrettyPrinter(glex, cds.get(0), cdScope, cds.get(3), decoratedCD, handCodedPath);
    decorateMill(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateCLI(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateAuxiliary(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    return decoratedCD;
  }

  public ASTCDCompilationUnit decorateTagCD(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                         List<ASTCDCompilationUnit> cds, MCPath handCodedPath, ASTCDCompilationUnit decoratedCD, ASTMCGrammar astGrammar) {
    decorateTagging(glex, cdScope, cds.get(0), decoratedCD, handCodedPath, astGrammar);
    return decoratedCD;
  }


    public void decorateForSymbolTablePackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                            ASTCDCompilationUnit astClassDiagram, ASTCDCompilationUnit symbolClassDiagramm,
                                            ASTCDCompilationUnit scopeClassDiagramm,
                                            ASTCDCompilationUnit decoratedCD, MCPath handCodedPath) {
    decorateWithSymbolTable(astClassDiagram, symbolClassDiagramm, scopeClassDiagramm, decoratedCD, glex, handCodedPath);
  }

  protected void decorateWithSymbolTable(ASTCDCompilationUnit cd, ASTCDCompilationUnit symbolCD, ASTCDCompilationUnit scopeCD,
                                         ASTCDCompilationUnit decoratedCD, GlobalExtensionManagement glex,
                                         MCPath handCodedPath) {
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex, symbolTableService);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, symbolTableService);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, symbolTableService, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator = new GlobalScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeInterfaceDecorator artifactScopeInterfaceDecorator = new ArtifactScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeClassDecorator artifactScopeDecorator = new ArtifactScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolSurrogateDecorator symbolReferenceDecorator = new SymbolSurrogateDecorator(glex, symbolTableService, visitorService, methodDecorator, new MandatoryMutatorSymbolSurrogateDecorator(glex));
    SymbolSurrogateBuilderDecorator symbolReferenceBuilderDecorator = new SymbolSurrogateBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator = new SymbolResolverInterfaceDecorator(glex, symbolTableService);
    SymbolDeSerDecorator symbolDeSerDecorator = new SymbolDeSerDecorator(glex, symbolTableService, handCodedPath);
    ScopeDeSerDecorator scopeDeSerDecorator = new ScopeDeSerDecorator(glex, symbolTableService, methodDecorator, visitorService, handCodedPath);
    Symbols2JsonDecorator symbolTablePrinterDecorator = new Symbols2JsonDecorator(glex, symbolTableService, visitorService, methodDecorator, handCodedPath);
    ScopesGenitorDecorator scopesGenitorDecorator = new ScopesGenitorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator = new ScopesGenitorDelegatorDecorator(glex, symbolTableService, visitorService);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, handCodedPath, symbolTableService, symbolDecorator,
            symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
            scopeInterfaceDecorator, scopeClassDecorator,
            globalScopeInterfaceDecorator, globalScopeClassDecorator,
            artifactScopeInterfaceDecorator, artifactScopeDecorator,
            commonSymbolInterfaceDecorator,
            symbolResolverInterfaceDecorator,
            symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);
    symbolTableCDDecorator.decorate(cd, symbolCD, scopeCD, decoratedCD);
  }


  /**
   * Decorates for the visitor package. Adds corresponding traverser and
   * visitors.
   *
   * @param glex The global extension management
   * @param cdScope The scope of the cd
   * @param astClassDiagram The input class diagram, which is decorated
   * @param handCodedPath The path for entities of the TOP mechanism
   * @return A compilation unit with the decorated class diagram
   */
  public void decorateTraverserForVisitorPackage(GlobalExtensionManagement glex,
                                                 ICD4AnalysisScope cdScope,
                                                 ASTCDCompilationUnit astClassDiagram,
                                                 ASTCDCompilationUnit decoratedCD, MCPath handCodedPath) {
    decorateWithTraverser(astClassDiagram, decoratedCD, glex, handCodedPath);
  }

  /**
   * Decorates traverser and visitors.
   *
   * @param cd The input class diagram, which is decorated
   * @param glex The global extension management
   * @param handCodedPath The path for entities of the TOP mechanism
   * @return A compilation unit with the decorated class diagram
   */
  protected void decorateWithTraverser(ASTCDCompilationUnit cd,
                                       ASTCDCompilationUnit decoratedCD,
                                       GlobalExtensionManagement glex,
                                       MCPath handCodedPath) {
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);

    TraverserInterfaceDecorator iTraverserDecorator = new TraverserInterfaceDecorator(glex, visitorService, symbolTableService);
    TraverserClassDecorator traverserDecorator = new TraverserClassDecorator(glex, visitorService, symbolTableService);
    Visitor2Decorator visitor2Decorator = new Visitor2Decorator(glex, visitorService, symbolTableService);
    HandlerDecorator handlerDecorator = new HandlerDecorator(glex, visitorService, symbolTableService);
    InheritanceHandlerDecorator inheritanceHandlerDecorator = new InheritanceHandlerDecorator(glex, methodDecorator, visitorService, symbolTableService);

    CDTraverserDecorator decorator = new CDTraverserDecorator(glex, handCodedPath, visitorService, iTraverserDecorator, traverserDecorator, visitor2Decorator, handlerDecorator, inheritanceHandlerDecorator);

    decorator.decorate(cd, decoratedCD);
  }

  public void decorateForCoCoPackage(GlobalExtensionManagement glex,
                                     ICD4AnalysisScope cdScope,
                                     ASTCDCompilationUnit astClassDiagram,
                                     ASTCDCompilationUnit decoratedCD,
                                     MCPath handCodedPath) {
    decorateWithCoCo(astClassDiagram, decoratedCD, glex, handCodedPath);
  }

  protected void decorateWithCoCo(ASTCDCompilationUnit cd,
                                  ASTCDCompilationUnit decoratedCD,
                                  GlobalExtensionManagement glex,
                                  MCPath handCodedPath) {
    ASTService astService = new ASTService(cd);
    VisitorService visitorService = new VisitorService(cd);
    CoCoService coCoService = new CoCoService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex, coCoService);

    CoCoCheckerDecorator coCoCheckerDecorator = new CoCoCheckerDecorator(glex, methodDecorator, coCoService, visitorService);
    CoCoInterfaceDecorator coCoInterfaceDecorator = new CoCoInterfaceDecorator(glex, coCoService, astService);
    CoCoDecorator coCoDecorator = new CoCoDecorator(glex, handCodedPath, coCoCheckerDecorator, coCoInterfaceDecorator);

    coCoDecorator.decorate(cd, decoratedCD);
  }

  public void decorateForODPackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                   ASTCDCompilationUnit astClassDiagram,
                                   ASTCDCompilationUnit decoratedCD, MCPath handCodedPath) {
    decorateWithOD(astClassDiagram, decoratedCD, glex, handCodedPath);
  }

  protected void decorateWithOD(ASTCDCompilationUnit cd,
                                ASTCDCompilationUnit decoratedCD,
                                GlobalExtensionManagement glex, MCPath handCodedPath) {
    ODService odService = new ODService(cd);
    VisitorService visitorService = new VisitorService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex, odService);

    ODDecorator odDecorator = new ODDecorator(glex, methodDecorator, odService, visitorService);

    ODCDDecorator odcdDecorator = new ODCDDecorator(glex, odDecorator);
    odcdDecorator.decorate(cd, decoratedCD);
  }

  public void decorateForUtilsPackage(GlobalExtensionManagement glex,
                                      ASTCDCompilationUnit cd,
                                      ASTCDCompilationUnit decoratedCD) {
    decorateWithTypeDispatcher(cd, decoratedCD, glex);
  }

  protected void decorateWithTypeDispatcher(ASTCDCompilationUnit cd,
                                            ASTCDCompilationUnit decoratedCD,
                                            GlobalExtensionManagement glex) {

    TypeDispatcherDecorator dispatcherClassDecorator = new TypeDispatcherDecorator(glex,
        new VisitorService(cd),
        new SymbolTableService(cd));
    dispatcherClassDecorator.decorate(cd, decoratedCD);

    TypeDispatcherInterfaceDecorator dispatcherInterfaceDecorator
        = new TypeDispatcherInterfaceDecorator(
            glex,
        new VisitorService(cd),
        new SymbolTableService(cd));

    dispatcherInterfaceDecorator.decorate(cd, decoratedCD);
  }

  public void decorateMill(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                           ASTCDCompilationUnit cd,
                           ASTCDCompilationUnit decoratedCD, MCPath handCodedPath) {
    generateMill(cd, decoratedCD, glex, handCodedPath);
  }

  protected void generateMill(ASTCDCompilationUnit cd, ASTCDCompilationUnit decoratedCD,
                              GlobalExtensionManagement glex, MCPath handCodedPath) {
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    ParserService parserService = new ParserService(cd);
    MillDecorator millDecorator = new MillDecorator(glex, symbolTableService, visitorService, parserService);
    CDMillDecorator cdMillDecorator = new CDMillDecorator(glex, millDecorator);

    cdMillDecorator.decorate(cd, decoratedCD);
  }

  public void decorateCLI(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                          ASTCDCompilationUnit cd,
                          ASTCDCompilationUnit decoratedCD,
                          MCPath handCodedPath) {
    generateCLI(cd, decoratedCD, glex, handCodedPath);
  }

  protected void generateCLI(ASTCDCompilationUnit cd,
                             ASTCDCompilationUnit decoratedCD,
                             GlobalExtensionManagement glex, MCPath handCodedPath) {
    ParserService parserService = new ParserService(cd);
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    CLIDecorator cliDecorator = new CLIDecorator(glex, parserService, symbolTableService);
    CDCLIDecorator cdcliDecorator = new CDCLIDecorator(glex, cliDecorator, parserService);

    cdcliDecorator.decorate(cd, decoratedCD);
  }

  public void decorateAuxiliary(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                ASTCDCompilationUnit cd, ASTCDCompilationUnit decoratedCD,
                                MCPath handCodedPath) {
    generateAuxiliary(cd, decoratedCD, glex, handCodedPath);
  }

  protected void generateAuxiliary(ASTCDCompilationUnit cd, ASTCDCompilationUnit decoratedCD,
                                   GlobalExtensionManagement glex, MCPath handCodedPath) {
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    ParserService parserService = new ParserService(cd);
    MillForSuperDecorator millForSuperDecorator = new MillForSuperDecorator(glex, symbolTableService, visitorService, parserService);
    CDAuxiliaryDecorator cdAuxiliaryDecorator = new CDAuxiliaryDecorator(glex, millForSuperDecorator);

    cdAuxiliaryDecorator.decorate(cd, decoratedCD);
  }

  public void decorateTagging(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                ASTCDCompilationUnit cd, ASTCDCompilationUnit decoratedCD,
                                MCPath handCodedPath, ASTMCGrammar astTagGrammar){
    generateTagging(cd, decoratedCD, glex, handCodedPath, astTagGrammar);
  }

  protected void generateTagging(ASTCDCompilationUnit cd, ASTCDCompilationUnit decoratedCD,
                                   GlobalExtensionManagement glex, MCPath handCodedPath, ASTMCGrammar astTagGrammar) {
    ASTMCGrammar originalGrammar = getOriginalGrammarFromTagGrammar(astTagGrammar).get();

    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    ParserService parserService = new ParserService(cd);
    AbstractService abstractService = new AbstractService<>(cd);

    TaggerDecorator taggerDecorator = new TaggerDecorator(glex, abstractService, visitorService, originalGrammar);
    TagConformsToSchemaCoCoDecorator tagCoCoDecorator = new TagConformsToSchemaCoCoDecorator(glex, abstractService, visitorService, originalGrammar);
    CDTaggingDecorator taggingDecorator = new CDTaggingDecorator(glex, taggerDecorator, tagCoCoDecorator);

    taggingDecorator.decorate(cd, decoratedCD);
  }

  protected ASTCDCompilationUnit deriveTaggingCD(ASTMCGrammar astGrammar, ICD4AnalysisGlobalScope cdScope ) {
    return new MC2CDTaggingTranslation(cdScope).apply(getOriginalGrammarFromTagGrammar(astGrammar).get());
  }

  // Find & load the grammar A for given ATagDefinition grammar
  protected Optional<ASTMCGrammar> getOriginalGrammarFromTagGrammar(ASTMCGrammar astTagGrammar) {
    String originalGrammarName;
    if (astTagGrammar.getSymbol().getName().endsWith(TaggingConstants.TAGDEFINITION_SUFFIX)) {
      originalGrammarName = astTagGrammar.getSymbol().getFullName().substring(0, astTagGrammar.getSymbol().getFullName().length() - TaggingConstants.TAGDEFINITION_SUFFIX.length());
    }else if (astTagGrammar.getSymbol().getName().endsWith(TaggingConstants.TAGSCHEMA_SUFFIX)) {
      originalGrammarName = astTagGrammar.getSymbol().getFullName().substring(0, astTagGrammar.getSymbol().getFullName().length() - TaggingConstants.TAGSCHEMA_SUFFIX.length());
    }else{
      Log.error("0xA1018 Unable to generate Tagging infrastructure on non TagSchema/TagDef Grammar:" + astTagGrammar.getSymbol().getFullName());
      return Optional.empty();
    }
    Optional<MCGrammarSymbol> originalGrammarOpt = Grammar_WithConceptsMill.globalScope().resolveMCGrammar(originalGrammarName);
    if (originalGrammarOpt.isEmpty()){
      Log.error("0xA1026 Failed to resolve original grammar " + originalGrammarName + " of tag grammar " + astTagGrammar.getSymbol().getFullName());
      return Optional.empty();
    }
    return Optional.of(originalGrammarOpt.get().getAstNode());
  }


  public void decoratePrettyPrinter(GlobalExtensionManagement glex, ASTCDCompilationUnit input, ICD4AnalysisScope cdScope,
                                    ASTCDCompilationUnit prettyPrintCD, ASTCDCompilationUnit decoratedCD,
                                    MCPath handCodedPath) {
    CDPrettyPrinterDecorator prettyPrinterDecorator = new CDPrettyPrinterDecorator(glex);
    prettyPrinterDecorator.decorate(input, decoratedCD, prettyPrintCD);
  }

  /**
   * Adds the suffix "List" to all attribute names that comprise multiple
   * instances.
   *
   * @param originalCDs The list of input cds
   * @return The decorated list of output cds
   */
  public List<ASTCDCompilationUnit> addListSuffixToAttributeName(List<ASTCDCompilationUnit> originalCDs) {
    ListSuffixDecorator listSuffixDecorator = new ListSuffixDecorator();
    // decoration is only applied to the first cd, representing the AST package
    listSuffixDecorator.decorate(originalCDs.get(0), originalCDs.get(0));
    return originalCDs;
  }

  public ASTCDCompilationUnit addListSuffixToAttributeName(ASTCDCompilationUnit originalCD) {
    ListSuffixDecorator listSuffixDecorator = new ListSuffixDecorator();
    return listSuffixDecorator.decorate(originalCD, originalCD);
  }

  /**
   * Decorates class diagram AST by adding of new classes and methods using in
   * ast files
   *
   * @param glex - object for managing hook points, features and global
   * variables
   * @param astClassDiagram - class diagram AST
   */

  public ASTCDCompilationUnit decorateForASTPackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                                    ASTCDCompilationUnit astClassDiagram, MCPath handCodedPath) {
    return decorateWithAST(astClassDiagram, glex, handCodedPath);
  }

  public ASTCDCompilationUnit decorateEmfForASTPackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                                       ASTCDCompilationUnit astClassDiagram, MCPath handCodedPath) {
    return decorateEmfWithAST(astClassDiagram, glex, handCodedPath);
  }

  protected ASTCDCompilationUnit prepareCD(ICD4AnalysisScope scope, ASTCDCompilationUnit cd) {
    ASTCDCompilationUnit preparedCD = cd;

    TypeCD2JavaDecorator typeCD2JavaDecorator = new TypeCD2JavaDecorator(scope);
    preparedCD = typeCD2JavaDecorator.decorate(preparedCD);

    return preparedCD;
  }

  protected ASTCDCompilationUnit decorateWithAST(ASTCDCompilationUnit cd, GlobalExtensionManagement glex, MCPath handCodedPath) {
    ASTService astService = new ASTService(cd);
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);

    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);

    DataDecoratorUtil decoratorUtil = new DataDecoratorUtil();

    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, astService, decoratorUtil);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, astSymbolDecorator,
            astScopeDecorator, methodDecorator, symbolTableService);

    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astClassReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(glex, astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, astService), astService);
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);

    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);

    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex, astService), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService, astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, decoratorUtil, methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator,
            astBuilderDecorator, astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
    ASTCDCompilationUnit compilationUnit = astcdDecorator.decorate(cd);

    return compilationUnit;
  }

  /**
   * Decorates the class diagrams of a given language (specified via three input
   * class diagrams) for AST, symbol table, visitor, CoCos, OD, and mill with
   * EMF-compatible elements.
   *
   * @param glex The global extension management
   * @param cdScope The common scope of the class diagrams
   * @param cds The class diagrams of the AST, symbols and scope of a language
   * @param handCodedPath The path to hand-coded java artifacts
   * @return The list of decorated class diagrams
   */
  public ASTCDCompilationUnit decorateEmfCD(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                            List<ASTCDCompilationUnit> cds, MCPath handCodedPath) {
    cds = addListSuffixToAttributeName(cds);
    prepareCD(cdScope, cds.get(0));
    prepareCD(cdScope, cds.get(1));
    prepareCD(cdScope, cds.get(2));

    ASTCDCompilationUnit decoratedCD = decorateEmfForASTPackage(glex, cdScope, cds.get(0), handCodedPath);
    // we precisely know the strucutre of the given cd list
    // in a future version, we will only handle one single cd
    decorateForSymbolTablePackage(glex, cdScope, cds.get(0),
            cds.get(1), cds.get(2), decoratedCD, handCodedPath);
    decorateTraverserForVisitorPackage(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateForCoCoPackage(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateForUtilsPackage(glex, cds.get(0), decoratedCD);
    decorateForODPackage(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decoratePrettyPrinter(glex, cds.get(0), cdScope, cds.get(3), decoratedCD, handCodedPath);
    decorateMill(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateCLI(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    decorateAuxiliary(glex, cdScope, cds.get(0), decoratedCD, handCodedPath);
    return decoratedCD;
  }

  protected ASTCDCompilationUnit decorateEmfWithAST(ASTCDCompilationUnit cd, GlobalExtensionManagement glex, MCPath handCodedPath) {
    ASTService astService = new ASTService(cd);
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    EmfService emfService = new EmfService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex, emfService);
    EmfMutatorDecorator emfMutatorDecorator = new EmfMutatorDecorator(glex, astService);
    DataEmfDecorator dataEmfDecorator = new DataEmfDecorator(glex, methodDecorator, astService, new DataDecoratorUtil(), emfMutatorDecorator);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTEmfDecorator astEmfDecorator = new ASTEmfDecorator(glex, astService, visitorService,
            astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService, emfService);
    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);

    ASTFullEmfDecorator fullEmfDecorator = new ASTFullEmfDecorator(dataEmfDecorator, astEmfDecorator, astClassReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(glex, astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, emfService), astService);
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);


    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);

    EmfEnumDecorator emfEnumDecorator = new EmfEnumDecorator(glex, new AccessorDecorator(glex, emfService), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
            astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);

    PackageImplDecorator packageImplDecorator = new PackageImplDecorator(glex, new MandatoryAccessorDecorator(glex), emfService);
    PackageInterfaceDecorator packageInterfaceDecorator = new PackageInterfaceDecorator(glex, emfService);

    ASTEmfCDDecorator astcdDecorator = new ASTEmfCDDecorator(glex, fullEmfDecorator, astLanguageInterfaceDecorator, astBuilderDecorator,
            astConstantsDecorator, emfEnumDecorator, fullASTInterfaceDecorator, packageImplDecorator, packageInterfaceDecorator);
    ASTCDCompilationUnit compilationUnit = astcdDecorator.decorate(cd);

    return compilationUnit;
  }

  /**
   * Generates the Java artifacts for the given class diagrams.
   *
   * @param glex The global extension management
   * @param oldCDs The basic class diagrams for AST, symbols and scope
   * @param cd A list of input class diagrams to generate code for
   * @param outputDirectory The corresponding output directory
   * @param handcodedPath The path to hand-coded java artifacts
   */
  public void generateFromCD(GlobalExtensionManagement glex, List<ASTCDCompilationUnit> oldCDs,
                             ASTCDCompilationUnit cd, File outputDirectory, MCPath handcodedPath, MCPath templatePath) {

    // generate from CDs
    generateFromCD(glex, oldCDs.get(0), cd, outputDirectory, handcodedPath, templatePath);

  }

  public void generateFromCD(GlobalExtensionManagement glex, ASTCDCompilationUnit oldCD, ASTCDCompilationUnit decoratedCD,
                             File outputDirectory, MCPath handcodedPath, MCPath templatePath) {
    // need symboltable of the old cd
    glex.setGlobalValue("service", new AbstractService(oldCD));
    glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    final String diagramName = decoratedCD.getCDDefinition().getName();
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    setup.setHandcodedPath(handcodedPath);
    setup.setAdditionalTemplatePaths(templatePath.getEntries().stream().map(p -> new File(p.toUri())).collect(Collectors.toList()));
    setup.setModelName(diagramName);
    setup.setGlex(glex);

    TopDecorator topDecorator = new TopDecorator(handcodedPath);
    topDecorator.decorate(decoratedCD);

    CDGenerator generator = new CDGenerator(setup);
    generator.generate(decoratedCD);
  }

  /**
   * Generates the Java artifacts for the given class diagrams.
   *
   * @param glex The global extension management
   * @param oldCDs The basic class diagrams for AST, symbols and scope
   * @param cd A class diagram to generate code for
   * @param outputDirectory The corresponding output directory
   * @param handcodedPath The path to hand-coded java artifacts
   */
  public void generateEmfFromCD(GlobalExtensionManagement glex, List<ASTCDCompilationUnit> oldCDs,
                                ASTCDCompilationUnit cd, File outputDirectory, MCPath handcodedPath, MCPath templatePath) {
    // we precisely know the list of old CDs, which will be merged to a single
    // CD in the future
    // generate from CDs
    generateEmfFromCD(glex, oldCDs.get(0), cd, outputDirectory, handcodedPath, templatePath);

  }

  public void generateEmfFromCD(GlobalExtensionManagement glex, ASTCDCompilationUnit oldCD, ASTCDCompilationUnit decoratedCD,
                                File outputDirectory, MCPath handcodedPath, MCPath templatePath) {
    // need symboltable of the old cd
    glex.setGlobalValue("service", new EmfService(oldCD));
    glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    final String diagramName = decoratedCD.getCDDefinition().getName();
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    setup.setHandcodedPath(handcodedPath);
    setup.setAdditionalTemplatePaths(templatePath.getEntries().stream().map(p -> new File(p.toUri())).collect(Collectors.toList()));
    setup.setModelName(diagramName);
    setup.setGlex(glex);

    TopDecorator topDecorator = new TopDecorator(handcodedPath);
    topDecorator.decorate(decoratedCD);

    CDEmfGenerator generator = new CDEmfGenerator(setup);
    //set originalDefinition, because information is needed in template
    generator.setOriginalDefinition(oldCD.getCDDefinition().deepClone());
    generator.generate(decoratedCD);
  }

  public void generateLaunchScripts(GlobalExtensionManagement glex, File outputDirectory, String toolName) {
    freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    MontiCoreTemplateLoader templateLoader = new MontiCoreTemplateLoader(this.getClass().getClassLoader());
    configuration.setTemplateLoader(templateLoader);

    FreeMarkerTemplateEngine engine = new FreeMarkerTemplateEngine(configuration);
    ImmutableMap<String, String> data = ImmutableMap.of("toolName", toolName);

    File scriptOut = new File(outputDirectory, "scripts");

    generateLaunchScript(engine, "scripts.BatchLauncher", data, new File(scriptOut, "run.bat"));
    generateLaunchScript(engine, "scripts.ShellLauncher", data, new File(scriptOut, "run"));
  }

  private void generateLaunchScript(
          FreeMarkerTemplateEngine engine,
          String templateName,
          Object data,
          File dest
                                   ) {
    StringBuilder stringBuilder = new StringBuilder();
    engine.loadAndRun(templateName, stringBuilder, data);

    String content = stringBuilder.toString();

    try {
      Files.createParentDirs(dest);
    } catch(IOException e) {
      Log.error("0xA1028 Failed to to create parent directories for output file: " + dest.getAbsolutePath(), e);
    }

    try(BufferedWriter writer = Files.newWriter(dest, StandardCharsets.UTF_8)) {
      writer.write(content);
    } catch(IOException e) {
      Log.error("0xA1029 Writing to output file failed: " + dest.getAbsolutePath(), e);
    }
  }

  protected void createCDSymbolsForSuperGrammars(GlobalExtensionManagement glex, ASTMCGrammar astGrammar,
                                                 ICD4AnalysisGlobalScope cdScope) {
    if(astGrammar.isPresentSymbol()) {
      MCGrammarSymbol sym = astGrammar.getSymbol();
      for(MCGrammarSymbol mcgsym: MCGrammarSymbolTableHelper.getAllSuperGrammars(sym)) {
        Optional<DiagramSymbol> importedCd = cdScope.resolveDiagramDown(mcgsym.getFullName());
        if(!importedCd.isPresent() && mcgsym.isPresentAstNode()) {
          transformAndCreateSymbolTable(mcgsym.getAstNode(), glex, cdScope);
        }
      }
    }
  }

  protected void createCDSymbolsForSuperGrammarsForSymbolCD(ASTMCGrammar astGrammar,
                                                            ICD4AnalysisGlobalScope cdScope) {
    if(astGrammar.isPresentSymbol()) {
      MCGrammarSymbol sym = astGrammar.getSymbol();
      for(MCGrammarSymbol mcgsym: MCGrammarSymbolTableHelper.getAllSuperGrammars(sym)) {
        Optional<DiagramSymbol> importedCd = cdScope.resolveDiagramDown(mcgsym.getFullName());
        if(!importedCd.isPresent() && mcgsym.isPresentAstNode()) {
          transformAndCreateSymbolTableForSymbolCD(mcgsym.getAstNode(), cdScope);
        }
      }
    }
  }

  protected void createCDSymbolsForSuperGrammarsForScopeCD(ASTMCGrammar astGrammar,
                                                           ICD4AnalysisGlobalScope cdScope) {
    if(astGrammar.isPresentSymbol()) {
      MCGrammarSymbol sym = astGrammar.getSymbol();
      for(MCGrammarSymbol mcgsym: MCGrammarSymbolTableHelper.getAllSuperGrammars(sym)) {
        Optional<DiagramSymbol> importedCd = cdScope.resolveDiagramDown(mcgsym.getFullName());
        if(!importedCd.isPresent() && mcgsym.isPresentAstNode()) {
          transformAndCreateSymbolTableForScopeCD(mcgsym.getAstNode(), cdScope);
        }
      }
    }
  }

  /**
   * Transforms grammar AST to class diagram AST and create CD symbol table
   *
   * @param astGrammar - grammar AST
   * @param glex - object for managing hook points, features and global
   * variables
   * @param symbolTable grammar symbol table
   */
  protected ASTCDCompilationUnit transformAndCreateSymbolTable(ASTMCGrammar astGrammar,
                                                               GlobalExtensionManagement glex, ICD4AnalysisGlobalScope symbolTable) {
    // transformation
    ASTCDCompilationUnit compUnit = new MC2CDTransformation(glex).apply(astGrammar);
    return createSymbolsFromAST(symbolTable, compUnit);
  }

  protected ASTCDCompilationUnit transformAndCreateSymbolTableForSymbolCD(ASTMCGrammar astGrammar, ICD4AnalysisGlobalScope symbolTable) {
    // transformation
    ASTCDCompilationUnit compUnit = new MC2CDSymbolTranslation().apply(astGrammar);
    return createSymbolsFromAST(symbolTable, compUnit);
  }

  protected ASTCDCompilationUnit transformAndCreateSymbolTableForScopeCD(ASTMCGrammar astGrammar, ICD4AnalysisGlobalScope symbolTable) {
    // transformation
    ASTCDCompilationUnit compUnit = new MC2CDScopeTranslation().apply(astGrammar);
    return createSymbolsFromAST(symbolTable, compUnit);
  }

  public ICD4AnalysisGlobalScope createCD4AGlobalScope(MCPath modelPath) {
    CD4CodeMill.init();
    ICD4CodeGlobalScope scope = CD4CodeMill.globalScope();
    // reset global scope
    scope.clear();
    BuiltInTypes.addBuiltInTypes(scope);

    // Set ModelPath
    scope.setSymbolPath(modelPath);
    return scope;  }

  public IGrammar_WithConceptsGlobalScope createMCGlobalScope(MCPath modelPath) {
    Grammar_WithConceptsMill.init();
    IGrammar_WithConceptsGlobalScope scope = Grammar_WithConceptsMill.globalScope();
    // reset global scope
    scope.clear();
    BasicSymbolsMill.initializePrimitives();

    // Set ModelPath
    scope.setSymbolPath(modelPath);
    return scope;
  }

  public void generateDSTLanguage(ASTMCGrammar astGrammar, File grammarOut, MCPath modelPathHC) {
    DSTLGenScript dstlgenUtil = new DSTLGenScript();
    // D1 Initialize glex and generator
    GlobalExtensionManagement dstlGlex = dstlgenUtil.initGlex(astGrammar);

    // D2 Parse TF grammar extension
    Optional<ASTMCGrammar> gext = dstlgenUtil.parseGrammarHC(astGrammar, modelPathHC);

    // D3 Generate grammar
    dstlgenUtil.generateDSTL(astGrammar, gext, dstlGlex, grammarOut);
  }

  public void generateDSTInfrastructure(ASTMCGrammar astTRGrammar, File out, MCPath modelPathHC) {
    if(!astTRGrammar.getSymbol().getName().endsWith("TR") || astTRGrammar.getPackageList().stream().noneMatch(p -> p.equals("tr"))) {
      Log.error("0xA1018 Unable to generate DST infrastructure on non-TR Grammar:" + astTRGrammar.getSymbol().getFullName());
      return;
    }
    // a TR grammar has the fqn format: package.tr.NameTR, which we reverse here to result in package.Name
    String originalGrammarName = astTRGrammar.getSymbol().getFullName().substring(0, astTRGrammar.getSymbol().getFullName().length() - 2).replace("tr.", "");
    Optional<MCGrammarSymbol> originalGrammarOpt = Grammar_WithConceptsMill.globalScope().resolveMCGrammar(originalGrammarName);
    if(originalGrammarOpt.isEmpty()) {
      Log.error("0xA1026 Failed to resolve original grammar " + originalGrammarName + " of TRGrammar " + astTRGrammar.getSymbol().getFullName());
      return;
    }
    ASTMCGrammar astGrammar = originalGrammarOpt.get().getAstNode();
    DSTLGenScript dstlgenUtil = new DSTLGenScript();
    // D1 Initialize glex and generator
    GlobalExtensionManagement dstlGlex = dstlgenUtil.initGlex(astGrammar);

    GeneratorEngine dstlGenerator = dstlgenUtil.initGenerator(dstlGlex, out);

    // No D2, D3: TR Grammar is already present (see generateDSTLanguage)

    // D4 Generate context conditions
    dstlgenUtil.generateDSTLCoCos(astGrammar, dstlGenerator, modelPathHC, dstlGlex);

    // D5 Generate DSTL to ODRule translator
    dstlgenUtil.generateTranslator(astGrammar, dstlGenerator, modelPathHC);

    // D6 Generate TFGenTool class
    dstlgenUtil.generateTFGenToolClass(astGrammar, dstlGenerator, modelPathHC);
  }

  /**
   * Generate the TagSchema and TagDefinition grammars and output them as mc4 files
   */
  public void generateTaggingLanguages(ASTMCGrammar astGrammar, File outputDirectory, MCPath modelPathHC) throws IOException {
    TagGenerator.generateTaggingLanguages(astGrammar, outputDirectory, modelPathHC);
  }

  /**
   * Instantiates the glex and initializes it with all available default
   * options based on the current configuration.
   *
   * @param mcConfig The used input configuration
   * @return The instantiated glex with config options
   */
  public GlobalExtensionManagement initGlex(MontiCoreConfiguration mcConfig) {
    // initialize glex
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.addAfterTemplate("cd2java.Imports", new TemplateHookPoint("mc.Imports"));
    if(Reporting.isInitialized()) {
      if(mcConfig.getConfigTemplate().isPresent()) {
        String configTemplate = mcConfig.getConfigTemplate().get();
        if (configTemplate.endsWith(".ftl")) { // remove file ending
          configTemplate = configTemplate.substring(0, configTemplate.length() - 4);
        }
        glex.setGlobalValue(CONFIGTEMPLATE_LONG, configTemplate);
      }
    } else {
      Log.debug("Reporting not initialised or disabled. " +
              "No values are added to the glex.", LOG_ID);
    }
    return glex;
  }

  /**
   * @see groovy.lang.Script#run()
   */
  @Override
  public Object run() {
    return true;
  }

  /**
   * The actual Groovy runner used by MontiCore.
   */
  public static class Runner extends GroovyRunnerBase {

    /**
     * The default (Java) imports for within Groovy scripts.
     */
    public static final String[] DEFAULT_IMPORTS = {
            "mc.grammar._ast",
            "de.monticore.generating.templateengine",
            "de.monticore.codegen.cd2java.ast.cddecoration",
            "de.monticore.grammar.grammar._ast",
            "de.monticore.symboltable",
            "de.monticore.io.paths",
            "de.monticore.languages.grammar",
            "de.se_rwth.commons.logging",
            "de.monticore.generating.templateengine.reporting",
            "de.se_rwth.commons",
            "de.monticore.generating.templateengine.reporting.reporter",
            "de.monticore.incremental"};

    public static final String[] DEFAULT_STATIC_IMPORTS = {
            "de.se_rwth.commons.logging.Log",
            "de.monticore.generating.templateengine.reporting.Reporting",
            "de.se_rwth.commons.Names"};


    /**
     * @see de.se_rwth.commons.groovy.GroovyRunnerBase#doRun(java.lang.String,
     * de.se_rwth.commons.configuration.Configuration)
     */
    @Override
    protected void doRun(String script, Configuration configuration) {
      FileReaderWriter.init();

      GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
              .withScriptBaseClass(MontiCoreScript.class)
              .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS)
                      .addStaticStars(DEFAULT_STATIC_IMPORTS));

      Optional<Configuration> config = Optional.ofNullable(configuration);
      List<MCPath> mcPaths = new ArrayList<>();
      Optional<MontiCoreReports> reportsOpt = Optional.empty();

      try {
        if(config.isPresent()) {
          MontiCoreConfiguration mcConfig = MontiCoreConfiguration.withConfiguration(config.get());
          // we add the configuration object as property with a special property name
          builder.addVariable(MontiCoreConfiguration.CONFIGURATION_PROPERTY, mcConfig);

          // add everything properly typed for usage in the script

          // as currently MontiCore still can process multiple grammars,
          // we add a trailing "s" for the proper plural
          MCPath grammarsPath = mcConfig.getGrammars();
          MCPath modelPath = mcConfig.getModelPath();
          MCPath handcodedModelPath = mcConfig.getHandcodedModelPath();
          MCPath handcodedPath = mcConfig.getHandcodedPath();
          MCPath templatePath = mcConfig.getTemplatePath();

          mcPaths.add(grammarsPath);
          mcPaths.add(modelPath);
          mcPaths.add(handcodedModelPath);
          mcPaths.add(handcodedPath);
          mcPaths.add(templatePath);

          builder.addVariable(GRAMMAR_LONG + "s", grammarsPath);
          builder.addVariable(MODELPATH_LONG, modelPath);
          builder.addVariable(HANDCODEDMODELPATH_LONG, handcodedModelPath);
          builder.addVariable(GENDST_LONG, mcConfig.getGenDST().orElse(false)); // no transformation infrastructure generation by default
          builder.addVariable(GENTAG_LONG, mcConfig.getGenTag().orElse(false)); // no tagging generation by default
          builder.addVariable(OUT_LONG, mcConfig.getOut());
          builder.addVariable(TOOL_JAR_NAME_LONG, mcConfig.getToolName());
          builder.addVariable(REPORT_LONG, mcConfig.getReport());
          builder.addVariable(HANDCODEDPATH_LONG, handcodedPath);
          builder.addVariable(TEMPLATEPATH_LONG, templatePath);
          builder.addVariable(GROOVYHOOK1, mcConfig.getGroovyHook1());
          builder.addVariable(GROOVYHOOK2, mcConfig.getGroovyHook2());
          builder.addVariable("LOG_ID", LOG_ID);
          builder.addVariable("grammarIterator", grammarsPath.getEntries().iterator());
          reportsOpt = Optional.of(new MontiCoreReports(mcConfig.getOut().getAbsolutePath(),
                  mcConfig.getReport().getAbsolutePath(),
                  mcConfig.getReportPathOutput(),
                  handcodedPath, templatePath, mcConfig.getStatJson()));
          builder.addVariable("reportManagerFactory", reportsOpt.get());

          // for backward-compatibilty with outdated Maven scripts, we also add
          // the "force" parameter, which is always true
          builder.addVariable("force", true);
        }

        GroovyInterpreter g = builder.build();
        g.evaluate(script);
      } finally {
        // Close all file handles
        MCPath.closeAllJarFileSystems();
        FileReaderWriter.closeOpenedJarFiles();
        for(MCPath mcPath: mcPaths) {
          mcPath.close();
        }
        if(reportsOpt.isPresent()) {
          reportsOpt.get().close();
        }
      }
    }
  }

}
