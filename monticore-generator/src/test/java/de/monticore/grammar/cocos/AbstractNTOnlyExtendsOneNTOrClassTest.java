/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractNTOnlyExtendsOneNTOrClassTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4012.A4012";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
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
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }

}
