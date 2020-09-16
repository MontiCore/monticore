// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExternalNTNoASTRuleTest extends CocoTest {

  private static final String grammar = "cocos.invalid.A4118.A4118";

  private static final String MESSAGE = " The external production A must not have a corresponding ASTRule";

  private static Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new ExternalNTNoASTRule());
  }

  @Test
  public void testValidModel(){
    testValidGrammar("cocos.valid.Component", checker);
  }

  @Test
  public void testInvalidModel(){
    testInvalidGrammar(grammar, ExternalNTNoASTRule.ERROR_CODE, MESSAGE, checker);
  }
}
