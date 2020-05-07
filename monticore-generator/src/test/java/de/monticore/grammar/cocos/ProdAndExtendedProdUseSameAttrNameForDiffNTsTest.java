/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProdAndExtendedProdUseSameAttrNameForDiffNTsTest extends CocoTest {

  private final String MESSAGE = " The production B extending the production A must not use the\n" +
      "name a for the nonterminal D as A already uses this name for the nonterminal C.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4024.A4024";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new ProdAndExtendedProdUseSameAttrNameForDiffNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, MESSAGE,
        checker);
  }

  @Test
  public void testInvalid_b() {
    //Super = C;
    //Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the nonterminal C.";
    testInvalidGrammar(grammar + "_b", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_c() {
    //  Super = c:["state"];
    //  Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the production that is not a constant group.";
    testInvalidGrammar(grammar + "_c", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_d() {
    //  Super = c:"state";
    //  Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the production that is a terminal named c.";
    testInvalidGrammar(grammar + "_d", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_e() {
    //  Super = ["c"];
    //  Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the production that is not a constant group.";
    testInvalidGrammar(grammar + "_e", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_f() {
    //  Super = [c:"state"];
    //  Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the production that is not a constant group.";
    testInvalidGrammar(grammar + "_f", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_g() {
    //  Super1 = c:D;
    //  Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the nonterminal D.";
    testInvalidGrammar(grammar + "_g", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_h() {
    //  Super1 = c:D;
    //  Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the nonterminal Name.";
    testInvalidGrammar(grammar + "_h", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_i() {
    //  Super1 = c:D;
    //  Sub extends Super = c:A;
    String message = " The production Sub extending the production Super must not use the\n" +
        "name c for the nonterminal A as Super already uses this name for the nonterminal D.";
    testInvalidGrammar(grammar + "_i", ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker, 4);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("mc.grammars.types.TestTypes", checker);
  }

  @Test
  public void testCorrect3() {
    testValidGrammar("cocos.valid.ProdAndOverriddenProdUseSameAttrNameForDiffNTs_sub", checker);
  }

}
