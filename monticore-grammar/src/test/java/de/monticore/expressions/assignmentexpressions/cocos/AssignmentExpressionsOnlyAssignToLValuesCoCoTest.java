// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsCoCoChecker;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis.types3.util.ILValueRelations;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class AssignmentExpressionsOnlyAssignToLValuesCoCoTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    // using a language containing lvalue and non-lvalue expressions
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    // this test is (currently) not type dependent,
    // as whether something is a lvalue can be derived from the ASTNode-type
    // thus nothing type related needs to be initialized
  }

  @Test
  public void testNameExprAssignments() throws IOException {
    testValid("a = 42");
    testValid("a += 0");
    testValid("a -= 0");
    testValid("a *= 0");
    testValid("a /= 0");
    testValid("a &= 0");
    testValid("a |= 0");
    testValid("a ^= 0");
    testValid("a >>= 0");
    testValid("a >>>= 0");
    testValid("a <<= 0");
    testValid("a %= 0");
    testValid("C++");
    testValid("C--");
    testValid("--a");
    testValid("++a");
  }

  @Test
  public void testFieldAccessExprAssignments() throws IOException {
    testValid("a.a = 42");
    testValid("a.a += 0");
    testValid("a.a -= 0");
    testValid("a.a *= 0");
    testValid("a.a /= 0");
    testValid("a.a &= 0");
    testValid("a.a |= 0");
    testValid("a.a ^= 0");
    testValid("a.a >>= 0");
    testValid("a.a >>>= 0");
    testValid("a.a <<= 0");
    testValid("a.a %= 0");
    testValid("a.a++");
    testValid("a.a--");
    testValid("--a.a");
    testValid("++a.a");
  }

  @Test
  public void testArrayAccessExprAssignments() throws IOException {
    testValid("a[0] = 42");
    testValid("a[0] += 0");
    testValid("a[0] -= 0");
    testValid("a[0] *= 0");
    testValid("a[0] /= 0");
    testValid("a[0] &= 0");
    testValid("a[0] |= 0");
    testValid("a[0] ^= 0");
    testValid("a[0] >>= 0");
    testValid("a[0] >>>= 0");
    testValid("a[0] <<= 0");
    testValid("a[0] %= 0");
    testValid("a[0]++");
    testValid("a[0]--");
    testValid("--a[0]");
    testValid("++a[0]");
  }

  @Test
  public void testLiteralAssignments() throws IOException {
    testInvalid("true = true");
    testInvalid("'a' = 'a'");
    testInvalid("1 = 1");
    testInvalid("1l = 1l");
    testInvalid("0.1f = 0.1f");
    testInvalid("0.1 = 0.1");
    testInvalid("'a' += 'a'");
    testInvalid("1 += 1");
    testInvalid("1l += 1l");
    testInvalid("0.1f += 0.1f");
    testInvalid("0.1 += 0.1");
    testInvalid("'a' -= 'a'");
    testInvalid("1 -= 1");
    testInvalid("1l -= 1l");
    testInvalid("0.1f -= 0.1f");
    testInvalid("0.1 -= 0.1");
    testInvalid("'a' *= 'a'");
    testInvalid("1 *= 1");
    testInvalid("1l *= 1l");
    testInvalid("0.1f *= 0.1f");
    testInvalid("0.1 *= 0.1");
    testInvalid("'a' /= 'a'");
    testInvalid("1 /= 1");
    testInvalid("1l /= 1l");
    testInvalid("0.1f /= 0.1f");
    testInvalid("0.1 /= 0.1");
    testInvalid("'a' %= 'a'");
    testInvalid("1 %= 1");
    testInvalid("1l %= 1l");
    testInvalid("0.1f %= 0.1f");
    testInvalid("0.1 %= 0.1");
    testInvalid("'a' >>= 'a'");
    testInvalid("1 >>= 1");
    testInvalid("1l >>= 1l");
    testInvalid("0.1f >>= 0.1f");
    testInvalid("0.1 >>= 0.1");
    testInvalid("'a' <<= 'a'");
    testInvalid("1 <<= 1");
    testInvalid("1l <<= 1l");
    testInvalid("0.1f <<= 0.1f");
    testInvalid("0.1 <<= 0.1");
    testInvalid("'a' >>>= 'a'");
    testInvalid("1 >>>= 1");
    testInvalid("1l >>>= 1l");
    testInvalid("0.1f >>>= 0.1f");
    testInvalid("0.1 >>>= 0.1");
    testInvalid("true &= true");
    testInvalid("'a' &= 'a'");
    testInvalid("1 &= 1");
    testInvalid("1l &= 1l");
    testInvalid("true |= true");
    testInvalid("'a' |= 'a'");
    testInvalid("1 |= 1");
    testInvalid("1l |= 1l");
    testInvalid("true ^= true");
    testInvalid("'a' ^= 'a'");
    testInvalid("1 ^= 1");
    testInvalid("1l ^= 1l");
    testInvalid("++'c'");
    testInvalid("++1");
    testInvalid("++1l");
    testInvalid("++0.1f");
    testInvalid("++0.1");
    testInvalid("--'c'");
    testInvalid("--1");
    testInvalid("--1l");
    testInvalid("--0.1f");
    testInvalid("--0.1");
    testInvalid("'c'++");
    testInvalid("1++");
    testInvalid("1l++");
    testInvalid("0.1f++");
    testInvalid("0.1++");
    testInvalid("'c'--");
    testInvalid("1--");
    testInvalid("1l--");
    testInvalid("0.1f--");
    testInvalid("0.1--");
  }

  @Test
  public void testFurtherInvalidAssignments() throws IOException {
    testInvalid("(a) = (42)");
    testInvalid("a + a = 84");
    testInvalid("1 + 1 = 2");
    testInvalid("getToInc()++");
    testInvalid("getVar() = 2");
  }

  // Helper

  protected void testValid(String exprStr) throws IOException {
    check(exprStr);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Log.clearFindings();
  }

  protected void testInvalid(String exprStr) throws IOException {
    check(exprStr);
    Assertions.assertTrue(!Log.getFindings().isEmpty());
    Assertions.assertTrue(Log.getFindings().stream().anyMatch(
        f -> f.getMsg().contains("0xFDD47")
    ));
    Log.clearFindings();
  }

  protected void check(String exprStr) throws IOException {
    Optional<ASTExpression> exprOpt = CombineExpressionsWithLiteralsMill
        .parser().parse_StringExpression(exprStr);
    Assertions.assertTrue(exprOpt.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
    getChecker().checkAll(exprOpt.get());
  }

  protected AssignmentExpressionsCoCoChecker getChecker() {
    ILValueRelations ilValueRelations = new CommonExpressionsLValueRelations();
    AssignmentExpressionsCoCoChecker checker =
        new AssignmentExpressionsCoCoChecker();
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo)
        new AssignmentExpressionsOnlyAssignToLValuesCoCo(ilValueRelations)
    );
    return checker;
  }

}
