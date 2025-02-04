/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.grammar.cocos.NTOnlyExtendsOneNTOrClass.ERROR_CODE;
import static java.lang.String.format;


public class NTOnlyExtendsOneNTOrClassTest extends CocoTest {

  private final String MESSAGE = " The nonterminal %s must not %s more than one %s.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4011.A4011";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
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
