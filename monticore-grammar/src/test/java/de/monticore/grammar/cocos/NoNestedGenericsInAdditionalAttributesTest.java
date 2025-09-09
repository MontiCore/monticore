/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoNestedGenericsInAdditionalAttributesTest extends CocoTest {

  public final String MESSAGE = " %srule does not allow the definition of nested generics. " +
      "Problem in grammar '%s', rule for '%s', with additional attribute: '%s'.";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoNestedGenericsInAdditionalAttributes());
  }

  @Test
  public void testInvalidNestedGeneric() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102a", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102a", "A", "b:List<Optional<String>>"), checker);
  }

  @Test
  public void testInvalidPlus() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102b", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102b", "A", "b:Optional<String>+"), checker);
  }

  @Test
  public void testInvalidQuestion() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102c", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102c", "A", "b:Optional<String>?"), checker);
  }

  @Test
  public void testInvalidMaxNumber() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102d", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102d", "A", "b:Optional<String> max=5"), checker);
  }

  @Test
  public void testInvalidStar() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102e", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102e", "A", "b:Optional<String>*"), checker);
  }

  @Test
  public void testInvalidMinNull() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102f", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102f", "A", "b:Optional<String> min=0"), checker);
  }

  @Test
  public void testInvalidSymbolRule() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102g", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Symbol", "A4102g", "A", "b:Set<Optional<String>>"), checker);
  }

  @Test
  public void testInvalidScopeRule() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102h", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Scope", "A4102h", "A4102hScope", "b:Optional<String> max=5"), checker);
  }

  @Test
  public void testInvalidMaxStar() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A4102.A4102i", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102i", "A", "b:Optional<String> max=*"), checker);
  }

  @Test
  public void testCorrectASTRule() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

  @Test
  public void testCorrectSymbolRule() {
    testValidGrammar("de.monticore.grammar.cocos.valid.SymbolRules", checker);
  }

  @Test
  public void testCorrectScopeRule() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ScopeRule", checker);
  }
}
