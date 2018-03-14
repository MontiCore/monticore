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
public class NTNotExtendInterfaceOrExternalNTsTest extends CocoTest {

  private final String MESSAGE = " The nonterminal B must not extend the %s nonterminal A. " +
          "Nonterminals may only extend abstract or normal nonterminals.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2103.A2103";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new NTNotExtendInterfaceOrExternalNTs());
  }

  @Test
  public void testExtendInterfaceNT() {
    testInvalidGrammar(grammar + "a", NTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(MESSAGE, "interface"), checker);
  }
  
  @Test
  public void testExtendExternalNT() {
    testInvalidGrammar(grammar + "b", NTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(MESSAGE, "external"), checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }
}
