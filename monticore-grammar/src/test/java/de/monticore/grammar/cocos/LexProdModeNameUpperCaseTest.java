/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class LexProdModeNameUpperCaseTest extends CocoTest{
  private final String MESSAGE = " The used Mode M, should only consist of Upper-case letters.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0136.A0136";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new LexProdModeNameUpperCase());
  }

  @Test
  public void testUpperCasedPackage() {
    testInvalidGrammar(grammar, LexProdModeNameUpperCase.ERROR_CODE,
                         String.format(LexProdModeNameUpperCase.ERROR_MSG_FORMAT,
                              "de.monticore.grammar.cocos.invalid.A0136"), checker);
  }



}
