/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractNTOnlyExtendsOneNTOrClassTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4012.A4012";
  
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new AbstractNTOnlyExtendsOneNTOrClass());
  }

  @Test
  public void testExtendMultiple() {
    testInvalidGrammar(grammar + "a", AbstractNTOnlyExtendsOneNTOrClass.ERROR_CODE,
        String.format(AbstractNTOnlyExtendsOneNTOrClass.ERROR_MSG_FORMAT, "C", "extend", "nonterminal"), checker);
  }

  @Test
  public void testAstExtendMultiple() {
    testInvalidGrammar(grammar + "b", AbstractNTOnlyExtendsOneNTOrClass.ERROR_CODE,
        String.format(AbstractNTOnlyExtendsOneNTOrClass.ERROR_MSG_FORMAT, "A", "astextend", "class"), checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }

}
