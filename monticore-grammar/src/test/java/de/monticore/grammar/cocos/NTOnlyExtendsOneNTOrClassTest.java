/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.grammar.cocos.NTOnlyExtendsOneNTOrClass.ERROR_CODE;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;
import static java.lang.String.format;

public class NTOnlyExtendsOneNTOrClassTest extends CocoTest {

  private final String MESSAGE = " The nonterminal %s must not %s more than one %s.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4011.A4011";

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
    checker.addCoCo(new NTOnlyExtendsOneNTOrClass());
  }

  @Test
  public void testExtendMultiple() {
    testInvalidGrammar(grammar + "a", ERROR_CODE,
            format(MESSAGE, "C", "extend", "nonterminal"), checker);
  }

  @Test
  public void testASTExtendMultiple() {
    testInvalidGrammar(grammar + "b", ERROR_CODE,
            format(MESSAGE, "A", "astextend", "class"), checker);
  }

  @Test
  public void testExtendNT() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }

}
