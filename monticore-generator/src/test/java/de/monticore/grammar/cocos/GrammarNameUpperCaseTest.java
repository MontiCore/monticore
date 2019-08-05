/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class GrammarNameUpperCaseTest extends CocoTest{

  private final String MESSAGE = " The grammar's name a4033 should start with an upper-case letter.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4033.a4033";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new GrammarNameUpperCase());
  }

  @Test
  public void testLowerCasedGrammarName() {
    testInvalidGrammar(grammar, GrammarNameUpperCase.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
