/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGenerator;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.ast.CdDecorator;
import de.monticore.codegen.cd2java.ast_emf.CdEmfDecorator;
import de.monticore.codegen.cd2java.cocos.CoCoGenerator;
import de.monticore.codegen.cd2java.od.ODGenerator;
import de.monticore.codegen.cd2java.types.TypeResolverGenerator;
import de.monticore.codegen.cd2java.visitor.VisitorGenerator;
import de.monticore.codegen.mc2cd.MC2CDTransformation;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGenerator;
import de.monticore.codegen.symboltable.SymbolTableGenerator;
import de.monticore.codegen.symboltable.SymbolTableGeneratorBuilder;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter;
import de.monticore.grammar.cocos.GrammarCoCos;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;
import de.monticore.grammar.symboltable.MontiCoreGrammarSymbolTableCreator;
import de.monticore.incremental.IncrementalChecker;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.groovy.GroovyRunnerBase;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import parser.MCGrammarParser;

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
 *
 * @author Galina Volkova, Andreas Horst
 */
public class MontiCoreScript extends Script implements GroovyRunner {
  
  /* The logger name for logging from within a Groovy script. */
  static final String LOG_ID = "MAIN";
  
  private final CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
  
  /**
   * Executes the default MontiCore Groovy script (parses grammars, generates
   * ASTs, parsers, etc.).
   *
   * @see Configuration
   * @param configuration of MontiCore for this execution
   * @see Configuration
   */
  public void run(Configuration configuration) {
    try {
      ClassLoader l = MontiCoreScript.class.getClassLoader();
      String script = Resources.asCharSource(l.getResource("de/monticore/monticore_emf.groovy"),
          Charset.forName("UTF-8")).read();
      run(script, configuration);
    }
    catch (IOException e) {
      Log.error("0xA1015 Failed to default MontiCore script.", e);
    }
  }
  
  /**
   * Executes the given Groovy script with the given
   * {@link MontiCoreConfiguration}.
   *
   * @see Configuration
   * @param configuration of MontiCore for this execution
   * @param script to execute (NOT file or path, the actual Groovy source code)
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
      error("0xA1016 Cannot read " + grammar.toString() + " as it is not a file.");
    }
    return MCGrammarParser.parse(grammar);
  }
  
  /**
   * Parses all grammars in the given {@link IterablePath}.
   *
   * @param grammarPath set of file and directory entries which are/contain
   * grammar files to be parsed
   * @return list of all successfully created grammar ASTs
   */
  public List<ASTMCGrammar> parseGrammars(IterablePath grammarPath) {
    List<ASTMCGrammar> result = Lists.newArrayList();
    
    Iterator<Path> grammarPathIt = grammarPath.getResolvedPaths();
    while (grammarPathIt.hasNext()) {
      Path it = grammarPathIt.next();
      Optional<ASTMCGrammar> ast = parseGrammar(it);
      if (!ast.isPresent()) {
        error("0xA1017 Failed to parse " + it.toString());
      }
      else {
        result.add(ast.get());
      }
    }
    
    return result;
  }
  
  protected MontiCoreConfiguration __configuration;
  
  protected Iterator<Path> grammarIterator;
  
  protected GlobalExtensionManagement glex;
  
  protected GlobalScope symbolTable;
  
  protected Map<ASTMCGrammar, ASTCDCompilationUnit> firstPassGrammars;
  
  public void initGlobals(MontiCoreConfiguration configuration) {
    this.__configuration = configuration;
    IncrementalChecker.initialize(configuration.getOut());
    enableReporting();
    this.grammarIterator = configuration.getGrammars().getResolvedPaths();
    this.glex = new GlobalExtensionManagement();
    this.symbolTable = initSymbolTable(configuration.getModelPath());
    this.firstPassGrammars = new LinkedHashMap<>();
  }
  
  protected void storeCDForGrammar(ASTMCGrammar grammar, ASTCDCompilationUnit cdAst) {
    this.firstPassGrammars.put(grammar, cdAst);
  }
  
  protected ASTCDCompilationUnit getCDOfParsedGrammar(ASTMCGrammar grammar) {
    return this.firstPassGrammars.get(grammar);
  }
  
  protected Iterable<ASTMCGrammar> getParsedGrammars() {
    return this.firstPassGrammars.keySet();
  }
  
