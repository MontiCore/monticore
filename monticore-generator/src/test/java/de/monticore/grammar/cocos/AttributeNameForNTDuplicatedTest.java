/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class AttributeNameForNTDuplicatedTest extends CocoTest {

  private final String CODE = "0xA4077";
  
  private final String MESSAGE = " The production C must not use the attribute name a for different nonterminals.";
  
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  
  private final String grammar = "cocos.invalid.A4006.A4006";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
  }

  // TODO MB: Wenn der neue Check von Joel fertig ist, kann man hier diese CoCo checken
  @Ignore
  @Test
  public void testDuplicatedAttribute() {
    Log.getFindings().clear();
    testInvalidGrammarKeepFindings(grammar, CODE, MESSAGE, checker);
  }

  @Test
  public void testAttributes() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }
  
}
