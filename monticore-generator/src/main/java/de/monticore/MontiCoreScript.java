/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CDGenerator;
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
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast.mill.MillDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillForSuperDecorator;
import de.monticore.codegen.cd2java._ast_emf.ASTEmfCDDecorator;
import de.monticore.codegen.cd2java._ast_emf.CDEmfGenerator;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTFullEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.DataEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageImplDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast_emf.enums.EmfEnumDecorator;
import de.monticore.codegen.cd2java._ast_emf.factory.EmfNodeFactoryDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableCDDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolReferenceDecorator;
import de.monticore.codegen.cd2java._visitor.*;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.cocos.CoCoGenerator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.data.ListSuffixDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.codegen.cd2java.od.ODGenerator;
import de.monticore.codegen.cd2java.top.TopDecorator;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.MC2CDTransformation;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.Languages;
import de.monticore.codegen.parser.ParserGenerator;
import de.monticore.codegen.symboltable.SymbolTableGenerator;
import de.monticore.codegen.symboltable.SymbolTableGeneratorBuilder;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.cocos.GrammarCoCos;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsSymbolTableCreatorDelegator;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.groovy.GroovyRunnerBase;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import parser.MCGrammarParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

  private final CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();

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
      String script = Resources.asCharSource(l.getResource("de/monticore/monticore_noemf.groovy"),
          Charset.forName("UTF-8")).read();
      run(script, configuration);
    } catch (IOException e) {
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
          Charset.forName("UTF-8")).read();
      run(script, configuration);
    } catch (IOException e) {
      Log.error("0xA1015 Failed to default MontiCore script.", e);
    }
  }

  /**
   * Executes the given Groovy script with the given
   * {@link MontiCoreConfiguration}.
   *
   * @param configuration of MontiCore for this execution
   * @param script        to execute (NOT file or path, the actual Groovy source code)
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
    if (!grammar.toFile().isFile()) {
      Log.error("0xA1016 Cannot read " + grammar.toString() + " as it is not a file.");
    }
    return MCGrammarParser.parse(grammar);
  }

  /**
   * Parses all grammars in the given {@link IterablePath}.
   *
   * @param grammarPath set of file and directory entries which are/contain
   *                    grammar files to be parsed
   * @return list of all successfully created grammar ASTs
   */
  public List<ASTMCGrammar> parseGrammars(IterablePath grammarPath) {
    List<ASTMCGrammar> result = Lists.newArrayList();

    Iterator<Path> grammarPathIt = grammarPath.getResolvedPaths();
    while (grammarPathIt.hasNext()) {
      Path it = grammarPathIt.next();
      Optional<ASTMCGrammar> ast = parseGrammar(it);
      if (!ast.isPresent()) {
        Log.error("0xA1017 Failed to parse " + it.toString());
      } else {
        result.add(ast.get());
      }
    }

    return result;
  }

  protected MontiCoreConfiguration __configuration;


  protected Map<ASTMCGrammar, ASTCDCompilationUnit> firstPassGrammars = new LinkedHashMap<>();

  protected void storeCDForGrammar(ASTMCGrammar grammar, ASTCDCompilationUnit cdAst) {
    this.firstPassGrammars.put(grammar, cdAst);
  }

  protected ASTCDCompilationUnit getCDOfParsedGrammar(ASTMCGrammar grammar) {
    return this.firstPassGrammars.get(grammar);
  }

  protected Iterable<ASTMCGrammar> getParsedGrammars() {
    return this.firstPassGrammars.keySet();
  }

  /**
   * Generates the parser for the given grammar.
   *
   * @param grammar         to generate the parser for
   * @param symbolTable
   * @param outputDirectory output directory for generated Java code
   */
  public void generateParser(GlobalExtensionManagement glex, ASTMCGrammar grammar,
                             Grammar_WithConceptsGlobalScope symbolTable,
                             IterablePath handcodedPath, File outputDirectory) {
    Log.errorIfNull(
        grammar,
        "0xA4038 Parser generation can't be processed: the reference to the grammar ast is null");
    ParserGenerator.generateFullParser(glex, grammar, symbolTable, handcodedPath, outputDirectory);
  }

  /**
   * Generates the parser for the given grammar.
   *
   * @param grammar         to generate the parser for
   * @param symbolTable
   * @param outputDirectory output directory for generated Java code
   */
  public void generateParser(GlobalExtensionManagement glex, ASTMCGrammar grammar,
                             Grammar_WithConceptsGlobalScope symbolTable,
                             IterablePath handcodedPath, File outputDirectory, boolean embeddedJavaCode, Languages lang) {
    Log.errorIfNull(
        grammar,
        "0xA4038 Parser generation can't be processed: the reference to the grammar ast is null");
    ParserGenerator.generateParser(glex, grammar, symbolTable, handcodedPath, outputDirectory, embeddedJavaCode, lang);
  }

  /**
   * Generates the model language infrastructure for the given grammar (e.g.,
   * modeling language, model loader, symbols, symbol kinds, etc.)
   *
   * @param astGrammar      to generate the parser for
   * @param astCd
   * @param outputDirectory output directory for generated Java code
   */
  public void generateSymbolTable(GlobalExtensionManagement glex,
                                  Grammar_WithConceptsGlobalScope mcGlobal, ASTMCGrammar astGrammar,
                                  CD4AnalysisGlobalScope cdGlobal, ASTCDCompilationUnit astCd,
                                  File outputDirectory, IterablePath handcodedPath) {
    Log.errorIfNull(astGrammar);
    SymbolTableGeneratorHelper genHelper = new SymbolTableGeneratorHelper(mcGlobal, astGrammar, cdGlobal, astCd);
    SymbolTableGenerator symbolTableGenerator = new SymbolTableGeneratorBuilder().build();
    symbolTableGenerator.generate(glex, astGrammar, genHelper, outputDirectory, handcodedPath);
  }

  /**
   * TODO: Write me! TODO: doc that the grammar AST reference might change
   *
   * @param ast
   * @return
   */
  public ASTMCGrammar createSymbolsFromAST(Grammar_WithConceptsGlobalScope globalScope, ASTMCGrammar ast) {
    // Build grammar symbol table (if not already built)
    String qualifiedGrammarName = Names.getQualifiedName(ast.getPackageList(), ast.getName());
    Optional<MCGrammarSymbol> grammarSymbol = globalScope
        .resolveMCGrammarDown(qualifiedGrammarName);

    ASTMCGrammar result = ast;

    if (grammarSymbol.isPresent()) {
      result = grammarSymbol.get().getAstNode().get();
    } else {
      Grammar_WithConceptsLanguage language = new Grammar_WithConceptsLanguage();

      Grammar_WithConceptsSymbolTableCreatorDelegator stCreator = language.getSymbolTableCreator(globalScope);
      stCreator.createFromAST(result);
      globalScope.cache(qualifiedGrammarName);
    }

    MCGrammarSymbol symbol = result.getSymbol();
    for (MCGrammarSymbol it : MCGrammarSymbolTableHelper.getAllSuperGrammars(symbol)) {
      if (!it.getFullName().equals(symbol.getFullName())) {
        Reporting.reportOpenInputFile(Optional.empty(),
            Paths.get(it.getFullName().replaceAll("\\.", "/").concat(".mc4")));
        Reporting.reportOpenInputFile(Optional.empty(),
            Paths.get(it.getFullName().replaceAll("\\.", "/").concat(".cd")));

      }
    }

    return result;
  }

  /**
   * TODO: Write me! TODO: doc that the grammar AST reference might change
   *
   * @param ast
   * @return
   */
  public ASTCDCompilationUnit createSymbolsFromAST(CD4AnalysisGlobalScope globalScope,
                                                   ASTCDCompilationUnit ast) {
    // Build grammar symbol table (if not already built)

    final String qualifiedCDName = Names.getQualifiedName(ast.getPackageList(), ast.getCDDefinition()
        .getName());
    Optional<CDDefinitionSymbol> cdSymbol = globalScope.resolveCDDefinitionDown(
        qualifiedCDName);

    ASTCDCompilationUnit result = ast;

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().getAstNode().isPresent()) {
      result = (ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode().get();
      Log.debug("Used present symbol table for " + cdSymbol.get().getFullName(), LOG_ID);
    } else {
      CD4AnalysisSymbolTableCreatorDelegator stCreator = cd4AnalysisLanguage.getSymbolTableCreator(globalScope);
      stCreator.createFromAST(result);
      globalScope.cache(qualifiedCDName);
    }

    return result;
  }

  /**
   * @param ast
   * @param scope
   */
  public void runGrammarCoCos(ASTMCGrammar ast, Grammar_WithConceptsGlobalScope scope) {
    // Run context conditions
    Grammar_WithConceptsCoCoChecker checker = new GrammarCoCos().getCoCoChecker();
    checker.handle(ast);
    return;
  }

  /**
   * Transforms grammar AST to class diagram AST.
   *
   * @param astGrammar  - grammar AST
   * @param glex        - object for managing hook points, features and global
   *                    variables
   * @param symbolTable - cd symbol table
   */
  public ASTCDCompilationUnit getOrCreateCD(ASTMCGrammar astGrammar,
                                            GlobalExtensionManagement glex, CD4AnalysisGlobalScope symbolTable) {
    // transformation
    return TransformationHelper.getCDforGrammar(symbolTable, astGrammar)
        .orElse(new MC2CDTransformation(glex)
            .apply(astGrammar));
  }

  /**
   * Transforms grammar AST to class diagram AST.
   *
   * @param astGrammar - grammar AST
   * @param glex       - object for managing hook points, features and global
   *                   variables
   * @param cdScope    - grammar symbol table
   */
  public ASTCDCompilationUnit deriveCD(ASTMCGrammar astGrammar,
                                       GlobalExtensionManagement glex,
                                       CD4AnalysisGlobalScope cdScope,
                                       Grammar_WithConceptsGlobalScope mcScope) {
    // transformation
    Optional<ASTCDCompilationUnit> ast = TransformationHelper.getCDforGrammar(cdScope, astGrammar);
    ASTCDCompilationUnit astCD = ast.orElse(transformAndCreateSymbolTable(astGrammar, glex, cdScope));
    createCDSymbolsForSuperGrammars(glex, astGrammar, cdScope, mcScope);
    storeCDForGrammar(astGrammar, astCD);
    return astCD;
  }

  /**
   * Prints Cd4Analysis AST to the CD-file (*.cd) in the reporting directory
   *
   * @param astCd           - the top node of the Cd4Analysis AST
   * @param outputDirectory - output directory
   */
  public void reportGrammarCd(ASTMCGrammar astCd, CD4AnalysisGlobalScope globalScope,
                              Grammar_WithConceptsGlobalScope mcScope, File outputDirectory) {
    ASTCDCompilationUnit cd = getCDOfParsedGrammar(astCd);
    // we also store the class diagram fully qualified such that we can later on
    // resolve it properly for the generation of sub languages
    String reportSubDir = Joiners.DOT.join(astCd.getPackageList());
    reportSubDir = reportSubDir.isEmpty()
        ? cd.getCDDefinition().getName()
        : reportSubDir.concat(".").concat(cd.getCDDefinition().getName());

    // Write reporting CD
    ASTCDCompilationUnit astCdForReporting = new AstGeneratorHelper(cd, globalScope, mcScope).getASTCDForReporting();
    // No star imports in reporting CDs
    astCdForReporting.getMCImportStatementList().forEach(s -> s.setStar(false));
    GeneratorHelper.prettyPrintAstCd(astCdForReporting, outputDirectory, reportSubDir);

  }


  public ASTCDCompilationUnit decorateForSymbolTablePackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                                            ASTCDCompilationUnit astClassDiagram, IterablePath handCodedPath) {
    ASTCDCompilationUnit preparedCD = prepareCD(cdScope, astClassDiagram);
    return decorateWithSymbolTable(preparedCD, glex, handCodedPath);
  }

  private ASTCDCompilationUnit decorateWithSymbolTable(ASTCDCompilationUnit cd, GlobalExtensionManagement glex,
                                                       IterablePath handCodedPath) {
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, builderDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassBuilderDecorator scopeClassBuilderDecorator = new ScopeClassBuilderDecorator(glex, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator = new GlobalScopeInterfaceDecorator(glex, symbolTableService);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, methodDecorator);
    GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator = new GlobalScopeClassBuilderDecorator(glex, symbolTableService, builderDecorator);
    ArtifactScopeDecorator artifactScopeDecorator = new ArtifactScopeDecorator(glex, symbolTableService, methodDecorator);
    ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator = new ArtifactScopeBuilderDecorator(glex, symbolTableService, builderDecorator, accessorDecorator);
    SymbolReferenceDecorator symbolReferenceDecorator = new SymbolReferenceDecorator(glex, symbolTableService, methodDecorator);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, scopeClassDecorator, scopeClassBuilderDecorator, scopeInterfaceDecorator, globalScopeInterfaceDecorator,
        globalScopeClassDecorator, globalScopeClassBuilderDecorator, artifactScopeDecorator, artifactScopeBuilderDecorator);

    ASTCDCompilationUnit visitorCompilationUnit = symbolTableCDDecorator.decorate(cd);

    TopDecorator topDecorator = new TopDecorator(handCodedPath);
    return topDecorator.decorate(visitorCompilationUnit);
  }

  public ASTCDCompilationUnit decorateForVisitorPackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                                        ASTCDCompilationUnit astClassDiagram, IterablePath handCodedPath) {
    ASTCDCompilationUnit preparedCD = prepareCD(cdScope, astClassDiagram);
    return decorateWithVisitor(preparedCD, glex, handCodedPath);
  }

  private ASTCDCompilationUnit decorateWithVisitor(ASTCDCompilationUnit cd, GlobalExtensionManagement glex,
                                                   IterablePath handCodedPath) {
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);

    ASTVisitorDecorator astVisitorDecorator = new ASTVisitorDecorator(glex, visitorService);
    SymbolVisitorDecorator symbolVisitorDecorator = new SymbolVisitorDecorator(glex, visitorService, symbolTableService);
    ScopeVisitorDecorator scopeVisitorDecorator = new ScopeVisitorDecorator(glex, visitorService, symbolTableService);
    DelegatorVisitorDecorator delegatorVisitorDecorator = new DelegatorVisitorDecorator(glex, visitorService);
    ParentAwareVisitorDecorator parentAwareVisitorDecorator = new ParentAwareVisitorDecorator(glex, visitorService);
    InheritanceVisitorDecorator inheritanceVisitorDecorator = new InheritanceVisitorDecorator(glex, visitorService);

    CDVisitorDecorator decorator = new CDVisitorDecorator(glex,
        astVisitorDecorator, symbolVisitorDecorator, scopeVisitorDecorator,
        delegatorVisitorDecorator, inheritanceVisitorDecorator, parentAwareVisitorDecorator);

    ASTCDCompilationUnit visitorCompilationUnit = decorator.decorate(cd);

    TopDecorator topDecorator = new TopDecorator(handCodedPath);
    return topDecorator.decorate(visitorCompilationUnit);
  }

  public ASTCDCompilationUnit addListSuffixToAttributeName(ASTCDCompilationUnit originalCD) {
    ListSuffixDecorator listSuffixDecorator = new ListSuffixDecorator();
    return listSuffixDecorator.decorate(originalCD, originalCD);
  }

  /**
   * Decorates class diagram AST by adding of new classes and methods using in
   * ast files TODO: rephrase!
   *
   * @param glex            - object for managing hook points, features and global
   *                        variables
   * @param astClassDiagram - class diagram AST
   */

  public ASTCDCompilationUnit decorateForASTPackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                                    ASTCDCompilationUnit astClassDiagram, IterablePath handCodedPath) {
    ASTCDCompilationUnit preparedCD = prepareCD(cdScope, astClassDiagram);
    return decorateWithAST(preparedCD, glex, handCodedPath);
  }

  public ASTCDCompilationUnit decorateEmfForASTPackage(GlobalExtensionManagement glex, ICD4AnalysisScope cdScope,
                                                       ASTCDCompilationUnit astClassDiagram, IterablePath handCodedPath) {
    ASTCDCompilationUnit preparedCD = prepareCD(cdScope, astClassDiagram);
    return decorateEmfWithAST(preparedCD, glex, handCodedPath);
  }

  private ASTCDCompilationUnit prepareCD(ICD4AnalysisScope scope, ASTCDCompilationUnit cd) {
    ASTCDCompilationUnit preparedCD = cd;

    TypeCD2JavaDecorator typeCD2JavaDecorator = new TypeCD2JavaDecorator(scope);
    preparedCD = typeCD2JavaDecorator.decorate(preparedCD);

    return preparedCD;
  }

  private ASTCDCompilationUnit decorateWithAST(ASTCDCompilationUnit cd, GlobalExtensionManagement glex, IterablePath handCodedPath) {
    ASTService astService = new ASTService(cd);
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(cd);

    MethodDecorator methodDecorator = new MethodDecorator(glex);

    DataDecoratorUtil decoratorUtil = new DataDecoratorUtil();

    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, astService, decoratorUtil);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService, astSymbolDecorator,
        astScopeDecorator, methodDecorator, symbolTableService);

    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex), astService);
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);

    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(glex, nodeFactoryService);

    MillDecorator millDecorator = new MillDecorator(glex, astService);

    MillForSuperDecorator millForSuperDecorator = new MillForSuperDecorator(glex, astService);

    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);

    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService, astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, decoratorUtil, methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator,
        astBuilderDecorator, nodeFactoryDecorator, millDecorator, millForSuperDecorator, astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
    ASTCDCompilationUnit compilationUnit = astcdDecorator.decorate(cd);

    TopDecorator topDecorator = new TopDecorator(handCodedPath);
    return topDecorator.decorate(compilationUnit);
  }


  private ASTCDCompilationUnit decorateEmfWithAST(ASTCDCompilationUnit cd, GlobalExtensionManagement glex, IterablePath handCodedPath) {
    ASTService astService = new ASTService(cd);
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(cd);
    EmfService emfService = new EmfService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    EmfMutatorDecorator emfMutatorDecorator = new EmfMutatorDecorator(glex, astService);
    DataEmfDecorator dataEmfDecorator = new DataEmfDecorator(glex, methodDecorator, astService, new DataDecoratorUtil(), emfMutatorDecorator);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTEmfDecorator astEmfDecorator = new ASTEmfDecorator(glex, astService, visitorService, nodeFactoryService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService, emfService);
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullEmfDecorator fullEmfDecorator = new ASTFullEmfDecorator(dataEmfDecorator, astEmfDecorator, astReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex), astService);
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);

    EmfNodeFactoryDecorator nodeFactoryDecorator = new EmfNodeFactoryDecorator(glex, nodeFactoryService);

    MillDecorator millDecorator = new MillDecorator(glex, astService);

    MillForSuperDecorator millForSuperDecorator = new MillForSuperDecorator(glex, astService);

    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);

    EmfEnumDecorator emfEnumDecorator = new EmfEnumDecorator(glex, new AccessorDecorator(glex), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator);

    PackageImplDecorator packageImplDecorator = new PackageImplDecorator(glex, new MandatoryAccessorDecorator(glex), emfService);
    PackageInterfaceDecorator packageInterfaceDecorator = new PackageInterfaceDecorator(glex, emfService);

    ASTEmfCDDecorator astcdDecorator = new ASTEmfCDDecorator(glex, fullEmfDecorator, astLanguageInterfaceDecorator, astBuilderDecorator, nodeFactoryDecorator,
        millDecorator, millForSuperDecorator, astConstantsDecorator, emfEnumDecorator, fullASTInterfaceDecorator, packageImplDecorator, packageInterfaceDecorator);
    ASTCDCompilationUnit compilationUnit = astcdDecorator.decorate(cd);

    TopDecorator topDecorator = new TopDecorator(handCodedPath);
    return topDecorator.decorate(compilationUnit);
  }


  public void generateFromCD(GlobalExtensionManagement glex, ASTCDCompilationUnit oldCD, ASTCDCompilationUnit decoratedCD,
                             File outputDirectory, IterablePath handcodedPath) {
    // need symboltable of the old cd
    glex.setGlobalValue("service", new AbstractService(oldCD));
    glex.setGlobalValue("astHelper", new DecorationHelper());
    glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    final String diagramName = decoratedCD.getCDDefinition().getName();
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    setup.setHandcodedPath(handcodedPath);
    setup.setModelName(diagramName);
    setup.setGlex(glex);
    CDGenerator generator = new CDGenerator(setup);
    generator.generate(decoratedCD);
  }

  public void generateEmfFromCD(GlobalExtensionManagement glex, ASTCDCompilationUnit oldCD, ASTCDCompilationUnit decoratedCD,
                                File outputDirectory, IterablePath handcodedPath) {
    // need symboltable of the old cd
    glex.setGlobalValue("service", new EmfService(oldCD));
    glex.setGlobalValue("astHelper", new DecorationHelper());
    glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    final String diagramName = decoratedCD.getCDDefinition().getName();
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory);
    setup.setHandcodedPath(handcodedPath);
    setup.setModelName(diagramName);
    setup.setGlex(glex);
    CDEmfGenerator generator = new CDEmfGenerator(setup);
    //set originalDefinition, because information is needed in template
    generator.setOriginalDefinition(oldCD.getCDDefinition().deepClone());
    generator.generate(decoratedCD);
  }


  public void generateCocos(GlobalExtensionManagement glex, CD4AnalysisGlobalScope globalScope,
                            ASTCDCompilationUnit astClassDiagram, File outputDirectory) {
    CoCoGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
  }

  public void generateODs(GlobalExtensionManagement glex, CD4AnalysisGlobalScope globalScope,
                          Grammar_WithConceptsGlobalScope mcScope,
                          ASTCDCompilationUnit astClassDiagram, File outputDirectory) {
    ODGenerator.generate(glex, astClassDiagram, globalScope, mcScope, outputDirectory);
  }


  private void createCDSymbolsForSuperGrammars(GlobalExtensionManagement glex, ASTMCGrammar astGrammar,
                                               CD4AnalysisGlobalScope cdScope,
                                               Grammar_WithConceptsGlobalScope mcScope) {
    if (astGrammar.isPresentSymbol()) {
      MCGrammarSymbol sym = astGrammar.getSymbol();
      for (MCGrammarSymbol mcgsym : MCGrammarSymbolTableHelper.getAllSuperGrammars(sym)) {
        Optional<CDDefinitionSymbol> importedCd = cdScope.resolveCDDefinitionDown(mcgsym.getFullName());
        if (!importedCd.isPresent() && mcgsym.getAstNode().isPresent()) {
          transformAndCreateSymbolTable((ASTMCGrammar) mcgsym.getAstNode().get(), glex, cdScope);
        }
      }
    }
  }

  /**
   * Transforms grammar AST to class diagram AST and create CD symbol table
   *
   * @param astGrammar  - grammar AST
   * @param glex        - object for managing hook points, features and global
   *                    variables
   * @param symbolTable grammar symbol table
   */
  private ASTCDCompilationUnit transformAndCreateSymbolTable(ASTMCGrammar astGrammar,
                                                             GlobalExtensionManagement glex, CD4AnalysisGlobalScope symbolTable) {
    // transformation
    ASTCDCompilationUnit compUnit = new MC2CDTransformation(glex).apply(astGrammar);
    return createSymbolsFromAST(symbolTable, compUnit);
  }

  public CD4AnalysisGlobalScope createCD4AGlobalScope(ModelPath modelPath) {
    return new CD4AnalysisGlobalScope(modelPath, new CD4AnalysisLanguage());
  }

  public Grammar_WithConceptsGlobalScope createMCGlobalScope(ModelPath modelPath) {
    return new Grammar_WithConceptsGlobalScope(modelPath, new Grammar_WithConceptsLanguage());
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
        "de.monticore.incremental.IncrementalChecker",
        "de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter",
        "de.se_rwth.commons.Names"};


    /**
     * @see de.se_rwth.commons.groovy.GroovyRunnerBase#doRun(java.lang.String,
     * de.se_rwth.commons.configuration.Configuration)
     */
    @Override
    protected void doRun(String script, Configuration configuration) {
      GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
          .withScriptBaseClass(MontiCoreScript.class)
          .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS)
              .addStaticStars(DEFAULT_STATIC_IMPORTS));

      Optional<Configuration> config = Optional.ofNullable(configuration);
      if (config.isPresent()) {
        MontiCoreConfiguration mcConfig = MontiCoreConfiguration.withConfiguration(config.get());
        // we add the configuration object as property with a special property
        // name
        builder.addVariable(MontiCoreConfiguration.CONFIGURATION_PROPERTY, mcConfig);

        mcConfig.getAllValues().forEach((key, value) -> builder.addVariable(key, value));

        // after adding everything we override a couple of known variable
        // bindings
        // to have them properly typed in the script
        builder.addVariable(MontiCoreConfiguration.Options.GRAMMARS.toString(),
            mcConfig.getGrammars());
        builder.addVariable(MontiCoreConfiguration.Options.MODELPATH.toString(),
            mcConfig.getModelPath());
        builder.addVariable(MontiCoreConfiguration.Options.OUT.toString(),
            mcConfig.getOut());
        builder.addVariable(MontiCoreConfiguration.Options.REPORT.toString(),
            mcConfig.getReport());
        builder.addVariable(MontiCoreConfiguration.Options.FORCE.toString(),
            mcConfig.getForce());
        builder.addVariable(MontiCoreConfiguration.Options.HANDCODEDPATH.toString(),
            mcConfig.getHandcodedPath());
        builder.addVariable(MontiCoreConfiguration.Options.TEMPLATEPATH.toString(),
            mcConfig.getTemplatePath());
        builder.addVariable("LOG_ID", LOG_ID);
        builder.addVariable("glex", new GlobalExtensionManagement());
        builder.addVariable("grammarIterator", mcConfig.getGrammars().getResolvedPaths());
        builder.addVariable("reportManagerFactory", new MontiCoreReports(mcConfig.getOut().getAbsolutePath(),
            mcConfig.getReport().getAbsolutePath(),
            mcConfig.getHandcodedPath(), mcConfig.getTemplatePath()));
      }

      GroovyInterpreter g = builder.build();
      g.evaluate(script);
    }
  }

}