  public boolean isUpToDate(Path grammar) {
    return IncrementalChecker.isUpToDate(grammar, __configuration.getOut(),
        __configuration.getModelPath(), __configuration.getTemplatePath(), __configuration.getHandcodedPath());
  }
  
  public void cleanUp(Path grammar) {
    IncrementalChecker.cleanUp(grammar);
  }
  
  /**
   * Generates the parser for the given grammar.
   *
   * @param grammar to generate the parser for
   * @param symbolTable
   * @param outputDirectory output directory for generated Java code
   */
  public void generateParser(GlobalExtensionManagement glex, ASTMCGrammar grammar, GlobalScope symbolTable,
      IterablePath handcodedPath, File outputDirectory) {
    Log.errorIfNull(
        grammar,
        "0xA4038 Parser generation can't be processed: the reference to the grammar ast is null");
    ParserGenerator.generateParser(glex, grammar, symbolTable, handcodedPath, outputDirectory);
  }
  
  /**
   * Generates the model language infrastructure for the given grammar (e.g.,
   * modeling language, model loader, symbols, symbol kinds, etc.)
   *
   * @param astGrammar to generate the parser for
   * @param symbolTable
   * @param astCd
   * @param outputDirectory output directory for generated Java code
   */
  public void generateSymbolTable(ASTMCGrammar astGrammar, GlobalScope symbolTable,
      ASTCDCompilationUnit astCd,
      File outputDirectory, IterablePath handcodedPath) {
    Log.errorIfNull(astGrammar);
    SymbolTableGeneratorHelper genHelper = new SymbolTableGeneratorHelper(astGrammar, symbolTable, astCd);
    SymbolTableGenerator symbolTableGenerator = new SymbolTableGeneratorBuilder().build();
    symbolTableGenerator.generate(astGrammar, genHelper, outputDirectory, handcodedPath);
  }

  /**
   * @param astClassDiagram
   * @param glex
   * @param globalScope
   * @param outputDirectory
   * @param templatePath
   */
  public void generateTypeResolvers(GlobalExtensionManagement glex, GlobalScope globalScope,
      ASTCDCompilationUnit astClassDiagram, File outputDirectory, IterablePath templatePath) {
    VisitorGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
    TypeResolverGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
  }
  
