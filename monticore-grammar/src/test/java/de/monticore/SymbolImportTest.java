/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolSurrogate;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class SymbolImportTest {

  @BeforeEach
  public void init() {
    Log.init();
    LogStub.enableFailQuick(false);

    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();

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
    Assertions.assertTrue(grammarOpt.isPresent());
    Grammar_WithConceptsMill.scopesGenitorDelegator().createFromAST(grammarOpt.get());
    MCGrammarSymbol symbol = grammarOpt.get().getSymbol();

    for (MCGrammarSymbolSurrogate surrogate : symbol.getSuperGrammars()) {
      Assertions.assertTrue(surrogate.checkLazyLoadDelegate(), "Unable to lazy load delegate " + surrogate.getName() + " of " + surrogate.getEnclosingScope());
    }

    String allSuperGrammars = symbol.getSuperGrammars().stream().map(MCGrammarSymbol::getFullName).collect(Collectors.joining(", "));
    String allSuperGrammarsLazy = symbol.getSuperGrammars().stream().map(MCGrammarSymbolSurrogate::lazyLoadDelegate).map(MCGrammarSymbol::getFullName).collect(Collectors.joining(", "));

    // check if the surrogate is returning the correct symbol
    Assertions.assertTrue(symbol.getSuperGrammars().stream().anyMatch(x -> x.lazyLoadDelegate().getFullName().equals("de.monticore.grammar.SamePackage")), "SamePackage import failed: " + allSuperGrammars);
    Assertions.assertTrue(symbol.getSuperGrammars().stream().anyMatch(x -> x.lazyLoadDelegate().getFullName().equals("de.monticore.grammar.pack.DifferentPackage")), "DifferentPackage import failed: " + allSuperGrammars);

    // check if the surrogate is returning the correct fullname
    Assertions.assertTrue(symbol.getSuperGrammars().stream().anyMatch(x -> x.getFullName().equals("de.monticore.grammar.SamePackage")), "SamePackage lazy import failed: " + allSuperGrammarsLazy);
    Assertions.assertTrue(symbol.getSuperGrammars().stream().anyMatch(x -> x.getFullName().equals("de.monticore.grammar.pack.DifferentPackage")), "DifferentPackage lazy import failed: " + allSuperGrammarsLazy);

  }
}
