/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import de.se_rwth.commons.logging.Log;

public class ProdWithDoubleAnnosTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4119.A4119";

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ProdWithDoubleAnnos());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ProdWithDoubleAnnos.ERROR_CODE,
            format(ProdWithDoubleAnnos.ERROR_MSG_FORMAT, "A", "@Override"), checker);
  }

  @Test
  public void testInvalida() {
    testInvalidGrammar(grammar + "a", ProdWithDoubleAnnos.ERROR_CODE,
            format(ProdWithDoubleAnnos.ERROR_MSG_FORMAT, "A", "@Deprecated"), checker);
  }

}
