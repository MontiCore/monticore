/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProdExtendsNotExistingProdTest extends CocoTest {

  private final String MESSAGE = " The production Sup extends or implements the non-existent production Super";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0113.A0113";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ProdExtendsNotExistingProd());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar,ProdExtendsNotExistingProd.ERROR_CODE,MESSAGE,checker);
  }

  @Test
  public void testInvalid_b(){
    testInvalidGrammar(grammar+"a",ProdExtendsNotExistingProd.ERROR_CODE,MESSAGE,checker);
  }

  @Test
  public void testValid(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ProdExtendsNotExistingProd", checker);
  }

}
