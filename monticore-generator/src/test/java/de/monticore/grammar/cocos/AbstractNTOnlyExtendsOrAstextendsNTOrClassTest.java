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
public class AbstractNTOnlyExtendsOrAstextendsNTOrClassTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4030.A4030";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new AbstractNTOnlyExtendOrAstextendNTOrClass());
  }

  @Test
  public void testASTExtendMultiple() {
    testInvalidGrammar(grammar, AbstractNTOnlyExtendOrAstextendNTOrClass.ERROR_CODE,
        String.format(AbstractNTOnlyExtendOrAstextendNTOrClass.ERROR_MSG_FORMAT, "B"),
        checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }

}
