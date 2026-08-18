/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.runtime.junit.MCAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoNTInheritanceCycleTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4022.A4022";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoNTInheritanceCycle());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, NoNTInheritanceCycle.ERROR_CODE,
        String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "de.monticore.grammar.cocos.invalid.A4022.A4022.A"), checker);
  }

  @Test
  public void testInvalid2() {

    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope.resolveMCGrammar(grammar + "b").orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());

    checker.checkAll(grammarSymbol.getAstGrammar().get());
    
    MCAssertions.assertHasFindingStartingWith(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "de.monticore.grammar.cocos.invalid.A4022.A4022b.A"));
    MCAssertions.assertHasFindingStartingWith(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "de.monticore.grammar.cocos.invalid.A4022.A4022b.B"));
  }

  @Test
  public void testValid() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }

}
