/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractNTOnlyExtendsOrAstextendsNTOrClassTest extends CocoTest{

  private final String grammar = "de.monticore.grammar.cocos.invalid.A4030.A4030";
  
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new AbstractNTOnlyExtendOrAstextendNTOrClass());
  }

  @Test
  public void testASTExtendMultiple() {
    testInvalidGrammar(grammar, AbstractNTOnlyExtendOrAstextendNTOrClass.ERROR_CODE,
        String.format(AbstractNTOnlyExtendOrAstextendNTOrClass.ERROR_MSG_FORMAT, "B"),
        checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }

}
