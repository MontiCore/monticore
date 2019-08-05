package de.monticore;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.*;
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
import de.monticore.codegen.cd2java._cocos.CoCoCheckerDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoInterfaceDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.cocos.CoCoGenerator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.od.ODGenerator;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.cd2java.visitor.VisitorGenerator;
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
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsModelLoader;
import de.monticore.incremental.IncrementalChecker;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

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

  private final Grammar_WithConceptsGlobalScope symbolTable;

  private CD4AnalysisGlobalScope cdSymbolTable;

  private final Grammar_WithConceptsModelLoader mcModelLoader;

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

    Grammar_WithConceptsLanguage mcLanguage = new Grammar_WithConceptsLanguage();
    CD4AnalysisLanguage cd4aLanguage = new CD4AnalysisLanguage();

    this.symbolTable = new Grammar_WithConceptsGlobalScope(modelPath, mcLanguage);
    this.cdSymbolTable = new CD4AnalysisGlobalScope(modelPath, cd4aLanguage);
    this.mcModelLoader = new Grammar_WithConceptsModelLoader(mcLanguage);
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
    // initialize incremental generation; enabling of reporting;
    IncrementalChecker.initialize(out, report);
    InputOutputFilesReporter.resetModelToArtifactMap();
    Reporting.init(out.getAbsolutePath(), report.getAbsolutePath(),
        new MontiCoreReports(out.getAbsolutePath(), report.getAbsolutePath(), handcodedPath, templatePath));

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
    Optional<MCGrammarSymbol> grammarSymbol = symbolTable.resolveMCGrammarDown(qualifiedName);
    if (grammarSymbol.isPresent()) {
      return grammarSymbol.get().getAstGrammar();
    }
    return Optional.empty();
  }

  private String getQualifiedNameFromPath(Path grammar) {
    String qualifiedName = "";
    for (Path modelPath : this.modelPath.getFullPathOfEntries()) {
      if (grammar.startsWith(modelPath)) {
        qualifiedName = modelPath.relativize(grammar).toString();
        break;
      }
    }
    return Names.getPackageFromPath(FilenameUtils.removeExtension(qualifiedName));
  }

  private void runGrammarCoCos(ASTMCGrammar grammar) {
    // Run context conditions
    Grammar_WithConceptsCoCoChecker checker = new GrammarCoCos().getCoCoChecker();
    checker.handle(grammar);
  }

  private ASTCDCompilationUnit transformGrammarToCD(ASTMCGrammar astGrammar) {
    // transformation
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Optional<ASTCDCompilationUnit> ast = TransformationHelper.getCDforGrammar(cdSymbolTable, astGrammar);
    ASTCDCompilationUnit astCD = ast.orElse(transformAndCreateSymbolTable(astGrammar, glex));
    createCDSymbolsForSuperGrammars(glex, astGrammar);
    return astCD;
  }

  private ASTCDCompilationUnit transformAndCreateSymbolTable(ASTMCGrammar astGrammar,
                                                             GlobalExtensionManagement glex) {
    // transformation
    ASTCDCompilationUnit compUnit = new MC2CDTransformation(glex).apply(astGrammar);
    return createSymbolsFromAST(compUnit, cdSymbolTable);
  }

  public ASTCDCompilationUnit createSymbolsFromAST(ASTCDCompilationUnit ast, CD4AnalysisGlobalScope symbolTable) {
    // Build grammar symbol table (if not already built)

    final String qualifiedCDName = Names.getQualifiedName(ast.getPackageList(), ast.getCDDefinition()
        .getName());
    Optional<CDDefinitionSymbol> cdSymbol = symbolTable.resolveCDDefinitionDown(
        qualifiedCDName);

    ASTCDCompilationUnit result = ast;

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().getAstNode().isPresent()) {
      result = (ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode().get();
      Log.debug("Used present symbol table for " + cdSymbol.get().getFullName(), LOG_ID);
    } else {
      CD4AnalysisSymbolTableCreatorDelegator symbolTableCreator = cd4aModelLoader.getModelingLanguage().getSymbolTableCreator(cdSymbolTable);
      symbolTableCreator.createFromAST(ast);
    }

    return result;
  }

  private void createCDSymbolsForSuperGrammars(GlobalExtensionManagement glex, ASTMCGrammar astGrammar) {
    if (astGrammar.isPresentSymbol()) {
      MCGrammarSymbol sym = (MCGrammarSymbol) astGrammar.getSymbol();
      for (MCGrammarSymbol grammarSymbol : MCGrammarSymbolTableHelper.getAllSuperGrammars(sym)) {
        Optional<CDDefinitionSymbol> importedCd = cdSymbolTable.resolveCDDefinitionDown(grammarSymbol.getFullName());
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
    // old generation process
    for (Map.Entry<ASTMCGrammar, ASTCDCompilationUnit> pair : input.entrySet()) {
      ASTMCGrammar grammar = pair.getKey();
      ASTCDCompilationUnit cd = pair.getValue();

      // make sure to use the right report manager again
      Reporting.on(Names.getQualifiedName(grammar.getPackageList(), grammar.getName()));
      // reportGrammarCd(grammar, cd);

      GlobalExtensionManagement glex = new GlobalExtensionManagement();

      // M8: generate symbol table
      generateSymbolTable(glex, grammar, cd);

      // M9 Generate ast classes, visitor and context condition
      VisitorGenerator.generate(glex, cdSymbolTable, cd, out, handcodedPath);
      CoCoGenerator.generate(glex, cdSymbolTable, cd, out);
      ODGenerator.generate(glex, cd, cdSymbolTable, symbolTable, out);

      Log.info("Grammar " + grammar.getName() + " processed successfully!", LOG_ID);

      // M10: flush reporting
      Reporting.reportModelEnd(grammar.getName(), "");
      Reporting.flush(grammar);
    }

    // new generation process using decorator
    CD4AnalysisLanguage cd4aLanguage = new CD4AnalysisLanguage();
    cdSymbolTable = new CD4AnalysisGlobalScope(modelPath, cd4aLanguage);

    for (Map.Entry<ASTMCGrammar, ASTCDCompilationUnit> pair : input.entrySet()) {
      ASTCDCompilationUnit cd = pair.getValue();
      ASTCDCompilationUnit prepareCD = prepareCD(cdSymbolTable, cd);
      createSymbolsFromAST(prepareCD, cdSymbolTable);
      pair.setValue(prepareCD);
    }


    for (Map.Entry<ASTMCGrammar, ASTCDCompilationUnit> pair : input.entrySet()) {
      ASTMCGrammar grammar = pair.getKey();
      ASTCDCompilationUnit cd = pair.getValue();

      GlobalExtensionManagement glex = new GlobalExtensionManagement();
      glex.setGlobalValue("astHelper", new DecorationHelper());

      // TODO replacing old decoration process
      // Pre-transform CD to fit decoration
      Collection<ASTCDCompilationUnit> decoratedCDs = Stream.of(
          decorateWithAST(cd, glex))
          .collect(Collectors.toList());

      generate(decoratedCDs, glex);

      Log.info("Grammar " + grammar.getName() + " processed successfully!", LOG_ID);
    }
  }

  private ASTCDCompilationUnit prepareCD(ICD4AnalysisScope cdScope, ASTCDCompilationUnit cd) {
    ASTCDCompilationUnit preparedCD = cd;

    TypeCD2JavaDecorator typeCD2JavaDecorator = new TypeCD2JavaDecorator(cdScope);
    preparedCD = typeCD2JavaDecorator.decorate(preparedCD);

    return preparedCD;
  }

  private ASTCDCompilationUnit decorateWithAST(ASTCDCompilationUnit cd, GlobalExtensionManagement glex) {
    ASTService astService = new ASTService(cd);
    SymbolTableService symbolTableService = new SymbolTableService(cd);
    VisitorService visitorService = new VisitorService(cd);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(cd);
    MethodDecorator methodDecorator = new MethodDecorator(glex);

    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, new ASTService(cd), new DataDecoratorUtil());
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService, astSymbolDecorator,
        astScopeDecorator, methodDecorator,symbolTableService);

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
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), new MethodDecorator(glex), astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator);
    CD4AnalysisLanguage cd4aLanguage = new CD4AnalysisLanguage();
    CD4AnalysisGlobalScope symbolTable = new CD4AnalysisGlobalScope(modelPath, cd4aLanguage);
    CD4AnalysisSymbolTableCreatorDelegator symbolTableCreator = cd4aModelLoader.getModelingLanguage().getSymbolTableCreator(symbolTable);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, symbolTableCreator, fullDecorator, astLanguageInterfaceDecorator,
        astBuilderDecorator, nodeFactoryDecorator, millDecorator, millForSuperDecorator, astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
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
    SymbolTableGeneratorHelper genHelper = new SymbolTableGeneratorHelper(symbolTable, astGrammar, cdSymbolTable, astCd);
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
