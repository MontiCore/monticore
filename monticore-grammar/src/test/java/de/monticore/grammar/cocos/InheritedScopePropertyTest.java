/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class InheritedScopePropertyTest extends CocoTest {

  private final String grammar = "de.monticore.grammar.cocos.invalid.A0135.A0135";
  private final String grammar2 = "de.monticore.grammar.cocos.invalid.A0135.A0135a";
  private final String grammar3 = "de.monticore.grammar.cocos.invalid.A0135.A0135b";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
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
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
