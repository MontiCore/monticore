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
public class AttributeNameForNTDuplicatedTest extends CocoTest {
  
  private final String CODE = "xA4077";
  
  private final String MESSAGE = " The production C must not use the attribute name as for different nonterminals.";
  
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  
  private final String grammar = "cocos.invalid.A4006.A4006";
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDuplicatedAttribute() {
    Log.getFindings().clear();
    testInvalidGrammarKeepFindings(grammar, "0" + CODE, MESSAGE, checker);
  }
  
  @Test
  public void testAttributes() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }
  
}