  /**
   * TODO: Write me! TODO: doc that the grammar AST reference might change
   *
   * @param ast
   * @return
   */
  public ASTMCGrammar createSymbolsFromAST(GlobalScope globalScope, ASTMCGrammar ast) {
    // Build grammar symbol table (if not already built)
    String qualifiedGrammarName = Names.getQualifiedName(ast.getPackage(), ast.getName());
    Optional<MCGrammarSymbol> grammarSymbol = globalScope
        .<MCGrammarSymbol> resolveDown(qualifiedGrammarName, MCGrammarSymbol.KIND);
        
    ASTMCGrammar result = ast;
    
    if (grammarSymbol.isPresent()) {
      result = (ASTMCGrammar) grammarSymbol.get().getAstNode().get();
    }
    else {
      MontiCoreGrammarLanguage language = new MontiCoreGrammarLanguage();
      
      ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
      resolvingConfiguration.addTopScopeResolvers(language.getResolvingFilters());
      
      MontiCoreGrammarSymbolTableCreator stCreator = language.getSymbolTableCreator(resolvingConfiguration,
          globalScope).get();
      stCreator.createFromAST(result);
      globalScope.cache(language.getModelLoader(), qualifiedGrammarName);
    }
    
    MCGrammarSymbol symbol = (MCGrammarSymbol) result.getSymbol().get();
    for (MCGrammarSymbol it : MCGrammarSymbolTableHelper.getAllSuperGrammars(symbol)) {
      if (!it.getFullName().equals(symbol.getFullName())) {
        Reporting.reportOpenInputFile(null,
            Paths.get(it.getFullName().replaceAll("\\.", "/").concat(".mc4")));
        Reporting.reportOpenInputFile(null,
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
  public ASTCDCompilationUnit createSymbolsFromAST(GlobalScope globalScope,
      ASTCDCompilationUnit ast) {
    // Build grammar symbol table (if not already built)
    
    final String qualifiedCDName = Names.getQualifiedName(ast.getPackage(), ast.getCDDefinition()
        .getName());
    Optional<CDSymbol> cdSymbol = globalScope.<CDSymbol> resolveDown(
        qualifiedCDName,
        CDSymbol.KIND);
        
    ASTCDCompilationUnit result = ast;
    
    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().getAstNode().isPresent()) {
      result = (ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode().get();
      info("Used present symbol table for " + cdSymbol.get().getFullName());
    }
    else {
      ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
      resolvingConfiguration.addTopScopeResolvers(cd4AnalysisLanguage.getResolvingFilters());
      
      CD4AnalysisSymbolTableCreator stCreator = cd4AnalysisLanguage.getSymbolTableCreator(resolvingConfiguration,
          globalScope).get();
      stCreator.createFromAST(result);
      globalScope.cache(cd4AnalysisLanguage.getModelLoader(), qualifiedCDName);
    }
    
    return result;
  }
  
  public GlobalScope initSymbolTable(ModelPath modelPath) {
    final MontiCoreGrammarLanguage mcLanguage = new MontiCoreGrammarLanguage();
    
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addTopScopeResolvers(mcLanguage.getResolvingFilters());
    resolvingConfiguration.addTopScopeResolvers(cd4AnalysisLanguage.getResolvingFilters());
    
    return new GlobalScope(modelPath, Arrays.asList(mcLanguage, cd4AnalysisLanguage), resolvingConfiguration);
  }
  
  /**
   * TODO: Write me!
   *
   * @param ast
   * @param scope
   */
  public void runGrammarCoCos(ASTMCGrammar ast, GlobalScope scope) {
    // Run context conditions
    Grammar_WithConceptsCoCoChecker checker = new GrammarCoCos().getCoCoChecker();
    checker.handle(ast);
    return;
  }
  
  /**
   * TODO: write me!
   *
   * @param grammar TODO
   * @param globalScope TODO
   * @param outputDirectory TODO
   */
  public void generateParserWrappers(GlobalExtensionManagement glex, ASTMCGrammar grammar, GlobalScope globalScope,
      IterablePath targetPath,
      File outputDirectory) {
    Log.errorIfNull(
        grammar,
        "0xA4037 Generation of parser wrappers can't be processed: the reference to the grammar ast is null");
    ParserGenerator.generateParserWrappers(glex, grammar, globalScope, targetPath, outputDirectory);
  }
  
  /**
   * Transforms grammar AST to class diagram AST.
   *
   * @param astGrammar - grammar AST
   * @param glex TODO
   * @param targetPath TODO
   */
  public ASTCDCompilationUnit transformAstGrammarToAstCd(
      GlobalExtensionManagement glex, ASTMCGrammar astGrammar, GlobalScope symbolTable,
      IterablePath targetPath) {
      
    // transformation
    ASTCDCompilationUnit compUnit = new MC2CDTransformation(glex)
        .apply(astGrammar);
        
    return compUnit;
  }
  
  /**
   * Prints Cd4Analysis AST to the CD-file (*.cd) in the subdirectory
   * {@link MontiCoreScript#DIR_REPORTS}
   *
   * @param astCd - the top node of the Cd4Analysis AST
   * @param outputDirectory - output directory
   */
  public void storeInCdFile(ASTCDCompilationUnit astCd, File outputDirectory) {
    // we also store the class diagram fully qualified such that we can later on
    // resolve it properly for the generation of sub languages
    String subDir = Joiner.on(File.separator).join(astCd.getPackage());
    GeneratorHelper.prettyPrintAstCd(astCd, outputDirectory, subDir);
    
    String fqn = Names.getQualifiedName(astCd.getPackage(), astCd.getCDDefinition().getName());
    Reporting.reportOpenInputFile(outputDirectory.toPath().toAbsolutePath(),
        Paths.get(fqn.replaceAll("\\.", "/").concat(".cd")));
  }
  
  /**
   * Prints Cd4Analysis AST to the CD-file (*.cd) in the reporting directory
   * {@link MontiCoreScript#DIR_REPORTS}
   *
   * @param astCd - the top node of the Cd4Analysis AST
   * @param outputDirectory - output directory
   */
  public void reportGrammarCd(ASTCDCompilationUnit astCd, File outputDirectory) {
    // we also store the class diagram fully qualified such that we can later on
    // resolve it properly for the generation of sub languages
    String reportSubDir = Joiners.DOT.join(astCd.getPackage());
    reportSubDir = reportSubDir.isEmpty()
        ? astCd.getCDDefinition().getName()
        : reportSubDir.concat(".").concat(astCd.getCDDefinition().getName());
    
    // Write reporting CD
    ASTCDCompilationUnit astCdForReporting = new AstGeneratorHelper(astCd, symbolTable).getASTCDForReporting();
    // No star imports in reporting CDs
    astCdForReporting.getImportStatements().forEach(s -> s.setStar(false));
    GeneratorHelper.prettyPrintAstCd(astCdForReporting, outputDirectory, ReportingConstants.REPORTING_DIR
        + File.separator + reportSubDir);
    
  }
  
  /**
   * Decorates class diagram AST by adding of new classes and methods using in
   * ast files TODO: rephrase!
   *
   * @param glex - object for managing hook points, features and global
   * variables
   * @param astClassDiagram - class diagram AST
   * @param targetPath the directory to produce output in
   */
  public void decorateCd(GlobalExtensionManagement glex,
      ASTCDCompilationUnit astClassDiagram, GlobalScope symbolTable, IterablePath targetPath) {
    boolean emfCompatible = false;
    createCdDecorator(glex, symbolTable, targetPath, emfCompatible).decorate(astClassDiagram);
  }
  
  /**
   * Generates ast files for the given class diagram AST TODO: rephrase!
   *
   * @param glex - object for managing hook points, features and global
   * variables
   * @param astClassDiagram - class diagram AST
   * @param outputDirectory TODO
   */
  public void generate(GlobalExtensionManagement glex, GlobalScope globalScope,
      ASTCDCompilationUnit astClassDiagram, File outputDirectory, IterablePath templatePath) {
    boolean emfCompatible = false;
    AstGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory, templatePath,
        emfCompatible);
    VisitorGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
    CoCoGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
    ODGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
  }
  
  /**
   * Decorates class diagram AST by adding of new classes and methods using in
   * ast files TODO: rephrase!
   *
   * @param glex - object for managing hook points, features and global
   * variables
   * @param astClassDiagram - class diagram AST
   * @param targetPath the directory to produce output in
   */
  public void decorateEmfCd(GlobalExtensionManagement glex,
      ASTCDCompilationUnit astClassDiagram, GlobalScope symbolTable, IterablePath targetPath) {
    boolean emfCompatible = true;
    createCdDecorator(glex, symbolTable, targetPath, emfCompatible).decorate(astClassDiagram);
  }
  
  /**
   * Generates ast files for the given class diagram AST TODO: rephrase!
   *
   * @param glex - object for managing hook points, features and global
   * variables
   * @param astClassDiagram - class diagram AST
   * @param outputDirectory TODO
   */
  public void generateEmfCompatible(GlobalExtensionManagement glex, GlobalScope globalScope,
      ASTCDCompilationUnit astClassDiagram, File outputDirectory, IterablePath templatePath) {
    boolean emfCompatible = true;
    AstGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory, templatePath,
        emfCompatible);
    VisitorGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
    CoCoGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
    ODGenerator.generate(glex, globalScope, astClassDiagram, outputDirectory);
  }
  
  /**
   * Creates instance of the {@link CdDecorator} 
   * 
   * @param glex
   * @param symbolTable
   * @param targetPath
   * @param emfCompatible - create the CdDecorator for the emf compatible code
   * @return
   */
  private CdDecorator createCdDecorator(GlobalExtensionManagement glex, GlobalScope symbolTable,
      IterablePath targetPath, boolean emfCompatible) {
    if (emfCompatible) {
      return new CdEmfDecorator(glex, symbolTable, targetPath);
    }
    return new CdDecorator(glex, symbolTable, targetPath);
  }
  
  // #######################
  // log functions
  // #######################
  
  public boolean isDebugEnabled() {
    return Log.isDebugEnabled(LOG_ID);
  }
  
  /**
   * @see Log#debug(String, String)
   * @param msg
   */
  public void debug(String msg) {
    Log.debug(msg, LOG_ID);
  }
  
  /**
   * @see Log#debug(String, Throwable, String)
   * @param msg
   * @param t
   */
  public void debug(String msg, Throwable t) {
    Log.debug(msg, t, LOG_ID);
  }
  
  /**
   * @see Log#isInfoEnabled(String) TODO: Write me!
   * @return
   */
  public boolean isInfoEnabled() {
    return Log.isInfoEnabled(LOG_ID);
  }
  
  /**
   * @see Log#info(String, String)
   * @param msg
   */
  public void info(String msg) {
    Log.info(msg, LOG_ID);
  }
  
  /**
   * @see Log#info(String, Throwable, String)
   * @param msg
   * @param t
   */
  public void info(String msg, Throwable t) {
    Log.info(msg, t, LOG_ID);
  }
  
  /**
   * @see Log#warn(String)
   * @param msg
   */
  public void warn(String msg) {
    Log.warn(msg);
  }
  
  /**
   * @see Log#warn(String, Throwable)
   * @param msg
   * @param t
   */
  public void warn(String msg, Throwable t) {
    Log.warn(msg, t);
  }
  
  /**
   * @see Log#error(String)
   * @param msg
   */
  public void error(String msg) {
    Log.error(msg);
  }
  
  /**
   * @see Log#error(String, Throwable)
   * @param msg
   * @param t
   */
  public void error(String msg, Throwable t) {
    Log.error(msg, t);
  }
  
  /**
   * @see Log#enableFailQuick(boolean)
   * @param enable
   */
  public void enableFailQuick(boolean enable) {
    Log.enableFailQuick(enable);
  }
  
  /**
   * @see Log#getErrorCount()
   * @return
   */
  public long getErrorCount() {
    return Log.getErrorCount();
  }
  
  // #######################
  // log functions
  // #######################
  
  /**
   * The global reporting initialization. This method configures reporting
   * (i.e., configuring the output directory and all active reporters).
   * 
   * @param outputDir
   */
  public void enableReporting() {
    // TODO AHo: document!
    InputOutputFilesReporter.resetModelToArtifactMap();
    // ##
    MontiCoreReports reports = new MontiCoreReports(__configuration.getOut().getAbsolutePath(),
        __configuration.getHandcodedPath(), __configuration.getTemplatePath());
    Reporting.init(__configuration.getOut().getAbsolutePath(), reports);
  }
  
  /**
   * Causes the reporting system to flush its reports (i.e., to write their
   * gathered reports to disk).
   * 
   * @param ast the grammar for which to flush the reporting
   */
  public void flushReporting(ASTMCGrammar ast) {
    Reporting.flush(ast);
  }
  
  /**
   * Disables reporting.
   * 
   * @return the name of the model for which reporting was previously active
   * @see MontiCoreScript#startReportingFor(ASTMCGrammar, Path)
   */
  public String reportingOff() {
    return Reporting.off();
  }
  
  /**
   * Initializes reporting for a particular model (in this case grammar). This
   * method also serves as an initial reporting hook for reporting the parsing
   * of the main model (grammar) file.
   * 
   * @param grammar for which to report
   * @param grammarInput path to the artifact containing the grammar
   * @return whether reporting is activated
   */
  public boolean startReportingFor(ASTMCGrammar grammar, Path grammarInput) {
    String fqn = Names.getQualifiedName(grammar.getPackage(), grammar.getName());
    boolean result = Reporting.on(fqn);
    Reporting.reportParseInputFile(grammarInput, fqn);
    return result;
  }
  
  /**
   * Reporting is done per model (in this case grammar). This method provides
   * the switch to determine for which model (grammar) reporting should occur.
   * 
   * @param grammar to report for after invokation of this method
   * @return
   */
  public boolean reportingFor(ASTMCGrammar grammar, File outputDirectory) {
    reportGrammarCd(getCDOfParsedGrammar(grammar), outputDirectory);
    String fqn = Names.getQualifiedName(grammar.getPackage(), grammar.getName());
    return Reporting.on(fqn);
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
   *
   * @author (last commit) $Author$
   * @version $Revision$, $Date$
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
        "de.monticore.languages.grammar" };
        
    /**
     * @see de.se_rwth.commons.groovy.GroovyRunnerBase#doRun(java.lang.String,
     * de.se_rwth.commons.configuration.Configuration)
     */
    @Override
    protected void doRun(String script, Configuration configuration) {
      GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
          .withScriptBaseClass(MontiCoreScript.class)
          .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
          
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
        builder.addVariable(MontiCoreConfiguration.Options.FORCE.toString(),
            mcConfig.getForce());
        builder.addVariable(MontiCoreConfiguration.Options.HANDCODEDPATH.toString(),
            mcConfig.getHandcodedPath());
        builder.addVariable(MontiCoreConfiguration.Options.TEMPLATEPATH.toString(),
            mcConfig.getTemplatePath());
      }
      
      GroovyInterpreter g = builder.build();
      g.evaluate(script);
    }
    
  }
  
}
