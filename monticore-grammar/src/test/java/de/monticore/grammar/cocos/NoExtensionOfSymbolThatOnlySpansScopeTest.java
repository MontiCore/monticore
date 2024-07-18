/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoExtensionOfSymbolThatOnlySpansScopeTest extends CocoTest {

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoExtensionOfSymbolThatOnlySpansScope());
  }

  @Test
  public void testInvalid1(){
    String grammar = "de.monticore.grammar.cocos.invalid.A0810.A0810a";
    String message = " The production B extends the symbol production A and spans a scope " +
      "without being a symbol itself.";
    testInvalidGrammar(grammar, NoExtensionOfSymbolThatOnlySpansScope.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid2(){
    String grammar = "de.monticore.grammar.cocos.invalid.A0810.A0810b";
    String message = " The production F extends the symbol production E and spans a scope " +
      "without being a symbol itself.";
    testInvalidGrammar(grammar, NoExtensionOfSymbolThatOnlySpansScope.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid3(){
    String grammar = "de.monticore.grammar.cocos.invalid.A0810.A0810c";
    String message = " The production J extends the symbol production A and spans a scope " +
      "without being a symbol itself.";
    testInvalidGrammar(grammar, NoExtensionOfSymbolThatOnlySpansScope.ERROR_CODE, message, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.CorrectSymbolInheritance",checker);
  }


}
