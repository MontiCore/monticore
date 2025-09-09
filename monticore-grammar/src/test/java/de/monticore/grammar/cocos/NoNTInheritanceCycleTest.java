/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    Assertions.assertNotNull(grammarSymbol);
    Assertions.assertTrue(grammarSymbol.getAstGrammar().isPresent());

    Log.getFindings().clear();
    checker.checkAll(grammarSymbol.getAstGrammar().get());

    Assertions.assertEquals(2, Log.getFindings().size());
    Assertions.assertEquals(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "de.monticore.grammar.cocos.invalid.A4022.A4022b.A"), Log.getFindings().get(0).getMsg());
    Assertions.assertEquals(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "de.monticore.grammar.cocos.invalid.A4022.A4022b.B"), Log.getFindings().get(1).getMsg());

  }

  @Test
  public void testValid() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }

}
