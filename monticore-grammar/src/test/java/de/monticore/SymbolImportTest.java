/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(Grammar_WithConceptsMill.class)
public class SymbolImportTest {

  @BeforeEach
  public void init() {
    IGrammar_WithConceptsGlobalScope globalScope = Grammar_WithConceptsMill.globalScope();
    globalScope.clear();

    globalScope.getSymbolPath().addEntry(Paths.get("../monticore-grammar/src/test/resources"));
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testTestFullyQualified() throws IOException {
    // Test extends de.monticore.grammar.SamePackage, de.monticore.grammar.pack.DifferentPackage
    test("../monticore-grammar/src/test/resources/de/monticore/grammar/TestFullyQualifiedGrammar.mc4");
  }

  @Test
  public void testTestFullyQualifiedGrammarSamePackage() throws IOException {
    // Test extends SamePackage, de.monticore.grammar.pack.DifferentPackage
    test("../monticore-grammar/src/test/resources/de/monticore/grammar/TestFullyQualifiedGrammarSamePackage.mc4");
  }

  @Test
  public void testQualifiedImport() throws IOException {
    // Test
    // import de.monticore.grammar.SamePackage
    // import de.monticore.grammar.pack.DifferentPackage;
    // extends SamePackage, DifferentPackage
    test("../monticore-grammar/src/test/resources/de/monticore/grammar/TestQualifiedImportGrammar.mc4");
  }


  @Test
  public void testTestStarImportGrammar() throws IOException {
    // Test
    // import de.monticore.grammar.*
    // import de.monticore.grammar.pack.*;
    // extends SamePackage, DifferentPackage
    test("../monticore-grammar/src/test/resources/de/monticore/grammar/TestStarImportGrammar.mc4");
  }

  protected void test(String filename) throws IOException {
    Optional<ASTMCGrammar> grammarOpt = Grammar_WithConceptsMill.parser().parse(filename);
    assertTrue(grammarOpt.isPresent());
    Grammar_WithConceptsMill.scopesGenitorDelegator().createFromAST(grammarOpt.get());
    MCGrammarSymbol symbol = grammarOpt.get().getSymbol();


    String allSuperGrammars = symbol.getSuperGrammarSymbols().stream().map(MCGrammarSymbol::getFullName).collect(Collectors.joining(", "));
    String allSuperGrammarsLazy = symbol.getSuperGrammarSymbols().stream().map(MCGrammarSymbol::getFullName).collect(Collectors.joining(", "));

    // check if the surrogate is returning the correct symbol
    assertTrue(symbol.getSuperGrammarSymbols().stream().anyMatch(x -> x.getFullName().equals("de.monticore.grammar.SamePackage")), "SamePackage import failed: " + allSuperGrammars);
    assertTrue(symbol.getSuperGrammarSymbols().stream().anyMatch(x -> x.getFullName().equals("de.monticore.grammar.pack.DifferentPackage")), "DifferentPackage import failed: " + allSuperGrammars);

    // check if the surrogate is returning the correct fullname
    assertTrue(symbol.getSuperGrammarSymbols().stream().anyMatch(x -> x.getFullName().equals("de.monticore.grammar.SamePackage")), "SamePackage lazy import failed: " + allSuperGrammarsLazy);
    assertTrue(symbol.getSuperGrammarSymbols().stream().anyMatch(x -> x.getFullName().equals("de.monticore.grammar.pack.DifferentPackage")), "DifferentPackage lazy import failed: " + allSuperGrammarsLazy);

  }
}
