/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class NTOnlyExtendsOneNTOrClassTest extends CocoTest{

  private final String MESSAGE = " The nonterminal %s must not %s more than one %s.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4011.A4011";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new NTOnlyExtendsOneNTOrClass());
  }

  @Test
  public void testExtendMultiple() {
    testInvalidGrammar(grammar + "a", NTOnlyExtendsOneNTOrClass.ERROR_CODE,
        String.format(MESSAGE, "C", "extend", "nonterminal"), checker);
  }
  
  @Test
  public void testASTExtendMultiple() {
    testInvalidGrammar(grammar + "b", NTOnlyExtendsOneNTOrClass.ERROR_CODE,
        String.format(MESSAGE, "A", "astextend", "class"), checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }

}
