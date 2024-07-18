/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProdAndOverriddenProdUseSameAttrNameForDiffNTsTest extends CocoTest {

  private final String MESSAGE = " The overriding production QualifiedName must not use " +
      "the name part for the nonterminal StringLiteral as the overridden production uses this name for the nonterminal Name";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4025.A4025";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ProdAndOverriddenProdUseSameAttrNameForDiffNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, MESSAGE,
        checker);
  }

  @Test
  public void testInvalid_b() {
    //super
    //C;
    //A = C;
    //sub
    // B;
    // A = c:B;
    String message = " The overriding production A must not use the name c for the nonterminal B " +
        "as the overridden production uses this name for the nonterminal C";
    testInvalidGrammar(grammar + "_sub_b", ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_c() {
    //super
    //C;
    //A = c:["b"];
    //sub
    // B;
    // A = c:B;
    String message =  " The overriding production A must not use the name c for the nonterminal B" +
    " as the overridden production uses this name for the production of a constant group";
    testInvalidGrammar(grammar + "_sub_c", ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_d() {
    //super
    //A = ["c"];
    //sub
    // B;
    // A = c:B;
    String message = " The overriding production A must not use the name c for the nonterminal B" +
        " as the overridden production uses this name for the production of a constant group";
    testInvalidGrammar(grammar + "_sub_d", ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_e() {
    //super
    //A = [c:"b"];
    //sub
    // B;
    // A = c:B;
    String message = " The overriding production A must not use the name c for the nonterminal B " +
        "as the overridden production uses this name for the production of a constant group";
    testInvalidGrammar(grammar + "_sub_e", ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_f() {
    //super
    // D;
    // A = c:D;
    //sub
    // B;
    // A = c:B;
    String message = " The overriding production A must not use the name c for the nonterminal B" +
        " as the overridden production uses this name for the nonterminal D";
    testInvalidGrammar(grammar + "_sub_f", ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }

  @Test
  public void testInvalid_g() {
    //super
    //A = c:"b";
    //sub
    // B;
    // A = c:B;
    String message = " The overriding production A must not use the name c for the nonterminal " +
        "B as the overridden production uses this name for the production of a terminal";
    testInvalidGrammar(grammar + "_sub_g", ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, message,
        checker);
  }


  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("de.monticore.common.TestTypes", checker);
  }

  @Test
  public void testCorrect3() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ProdAndOverriddenProdUseSameAttrNameForDiffNTs_sub", checker);
  }


}
