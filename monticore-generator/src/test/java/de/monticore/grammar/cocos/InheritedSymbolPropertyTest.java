/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class InheritedSymbolPropertyTest extends CocoTest {
  
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  private final String grammar = "cocos.invalid.A0125.A0125";
  private final String grammar2 = "cocos.invalid.A0125.A0125a";
  private final String grammar3 = "cocos.invalid.A0125.A0125b";


  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new InheritedSymbolProperty());
  }

  /**
   * implements -> from interface
   */
  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, InheritedSymbolProperty.ERROR_CODE,
        String.format(InheritedSymbolProperty.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, InheritedSymbolProperty.ERROR_CODE,
            String.format(InheritedSymbolProperty.ERROR_MSG_FORMAT, "A"), checker);
  }

  /**
   * extends -> from class
   */
  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar3, InheritedSymbolProperty.ERROR_CODE,
        String.format(InheritedSymbolProperty.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
