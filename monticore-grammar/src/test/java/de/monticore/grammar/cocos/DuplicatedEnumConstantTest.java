/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DuplicatedEnumConstantTest extends CocoTest{

  private final String MESSAGE =  " Duplicate enum constant: a.";
  public static final String HINT =   "\nHint: The constants of enumerations must be unique within an enumeration.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4014.A4014";
  
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
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
