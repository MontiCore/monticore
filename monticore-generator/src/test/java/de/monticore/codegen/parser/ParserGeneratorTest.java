/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.parser.antlr.AntlrTool;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
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
    ParserGenerator.generateParser(glex, grammar, symbolTable, IterablePath.empty(),
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
    ParserGenerator.generateParser(glex, grammar, symbolTable, IterablePath.empty(),
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
    ParserGenerator.generateParser(glex, grammar, symbolTable, IterablePath.empty(),
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
    ParserGenerator.generateParser(glex, grammar, symbolTable, IterablePath.empty(),
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
    ParserGenerator.generateParser(glex, grammar, symbolTable, IterablePath.empty(),
        new File("target/generated-test-sources/parsertest"));
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
    ParserGenerator.generateParser(glex, grammar, symbolTable, IterablePath.empty(),
        new File("target/generated-test-sources/parsertest"));
    String g4File = "target/generated-test-sources/parsertest/de/monticore/action/_parser/ActionAntlr.g4";
    assertTrue(ast.get().isPresentSymbol());
    String[] args = {};
    AntlrTool antlrTool = new AntlrTool(args, ast.get().getSymbol(), Paths.get(outputPath.getAbsolutePath()));
    antlrTool.parseAntlrFile(g4File);
  }

  public ASTMCGrammar createSymbolsFromAST(Grammar_WithConceptsGlobalScope globalScope, ASTMCGrammar ast) {
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
