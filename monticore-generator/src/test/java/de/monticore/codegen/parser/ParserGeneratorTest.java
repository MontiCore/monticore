/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import de.monticore.MontiCoreScript;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsPhasedSTC;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsArtifactScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TestHelper.createGlobalScope;
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

  @BeforeEach
  public void setupMill(){
    LogStub.init();
    Log.enableFailQuick(false);
    Grammar_WithConceptsMill.reset();
    CD4CodeMill.reset();
    Grammar_WithConceptsMill.init();
    createGlobalScope(new MCPath(Paths.get("src/test/resources")));
    CD4CodeMill.init();
  }


  @BeforeEach
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
    Assertions.assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    createSymbolsFromAST(Grammar_WithConceptsMill.globalScope(), ast.get());
    ParserGenerator.generateParser(glex, grammar, Grammar_WithConceptsMill.globalScope(), new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    Assertions.assertEquals(0, Log.getFindingsCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExpressionParserGeneration() throws IOException {
    Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
    Optional<ASTMCGrammar> ast = parser
        .parse("src/test/resources/de/monticore/expression/Expression.mc4");
    Assertions.assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    createSymbolsFromAST(Grammar_WithConceptsMill.globalScope(), ast.get());
    ParserGenerator.generateParser(glex, grammar, Grammar_WithConceptsMill.globalScope(), new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    Assertions.assertEquals(0, Log.getFindingsCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCdAttributesParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/CdAttributes.mc4").getAbsolutePath()));
    Assertions.assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    createSymbolsFromAST(Grammar_WithConceptsMill.globalScope(), ast.get());
    ParserGenerator.generateParser(glex, grammar, Grammar_WithConceptsMill.globalScope(), new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    Assertions.assertEquals(0, Log.getFindingsCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubsubgrammarParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4").getAbsolutePath()));
    Assertions.assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    createSymbolsFromAST(Grammar_WithConceptsMill.globalScope(), ast.get());
    ParserGenerator.generateParser(glex, grammar, Grammar_WithConceptsMill.globalScope(), new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    Assertions.assertEquals(0, Log.getFindingsCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubgrammarParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/inherited/sub/Subgrammar.mc4").getAbsolutePath()));
    Assertions.assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    createSymbolsFromAST(Grammar_WithConceptsMill.globalScope(), ast.get());
    ParserGenerator.generateParser(glex, grammar, Grammar_WithConceptsMill.globalScope(), new MCPath(), new MCPath(),
            new File("target/generated-test-sources/parsertest"));
    Assertions.assertEquals(0, Log.getFindingsCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
   }


  @Test
  public void testActionParserGeneration() {
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/Action.mc4").getAbsolutePath()));
    Assertions.assertTrue(ast.isPresent());
    ASTMCGrammar grammar = ast.get();

    createSymbolsFromAST(Grammar_WithConceptsMill.globalScope(), ast.get());
    ParserGenerator.generateParser(glex, grammar, Grammar_WithConceptsMill.globalScope(), new MCPath(), new MCPath(),
        new File("target/generated-test-sources/parsertest"));
    Assertions.assertEquals(0, Log.getFindingsCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  private ASTMCGrammar createSymbolsFromAST(IGrammar_WithConceptsGlobalScope globalScope, ASTMCGrammar ast) {
    // Build grammar symbol table (if not already built)
    String qualifiedGrammarName = Names.getQualifiedName(ast.getPackageList(), ast.getName());
    Optional<MCGrammarSymbol> grammarSymbol = globalScope
        .resolveMCGrammarDown(qualifiedGrammarName);

    ASTMCGrammar result = ast;

    if (grammarSymbol.isPresent()) {
      result = grammarSymbol.get().getAstNode();
    } else {

      Grammar_WithConceptsPhasedSTC stCreator = new Grammar_WithConceptsPhasedSTC();
      IGrammar_WithConceptsArtifactScope artScope = stCreator.createFromAST(result);
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
