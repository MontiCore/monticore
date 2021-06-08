/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NTOnlyImplementInterfaceNTsTest extends CocoTest {
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String MESSAGE = " The nonterminal B must not implement the nonterminal A." +
          " Nonterminals may only implement interface nonterminals.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2102.A2102";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new NTOnlyImplementInterfaceNTs());
  }

  @Test
  public void testImplementsNormalNT() {
    testInvalidGrammar(grammar + "a", NTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testImplementsExternalNT() {
    testInvalidGrammar(grammar + "b", NTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testImplementsAbstractNT() {
    testInvalidGrammar(grammar + "c", NTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testImplementsEnumNT() {
    testInvalidGrammar(grammar + "d", NTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testImplementsInterfaceNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ImplementInterfaceNTs", checker);
  }



}
