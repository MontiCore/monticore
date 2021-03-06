/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class DuplicatedEnumConstantTest extends CocoTest{

  private final String MESSAGE =  " Duplicate enum constant: a.";
  public static final String HINT =   "\nHint: The constants of enumerations must be unique within an enumeration.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4014.A4014";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new DuplicatedEnumConstant());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, DuplicatedEnumConstant.ERROR_CODE, MESSAGE+HINT, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
