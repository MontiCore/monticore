/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class InheritedScopePropertyTest extends CocoTest {
  
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  private final String grammar = "cocos.invalid.A0135.A0135";
  private final String grammar2 = "cocos.invalid.A0135.A0135a";
  private final String grammar3 = "cocos.invalid.A0135.A0135b";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new InheritedScopeProperty());
  }

  /**
   * implements -> from interface
   */
  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, InheritedScopeProperty.ERROR_CODE,
        String.format(InheritedScopeProperty.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, InheritedScopeProperty.ERROR_CODE,
            String.format(InheritedScopeProperty.ERROR_MSG_FORMAT, "A"), checker);
  }

  /**
   * extends -> from class
   */
  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar3, InheritedScopeProperty.ERROR_CODE,
        String.format(InheritedScopeProperty.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
