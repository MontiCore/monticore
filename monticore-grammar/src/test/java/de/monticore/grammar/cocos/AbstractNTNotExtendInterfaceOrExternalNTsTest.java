/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractNTNotExtendInterfaceOrExternalNTsTest extends CocoTest {
  
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2107.A2107";
  
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new AbstractNTNotExtendInterfaceOrExternalNTs());
  }
  
  @Test
  public void testExtendInterfaceNT() {
    testInvalidGrammar(grammar + "a", AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_MSG_FORMAT, "B", "interface",
            "A"), checker);
  }
  
  @Test
  public void testExtendExternalNT() {
    testInvalidGrammar(grammar + "b", AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_MSG_FORMAT, "B", "external",
            "A"), checker);
  }
  
  @Test
  public void testExtendNT() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }
}
