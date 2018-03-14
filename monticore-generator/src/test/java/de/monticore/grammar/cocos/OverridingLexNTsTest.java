/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by
 *
 * @author KH
 */
public class OverridingLexNTsTest extends CocoTest{

  private final String MESSAGE =  " The lexical production CARDINALITY must not use a different "
      + "type to store the token than the overridden production.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4026.A4026b";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new OverridingLexNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingLexNTs.ERROR_CODE, String.format(MESSAGE, "interface"),
        checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }

}
