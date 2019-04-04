package de.monticore;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.CDGenerator;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.ast_new.*;
import de.monticore.codegen.cd2java.ast_new.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.cocos_new.CoCoCheckerDecorator;
import de.monticore.codegen.cd2java.cocos_new.CoCoDecorator;
import de.monticore.codegen.cd2java.cocos_new.CoCoInterfaceDecorator;
import de.monticore.codegen.cd2java.cocos_new.CoCoService;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java.factory.NodeFactoryService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.mill.MillDecorator;
import de.monticore.codegen.cd2java.od.ODGenerator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.visitor.VisitorGenerator;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.codegen.mc2cd.MC2CDTransformation;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.ParserGenerator;
import de.monticore.codegen.symboltable.SymbolTableGenerator;
import de.monticore.codegen.symboltable.SymbolTableGeneratorBuilder;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter;
import de.monticore.grammar.cocos.GrammarCoCos;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;
import de.monticore.grammar.symboltable.MontiCoreGrammarModelLoader;
import de.monticore.incremental.IncrementalChecker;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MontiCoreTool {

  private static final String LOG_ID = MontiCoreTool.class.getCanonicalName();

  private final IterablePath grammars;

  private final ModelPath modelPath;

  private final File out;

  private final File report;

  private final boolean force;

  private final IterablePath handcodedPath;

  private final IterablePath templatePath;

  private final ResolvingConfiguration resolvingConfiguration;

  private final GlobalScope symbolTable;

  private final MontiCoreGrammarModelLoader mcModelLoader;

  private final CD4AnalysisModelLoader cd4aModelLoader;

  public MontiCoreTool(MontiCoreConfiguration configuration) {
    this(configuration.getGrammars(), configuration.getModelPath(),
        configuration.getOut(), configuration.getReport(), configuration.getForce(),
        configuration.getHandcodedPath(), configuration.getTemplatePath());
  }

  public MontiCoreTool(IterablePath grammars, ModelPath modelPath,
      File out, File report, boolean force,
      IterablePath handcodedPath, IterablePath templatePath) {
    this.grammars = grammars;
    this.modelPath = modelPath;
    this.out = out;
    this.report = report;
    this.force = force;
    this.handcodedPath = handcodedPath;
    this.templatePath = templatePath;

    MontiCoreGrammarLanguage mcLanguage = new MontiCoreGrammarLanguage();
    CD4AnalysisLanguage cd4aLanguage = new CD4AnalysisLanguage();

    this.resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(mcLanguage.getResolvingFilters());
    resolvingConfiguration.addDefaultFilters(cd4aLanguage.getResolvingFilters());

    this.symbolTable =  new GlobalScope(modelPath, Arrays.asList(mcLanguage, cd4aLanguage), resolvingConfiguration);
    this.mcModelLoader = new MontiCoreGrammarModelLoader(mcLanguage);
    this.cd4aModelLoader = new CD4AnalysisModelLoader(cd4aLanguage);
  }

  public void execute() {
    // M1: configuration object "_configuration" prepared externally
    Log.debug("--------------------------------", LOG_ID);
    Log.debug("MontiCore", LOG_ID);
    Log.debug(" - eating your models since 2005", LOG_ID);
    Log.debug("--------------------------------", LOG_ID);
    Log.debug("Grammar files       : " + grammars, LOG_ID);
    Log.debug("Modelpath           : " + modelPath, LOG_ID);
    Log.debug("Output dir          : " + out, LOG_ID);
    Log.debug("Report dir          : " + report, LOG_ID);
    Log.debug("Handcoded files     : " + handcodedPath, LOG_ID);
    Log.debug("Template files      : " + templatePath, LOG_ID);

    // M1  basic setup and initialization:
    // initialize incremental generation; enabling of reporting; create global scope
    IncrementalChecker.initialize(out, report);
    InputOutputFilesReporter.resetModelToArtifactMap();

    // the first pass processes all input grammars up to transformation to CD and storage of the resulting CD to disk
    Map<ASTMCGrammar, ASTCDCompilationUnit> transformedGrammars = loadAndTransformMCGrammarToCD();

    // the second pass
    // do the rest which requires already created CDs of possibly
    // local super grammars etc.
    generate(transformedGrammars);
  }

  private Map<ASTMCGrammar, ASTCDCompilationUnit> loadAndTransformMCGrammarToCD() {
    Map<ASTMCGrammar, ASTCDCompilationUnit> result = new HashMap<>();
    Iterator<Path> grammarIterator = grammars.getResolvedPaths();
    while (grammarIterator.hasNext()) {
      Path input = grammarIterator.next();
      if (force || !IncrementalChecker.isUpToDate(input, modelPath, templatePath, handcodedPath)) {
        IncrementalChecker.cleanUp(input);

        // M2: parse grammar
        Optional<ASTMCGrammar> astGrammarOpt = loadGrammar(input);

        if (astGrammarOpt.isPresent()) {
          ASTMCGrammar astGrammar = astGrammarOpt.get();

          // start reporting
          String grammarName = Names.getQualifiedName(astGrammar.getPackageList(), astGrammar.getName());
          Reporting.on(grammarName);
          Reporting.reportModelStart(astGrammar, grammarName, "");

          Reporting.reportParseInputFile(input, grammarName);

          // M3: populate symbol table
          //astGrammar = createSymbolsFromAST(globalScope, astGrammar);

          // M4: execute context conditions
          runGrammarCoCos(astGrammar);

          // M5: transform grammar AST into Class Diagram AST
          ASTCDCompilationUnit cd = transformGrammarToCD(astGrammar);

          // M6: generate parser and wrapper
          generateParser(astGrammar);

          // store grammar and related cd
          result.put(astGrammar, cd);
        }
      }
    }
    return result;
  }

  private Optional<ASTMCGrammar> loadGrammar(Path grammar) {
    if (!grammar.toFile().isFile()) {
      Log.error("0xA1016 Cannot read " + grammar.toString() + " as it is not a file.");
    }
    String qualifiedName = getQualifiedNameFromPath(grammar);
    return mcModelLoader.loadModelIntoScope(qualifiedName, modelPath, symbolTable, resolvingConfiguration);
  }

  private String getQualifiedNameFromPath(Path grammar) {
    return grammar.toAbsolutePath().toString();
  }

  private void runGrammarCoCos(ASTMCGrammar grammar) {
    // Run context conditions
    Grammar_WithConceptsCoCoChecker checker = new GrammarCoCos().getCoCoChecker();
    checker.handle(grammar);
  }

  private ASTCDCompilationUnit transformGrammarToCD(ASTMCGrammar astGrammar) {
    // transformation
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Optional<ASTCDCompilationUnit> ast = TransformationHelper.getCDforGrammar(symbolTable, astGrammar);
    ASTCDCompilationUnit astCD = ast.orElse(transformAndCreateSymbolTable(astGrammar, glex));
    createCDSymbolsForSuperGrammars(glex, astGrammar);
    return astCD;
  }

  private ASTCDCompilationUnit transformAndCreateSymbolTable(ASTMCGrammar astGrammar,
      GlobalExtensionManagement glex) {
    // transformation
    ASTCDCompilationUnit compUnit = new MC2CDTransformation(glex).apply(astGrammar);
    return createSymbolsFromAST(compUnit);
  }

  public ASTCDCompilationUnit createSymbolsFromAST(ASTCDCompilationUnit ast) {
    // Build grammar symbol table (if not already built)

    final String qualifiedCDName = Names.getQualifiedName(ast.getPackageList(), ast.getCDDefinition()
        .getName());
    Optional<CDSymbol> cdSymbol = symbolTable.resolveDown(
        qualifiedCDName,
        CDSymbol.KIND);

    ASTCDCompilationUnit result = ast;

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().getAstNode().isPresent()) {
      result = (ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode().get();
      Log.debug("Used present symbol table for " + cdSymbol.get().getFullName(), LOG_ID);
    }
    else {
      Optional<ASTCDCompilationUnit> cdOpt = cd4aModelLoader.loadModelIntoScope(qualifiedCDName, modelPath, symbolTable, resolvingConfiguration);
      if (cdOpt.isPresent()) {
        result = cdOpt.get();
      }
    }

    return result;
  }

  private void createCDSymbolsForSuperGrammars(GlobalExtensionManagement glex, ASTMCGrammar astGrammar) {
    if (astGrammar.isPresentSymbol()) {
      MCGrammarSymbol sym = (MCGrammarSymbol) astGrammar.getSymbol();
      for (MCGrammarSymbol grammarSymbol : MCGrammarSymbolTableHelper.getAllSuperGrammars(sym)) {
        Optional<CDSymbol> importedCd = symbolTable.resolveDown(grammarSymbol.getFullName(), CDSymbol.KIND);
        if (!importedCd.isPresent() && grammarSymbol.getAstNode().isPresent()) {
          transformAndCreateSymbolTable((ASTMCGrammar) grammarSymbol.getAstNode().get(), glex);
        }
      }
    }
  }

  public void generateParser(ASTMCGrammar grammar) {
    Log.errorIfNull(grammar, "0xA4038 Parser generation can't be processed: the reference to the grammar ast is null");
    ParserGenerator.generateFullParser(new GlobalExtensionManagement(), grammar, symbolTable, handcodedPath, out);
  }

  private void generate(Map<ASTMCGrammar, ASTCDCompilationUnit> input) {
    for (Map.Entry<ASTMCGrammar, ASTCDCompilationUnit> pair : input.entrySet()) {
      ASTMCGrammar grammar = pair.getKey();
      ASTCDCompilationUnit cd = pair.getValue();

      // make sure to use the right report manager again
      Reporting.on(Names.getQualifiedName(grammar.getPackageList(), grammar.getName()));
      reportGrammarCd(grammar, cd);

      GlobalExtensionManagement glex = new GlobalExtensionManagement();

      // M7: decorate Class Diagram AST
      //TODO add visitor + od decorator
      Collection<ASTCDCompilationUnit> decoratedCDs = Stream.of(
          decorateWithAST(cd.deepClone(), glex),
          decorateWithCoCos(cd.deepClone(), glex))
          .collect(Collectors.toList());

      //TODO replace symbol table generation with ST decorator
      // M8: generate symbol table
      generateSymbolTable(glex, grammar, cd);

      // M9 Generate ast classes, visitor and context condition
      generate(decoratedCDs, glex);
      GlobalExtensionManagement old_glex = new GlobalExtensionManagement();
      VisitorGenerator.generate(old_glex, symbolTable, cd, out);
      ODGenerator.generate(old_glex, symbolTable, cd, out);

      Log.info("Grammar " + grammar.getName() + " processed successfully!", LOG_ID);

      // M10: flush reporting
      Reporting.reportModelEnd(grammar.getName(), "");
      Reporting.flush(grammar);
    }
  }

  private void reportGrammarCd(ASTMCGrammar astCd, ASTCDCompilationUnit cd) {
    // we also store the class diagram fully qualified such that we can later on
    // resolve it properly for the generation of sub languages
    String reportSubDir = Joiners.DOT.join(astCd.getPackageList());
    reportSubDir = reportSubDir.isEmpty()
        ? cd.getCDDefinition().getName()
        : reportSubDir.concat(".").concat(cd.getCDDefinition().getName());

    // Write reporting CD
    ASTCDCompilationUnit astCdForReporting = new AstGeneratorHelper(cd, symbolTable).getASTCDForReporting();
    // No star imports in reporting CDs
    astCdForReporting.getImportStatementList().forEach(s -> s.setStar(false));
    GeneratorHelper.prettyPrintAstCd(astCdForReporting, out, reportSubDir);
  }

  private ASTCDCompilationUnit decorateWithAST(ASTCDCompilationUnit cd, GlobalExtensionManagement glex) {
    ASTService astService = new ASTService(cd);
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(cd);

    DataDecorator dataDecorator = new DataDecorator(glex, new MethodDecorator(glex), new ASTService(cd));
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astSymbolDecorator, astScopeDecorator, astReferencedSymbolDecorator);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new MethodDecorator(glex));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);

    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(glex);

    MillDecorator millDecorator = new MillDecorator(glex);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astBuilderDecorator, nodeFactoryDecorator, millDecorator);
    return astcdDecorator.decorate(cd);
  }

  private ASTCDCompilationUnit decorateWithCoCos(ASTCDCompilationUnit cd, GlobalExtensionManagement glex) {
    CoCoService cocoService = new CoCoService(cd);
    VisitorService visitorService = new VisitorService(cd);
    ASTService astService = new ASTService(cd);

    MethodDecorator methodDecorator = new MethodDecorator(glex);
    CoCoCheckerDecorator cocoCheckerDecorator = new CoCoCheckerDecorator(glex, methodDecorator, cocoService, visitorService);
    CoCoInterfaceDecorator cocoInterfaceDecorator = new CoCoInterfaceDecorator(glex, cocoService, astService);
    CoCoDecorator cocoDecorator = new CoCoDecorator(glex, cocoCheckerDecorator, cocoInterfaceDecorator);
    return cocoDecorator.decorate(cd);
  }

  private void generateSymbolTable(GlobalExtensionManagement glex, ASTMCGrammar astGrammar, ASTCDCompilationUnit astCd) {
    Log.errorIfNull(astGrammar);
    SymbolTableGeneratorHelper genHelper = new SymbolTableGeneratorHelper(astGrammar, symbolTable, astCd);
    SymbolTableGenerator symbolTableGenerator = new SymbolTableGeneratorBuilder().build();
    symbolTableGenerator.generate(glex, astGrammar, genHelper, out, handcodedPath);
  }

  private void generate(Collection<ASTCDCompilationUnit> cds, GlobalExtensionManagement glex) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(out);
    setup.setHandcodedPath(handcodedPath);
    setup.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
    setup.setGlex(glex);
    CDGenerator generator = new CDGenerator(setup);
    cds.forEach(generator::generate);
  }
}
