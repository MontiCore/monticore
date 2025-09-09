/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PackageNameLowerCaseTest extends CocoTest{

  private final String MESSAGE = " The name C used for the nonterminal A referenced by the production B " +
          "should start with a lower-case letter.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4006.A4006";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new PackageNameLowerCase());
  }

  @Test
  public void testUpperCasedPackage() {
    testInvalidGrammar(grammar, PackageNameLowerCase.ERROR_CODE, String.format(PackageNameLowerCase.ERROR_MSG_FORMAT, "de.monticore.grammar.cocos.invalid.A4006"), checker);
  }


  @Test
  public void testAttributes(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
