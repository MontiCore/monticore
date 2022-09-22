/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class LeftRecursiveRulesInBlockTest extends CocoTest {

  private final String MESSAGE =  " The left recursive rule A is not allowed in blocks, because it is not supported in Antlr. ";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4056.A4056";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new LeftRecursiveRulesInBlock());
  }

  @Test
  public void testSimpleLeftRecursion() {
    testInvalidGrammar(grammar, LeftRecursiveRulesInBlock.ERROR_CODE,
        String.format(MESSAGE, "interface"), checker);
  }
  
  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
