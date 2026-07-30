// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsCoCoChecker;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(CombineExpressionsWithLiteralsMill.class)
public class AssignmentExpressionsOnlyAssignToLValuesCoCoTest {

  @BeforeEach
  public void before() {
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      // NameExpression assignments
      "a = 42",
      "a += 0",
      "a -= 0",
      "a *= 0",
      "a /= 0",
      "a &= 0",
      "a |= 0",
      "a ^= 0",
      "a >>= 0",
      "a >>>= 0",
      "a <<= 0",
      "a %= 0",
      "C++",
      "C--",
      "--a",
      "++a",
      
      // FieldAccessExpression assignments
      "a.a = 42",
      "a.a += 0",
      "a.a -= 0",
      "a.a *= 0",
      "a.a /= 0",
      "a.a &= 0",
      "a.a |= 0",
      "a.a ^= 0",
      "a.a >>= 0",
      "a.a >>>= 0",
      "a.a <<= 0",
      "a.a %= 0",
      "a.a++",
      "a.a--",
      "--a.a",
      "++a.a",
      
      // ArrayAccessExpression assignments
      "a[0] = 42",
      "a[0] += 0",
      "a[0] -= 0",
      "a[0] *= 0",
      "a[0] /= 0",
      "a[0] &= 0",
      "a[0] |= 0",
      "a[0] ^= 0",
      "a[0] >>= 0",
      "a[0] >>>= 0",
      "a[0] <<= 0",
      "a[0] %= 0",
      "a[0]++",
      "a[0]--",
      "--a[0]",
      "++a[0]"
  })
  public void testValid(String exprStr) throws IOException {
    check(exprStr);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      // Literal assignments
      "true = true",
      "'a' = 'a'",
      "1 = 1",
      "1l = 1l",
      "0.1f = 0.1f",
      "0.1 = 0.1",
      "'a' += 'a'",
      "1 += 1",
      "1l += 1l",
      "0.1f += 0.1f",
      "0.1 += 0.1",
      "'a' -= 'a'",
      "1 -= 1",
      "1l -= 1l",
      "0.1f -= 0.1f",
      "0.1 -= 0.1",
      "'a' *= 'a'",
      "1 *= 1",
      "1l *= 1l",
      "0.1f *= 0.1f",
      "0.1 *= 0.1",
      "'a' /= 'a'",
      "1 /= 1",
      "1l /= 1l",
      "0.1f /= 0.1f",
      "0.1 /= 0.1",
      "'a' %= 'a'",
      "1 %= 1",
      "1l %= 1l",
      "0.1f %= 0.1f",
      "0.1 %= 0.1",
      "'a' >>= 'a'",
      "1 >>= 1",
      "1l >>= 1l",
      "0.1f >>= 0.1f",
      "0.1 >>= 0.1",
      "'a' <<= 'a'",
      "1 <<= 1",
      "1l <<= 1l",
      "0.1f <<= 0.1f",
      "0.1 <<= 0.1",
      "'a' >>>= 'a'",
      "1 >>>= 1",
      "1l >>>= 1l",
      "0.1f >>>= 0.1f",
      "0.1 >>>= 0.1",
      "true &= true",
      "'a' &= 'a'",
      "1 &= 1",
      "1l &= 1l",
      "true |= true",
      "'a' |= 'a'",
      "1 |= 1",
      "1l |= 1l",
      "true ^= true",
      "'a' ^= 'a'",
      "1 ^= 1",
      "1l ^= 1l",
      "++'c'",
      "++1",
      "++1l",
      "++0.1f",
      "++0.1",
      "--'c'",
      "--1",
      "--1l",
      "--0.1f",
      "--0.1",
      "'c'++",
      "1++",
      "1l++",
      "0.1f++",
      "0.1++",
      "'c'--",
      "1--",
      "1l--",
      "0.1f--",
      "0.1--",
      
      // further invalid assignments
      "(a) = (42)",
      "a + a = 84",
      "1 + 1 = 2",
      "getToInc()++",
      "getVar() = 2"
  })
  public void testInvalid(String exprStr) throws IOException {
    check(exprStr);
    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith("0xFDD47"));
  }

  protected void check(String exprStr) throws IOException {
    Optional<ASTExpression> exprOpt = CombineExpressionsWithLiteralsMill
        .parser().parse_StringExpression(exprStr);
    assertTrue(exprOpt.isPresent());
    assertTrue(Log.getFindings().isEmpty());
    getChecker().checkAll(exprOpt.get());
  }

  protected AssignmentExpressionsCoCoChecker getChecker() {
    AssignmentExpressionsCoCoChecker checker =
        new AssignmentExpressionsCoCoChecker();
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo)
        new AssignmentExpressionsOnlyAssignToLValuesCoCo()
    );
    return checker;
  }

}
