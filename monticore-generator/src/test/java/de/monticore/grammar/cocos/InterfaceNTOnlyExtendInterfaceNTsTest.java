/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

public class InterfaceNTOnlyExtendInterfaceNTsTest extends CocoTest {

  private final String MESSAGE = " The interface nonterminal B must not extend the%s nonterminal A. " +
          "Interface nonterminals may only extend interface nonterminals.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2116.A2116";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new InterfaceNTOnlyExtendInterfaceNTs());
  }

  @Test
  public void testExtendAbstractNT() {
    testInvalidGrammar(grammar + "a", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, " abstract"), checker);
  }
  
  @Test
  public void testExtendExternalNT() {
    testInvalidGrammar(grammar + "b", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, " external"), checker);
  }
  
  @Test
  public void testExtendNormalNT() {
    testInvalidGrammar(grammar + "c", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }
  
  @Test
  public void testExtendNormalNT2() {
    testInvalidGrammar(grammar + "d", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }

  @Test
  public void testExtendInterfaceNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }
}
