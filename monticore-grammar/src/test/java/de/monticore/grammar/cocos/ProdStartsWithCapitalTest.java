/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProdStartsWithCapitalTest extends CocoTest {

  private final String MESSAGE = " The nonterminal a should not start with a lower-case letter.";;
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4031.A4031";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ProdStartsWithCapital());
  }

  @Test
  public void testNT() {
    testInvalidGrammar(grammar + "a", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testAbstractNT() {
    testInvalidGrammar(grammar + "b", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testInterfaceNT() {
    testInvalidGrammar(grammar + "c", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testExternalNT() {
    testInvalidGrammar(grammar + "d", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testEnumNT() {
    testInvalidGrammar(grammar + "e", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testExtendInterfaceNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }
}
