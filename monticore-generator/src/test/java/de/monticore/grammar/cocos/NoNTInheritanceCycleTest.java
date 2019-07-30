/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoNTInheritanceCycleTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4022.A4022";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo( new NoNTInheritanceCycle());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, NoNTInheritanceCycle.ERROR_CODE,
        String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "cocos.invalid.A4022.A4022.A"), checker);
  }

  @Test
  public void testInvalid2() {

    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();

    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope.resolveMCGrammar(grammar+ "b").orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());

    Log.getFindings().clear();
    checker.handle(grammarSymbol.getAstGrammar().get());

    assertEquals(2, Log.getFindings().size());
    assertEquals(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "cocos.invalid.A4022.A4022b.A"),
                 Log.getFindings().get(0).getMsg());
    assertEquals(NoNTInheritanceCycle.ERROR_CODE + String.format(NoNTInheritanceCycle.ERROR_MSG_FORMAT, "cocos.invalid.A4022.A4022b.B"),
                 Log.getFindings().get(1).getMsg());

  }

  @Test @Ignore
  public void testValid(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }

}
