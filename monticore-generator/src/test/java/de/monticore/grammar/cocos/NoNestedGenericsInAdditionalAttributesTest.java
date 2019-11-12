package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoNestedGenericsInAdditionalAttributesTest extends CocoTest {

  public final String MESSAGE = " %srule does not allow the definition of nested generics. " +
      "Problem in grammar '%s', rule for '%s', with additional attribute: '%s'.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new NoNestedGenericsInAdditionalAttributes());
  }

  @Test
  public void testInvalidNestedGeneric() {
    testInvalidGrammar("cocos.invalid.A4102.A4102a", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102a", "A", "b:List<Optional<String>>"), checker);
  }

  @Test
  public void testInvalidPlus() {
    testInvalidGrammar("cocos.invalid.A4102.A4102b", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102b", "A", "b:Optional<String>+"), checker);
  }

  @Test
  public void testInvalidQuestion() {
    testInvalidGrammar("cocos.invalid.A4102.A4102c", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102c", "A", "b:Optional<String>?"), checker);
  }

  @Test
  public void testInvalidMaxNumber() {
    testInvalidGrammar("cocos.invalid.A4102.A4102d", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102d", "A", "b:Optional<String> max=5"), checker);
  }

  @Test
  public void testInvalidStar() {
    testInvalidGrammar("cocos.invalid.A4102.A4102e", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102e", "A", "b:Optional<String>*"), checker);
  }

  @Test
  public void testInvalidMinNull() {
    testInvalidGrammar("cocos.invalid.A4102.A4102f", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102f", "A", "b:Optional<String> min=0"), checker);
  }

  @Test
  public void testInvalidSymbolRule() {
    testInvalidGrammar("cocos.invalid.A4102.A4102g", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Symbol", "A4102g", "A", "b:Set<Optional<String>>"), checker);
  }

  @Test
  public void testInvalidScopeRule() {
    testInvalidGrammar("cocos.invalid.A4102.A4102h", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Scope", "A4102h", "A4102hScope", "b:Optional<String> max=5"), checker);
  }

  @Test
  public void testInvalidMaxStar() {
    testInvalidGrammar("cocos.invalid.A4102.A4102i", NoNestedGenericsInAdditionalAttributes.ERROR_CODE,
        String.format(MESSAGE, "Ast", "A4102i", "A", "b:Optional<String> max=*"), checker);
  }

  @Test
  public void testCorrectASTRule() {
    testValidGrammar("cocos.valid.ASTRules", checker);
  }

  @Test
  public void testCorrectSymbolRule() {
    testValidGrammar("cocos.valid.SymbolRules", checker);
  }

  @Test
  public void testCorrectScopeRule() {
    testValidGrammar("cocos.valid.ScopeRule", checker);
  }
}
