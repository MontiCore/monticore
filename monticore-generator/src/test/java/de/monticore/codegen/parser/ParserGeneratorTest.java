/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._symboltable.GrammarFamilyPhasedSTC;
import de.monticore.grammar.grammarfamily._symboltable.IGrammarFamilyArtifactScope;
import de.monticore.grammar.grammarfamily._symboltable.IGrammarFamilyGlobalScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the MontiCore parser generator.
 * generates parser files and checks the correctness of the GrammarNameAntlr.g4 file
 */

public class ParserGeneratorTest {

  private GlobalExtensionManagement glex;

  private MCPath modelPath;

  private File outputPath;

  @BeforeClass
  public static void setup(){
    Log.init();
    Log.enableFailQuick(false);
    GrammarFamilyMill.init();
  }

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    Path modelPathPath = Paths.get("src/test/resources");
    outputPath = new File("target/generated-test-sources");
    this.modelPath = new MCPath(modelPathPath, outputPath.toPath());
  }

  @Test
  public void testAutomatonSTParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/AutomatonST.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    ParserGenerator.generateParser(glex, grammar, symbolTable, new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    assertEquals(0, Log.getFindingsCount());
  }

  @Test
  public void testExpressionParserGeneration() throws IOException {
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> ast = parser
        .parse("src/test/resources/de/monticore/expression/Expression.mc4");
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    ParserGenerator.generateParser(glex, grammar, symbolTable, new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    assertEquals(0, Log.getFindingsCount());
  }

  @Test
  public void testCdAttributesParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/CdAttributes.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    ParserGenerator.generateParser(glex, grammar, symbolTable, new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    assertEquals(0, Log.getFindingsCount());
  }

  @Test
  public void testSubsubgrammarParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    ParserGenerator.generateParser(glex, grammar, symbolTable, new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    assertEquals(0, Log.getFindingsCount());
  }

  @Test
  public void testSubgrammarParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/sub/Subgrammar.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    ParserGenerator.generateParser(glex, grammar, symbolTable, new MCPath(), new MCPath(),
            new File("target/generated-test-sources/parsertest"));
    assertEquals(0, Log.getFindingsCount());
   }


  @Test
  public void testActionParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/Action.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    createSymbolsFromAST(symbolTable, ast.get());
    ParserGenerator.generateParser(glex, grammar, symbolTable, new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    assertEquals(0, Log.getFindingsCount());
    assertEquals(0, Log.getFindingsCount());
  }

  private ASTMCGrammar createSymbolsFromAST(IGrammarFamilyGlobalScope globalScope, ASTMCGrammar ast) {
    // Build grammar symbol table (if not already built)
    String qualifiedGrammarName = Names.getQualifiedName(ast.getPackageList(), ast.getName());
    Optional<MCGrammarSymbol> grammarSymbol = globalScope
        .resolveMCGrammarDown(qualifiedGrammarName);

    ASTMCGrammar result = ast;

    if (grammarSymbol.isPresent()) {
      result = grammarSymbol.get().getAstNode();
    } else {

      GrammarFamilyPhasedSTC stCreator = new GrammarFamilyPhasedSTC();
      IGrammarFamilyArtifactScope artScope = stCreator.createFromAST(result);
      globalScope.addSubScope(artScope);
      globalScope.addLoadedFile(qualifiedGrammarName);
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
