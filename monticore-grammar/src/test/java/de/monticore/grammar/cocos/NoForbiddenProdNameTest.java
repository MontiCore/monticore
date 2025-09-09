/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoForbiddenProdNameTest extends CocoTest{

  private final String MESSAGE = " There must not exist a production with the name %s in the grammar %s.";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoForbiddenProdName());
  }

  @Test
  public void testInvalid1(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4096.A4096a";
    String message = String.format(MESSAGE, "ConstantsA4096a", "A4096a");
    testInvalidGrammar(grammar, NoForbiddenProdName.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid2(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4096.A4096b";
    String message = String.format(MESSAGE, "A4096bNode", "A4096b");
    testInvalidGrammar(grammar, NoForbiddenProdName.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid3(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4096.A4096c";
    String message = String.format(MESSAGE, "EnclosingScope", "A4096c");
    testInvalidGrammar(grammar, NoForbiddenProdName.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid4(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4096.A4096d";
    String message = String.format(MESSAGE, "Traverser", "A4096d");
    testInvalidGrammar(grammar, NoForbiddenProdName.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid5(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4096.A4096e";
    String message = String.format(MESSAGE, "Node", "A4096e");
    testInvalidGrammar(grammar, NoForbiddenProdName.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid6(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4096.A4096f";
    String message = String.format(MESSAGE, "Class", "A4096f");
    testInvalidGrammar(grammar, NoForbiddenProdName.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid7(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4096.A4096g";
    String message = String.format(MESSAGE, "Mode", "A4096g");
    testInvalidGrammar(grammar, NoForbiddenProdName.ERROR_CODE, message, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
