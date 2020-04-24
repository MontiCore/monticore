/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import com.google.common.base.Joiner;
import de.monticore.MontiCoreScript;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.parser.antlr.AntlrTool;
import de.monticore.codegen.parser.antlr.Grammar2Antlr;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsSymbolTableCreatorDelegator;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for the MontiCore parser generator.
 * generates parser files and checks the correctness of the GrammarNameAntlr.g4 file
 */

public class ParserGeneratorTest {

  private GlobalExtensionManagement glex;

  private ModelPath modelPath;

  private File outputPath;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    Path modelPathPath = Paths.get("src/test/resources");
    outputPath = new File("target/generated-test-sources");
    this.modelPath = new ModelPath(modelPathPath, outputPath.toPath());
  }

  @Test
  public void testAutomatonSTParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/AutomatonST.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    generateG4File(glex, grammar, symbolTable,
        new File("target/generated-test-sources/parsertest"));
    String g4File = "target/generated-test-sources/parsertest/de/monticore/automatonst/_parser/AutomatonSTAntlr.g4";
    assertTrue(ast.get().isPresentSymbol());
    String[] args = {};
    AntlrTool antlrTool = new AntlrTool(args, ast.get().getSymbol(), Paths.get(outputPath.getAbsolutePath()));
    antlrTool.parseAntlrFile(g4File);
  }

  @Test
  public void testExpressionParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/expression/Expression.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    generateG4File(glex, grammar, symbolTable,
        new File("target/generated-test-sources/parsertest"));
    String g4File = "target/generated-test-sources/parsertest/de/monticore/expression/expression/_parser/ExpressionAntlr.g4";
    assertTrue(ast.get().isPresentSymbol());
    String[] args = {};
    AntlrTool antlrTool = new AntlrTool(args, ast.get().getSymbol(), Paths.get(outputPath.getAbsolutePath()));
    antlrTool.parseAntlrFile(g4File);
  }

  @Test
  public void testCdAttributesParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/CdAttributes.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    generateG4File(glex, grammar, symbolTable,
        new File("target/generated-test-sources/parsertest"));
    String g4File = "target/generated-test-sources/parsertest/de/monticore/cdattributes/_parser/CdAttributesAntlr.g4";
    assertTrue(ast.get().isPresentSymbol());
    String[] args = {};
    AntlrTool antlrTool = new AntlrTool(args, ast.get().getSymbol(), Paths.get(outputPath.getAbsolutePath()));
    antlrTool.parseAntlrFile(g4File);
  }

  @Test
  public void testSubsubgrammarParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    generateG4File(glex, grammar, symbolTable,
        new File("target/generated-test-sources/parsertest"));
    String g4File = "target/generated-test-sources/parsertest/de/monticore/inherited/subsub/subsubgrammar/_parser/SubsubgrammarAntlr.g4";
    assertTrue(ast.get().isPresentSymbol());
    String[] args = {};
    AntlrTool antlrTool = new AntlrTool(args, ast.get().getSymbol(), Paths.get(outputPath.getAbsolutePath()));
    antlrTool.parseAntlrFile(g4File);
  }

  @Test
  public void testSubgrammarParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/sub/Subgrammar.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    generateG4File(glex, grammar, symbolTable, new File("target/generated-test-sources/parsertest"));
    String g4File = "target/generated-test-sources/parsertest/de/monticore/inherited/sub/subgrammar/_parser/SubgrammarAntlr.g4";
    assertTrue(ast.get().isPresentSymbol());
    String[] args = {};
    AntlrTool antlrTool = new AntlrTool(args, ast.get().getSymbol(), Paths.get(outputPath.getAbsolutePath()));
    antlrTool.parseAntlrFile(g4File);
  }


  @Test
  public void testActionParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/Action.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    generateG4File(glex, grammar, symbolTable,
        new File("target/generated-test-sources/parsertest"));
    String g4File = "target/generated-test-sources/parsertest/de/monticore/action/_parser/ActionAntlr.g4";
    assertTrue(ast.get().isPresentSymbol());
    String[] args = {};
    AntlrTool antlrTool = new AntlrTool(args, ast.get().getSymbol(), Paths.get(outputPath.getAbsolutePath()));
    antlrTool.parseAntlrFile(g4File);
  }

  /**
   * only generate g4 file so that no compilation errors occur in generated-test-sources
   */
  private static void generateG4File(
      GlobalExtensionManagement glex,
      ASTMCGrammar astGrammar,
      Grammar_WithConceptsGlobalScope symbolTable,
      File targetDir) {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir);

    String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
        ? astGrammar.getName()
        : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
        astGrammar.getName());
    MCGrammarSymbol grammarSymbol = symbolTable. resolveMCGrammar(
        qualifiedGrammarName).orElse(null);
    assertNotNull(grammarSymbol);

    MCGrammarInfo grammarInfo = new MCGrammarInfo(grammarSymbol);

    ParserGeneratorHelper genHelper = new ParserGeneratorHelper(astGrammar, grammarInfo,  true, Languages.JAVA);
    glex.setGlobalValue("parserHelper", genHelper);
    glex.setGlobalValue("nameHelper", new Names());
    setup.setGlex(glex);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getParserPackage()),
        astGrammar.getName() + "Antlr.g4");
    new GeneratorEngine(setup).generate("parser.Parser", filePath, astGrammar,
        new Grammar2Antlr(genHelper,
            grammarInfo, true));
  }


  private ASTMCGrammar createSymbolsFromAST(Grammar_WithConceptsGlobalScope globalScope, ASTMCGrammar ast) {
    // Build grammar symbol table (if not already built)
    String qualifiedGrammarName = Names.getQualifiedName(ast.getPackageList(), ast.getName());
    Optional<MCGrammarSymbol> grammarSymbol = globalScope
        .resolveMCGrammarDown(qualifiedGrammarName);

    ASTMCGrammar result = ast;

    if (grammarSymbol.isPresent()) {
      result = grammarSymbol.get().getAstNode();
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

}
