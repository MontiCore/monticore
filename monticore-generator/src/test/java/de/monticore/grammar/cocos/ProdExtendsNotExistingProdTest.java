// (c) https://github.com/MontiCore/monticore

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProdExtendsNotExistingProdTest extends CocoTest {

  private final String MESSAGE = " The production Sup extends or implements the non-existing production Super";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0113.A0113";

  @BeforeClass
  public static void setup(){
    LogStub.init();
    LogStub.enableFailQuick(false);
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
    testValidGrammar("cocos.valid.ProdExtendsNotExistingProd", checker);
  }

}
