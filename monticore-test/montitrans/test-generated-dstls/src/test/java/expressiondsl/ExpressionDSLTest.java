/* (c) https://github.com/MontiCore/monticore */
package expressiondsl;

import com.google.common.base.Strings;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.tr.expressionsbasistr._ast.ASTExpression_Pat;
import de.monticore.expressions.tr.expressionsbasistr._ast.ASTITFExpression;
import de.monticore.tf.*;
import de.monticore.tf.complex.*;
import de.monticore.tf.leftrecassignments.*;
import de.monticore.tf.specificliterals.*;
import de.monticore.tf.runtime.ODRule;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;
import mc.testcases.expressiondsl.ExpressionDSLMill;
import mc.testcases.expressiondsl._ast.ASTCDAttribute;
import mc.testcases.expressiondsl._ast.ASTFoo;
import mc.testcases.tr.expressiondsltr.ExpressionDSLTRMill;
import org.junit.*;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class ExpressionDSLTest {

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @BeforeClass
  public static void beforeClass() {
    Log.init();
    Log.enableFailQuick(false);
    ExpressionDSLMill.init();
  }

  @Test
  public void testExp01RenameLeftSide() throws IOException {
    test("expr a = a + 1", Exp01RenameLeftSide::new, "expr b = a + 1");
  }

  @Test
  public void testExp01RenameRightSide() throws IOException {
    test("expr a = a + 1", Exp01RenameRightSide::new, "expr a = b + 1");
  }

  @Test
  public void testExp01ChangeRightSideNumber() throws IOException {
    test("expr a = a + 1", Exp01ChangeRightSideNumber::new, "expr a = a + 42");
  }

  @Test
  public void testReplaceNameByName() throws IOException {
    test("expr a = a", ReplaceNameByName::new, "expr a = b");
  }

  @Test
  public void testReplaceNameByNumber() throws IOException {
    test("expr a = a", ReplaceNameByNumber::new, "expr a = 1");
  }

  @Test
  public void testReplaceNameByString() throws IOException {
    test("expr a = a", ReplaceNameByString::new, "expr a = \"s\"");
  }

  @Test
  public void testReplaceNumberByName() throws IOException {
    // NameExpression vs LiteralExpression with NatLiteral
    test("expr a = 1", ReplaceNumberByName::new, "expr a = b");
  }

  @Test
  public void testReplaceNumberByNumber() throws IOException {
    test("expr a = 1", ReplaceNumberByNumber::new, "expr a = 2");
  }

  @Test
  public void testReplaceNumberByString() throws IOException {
    test("expr a = 1", ReplaceNumberByString::new, "expr a = \"s\"");
  }

  @Test
  public void testReplaceIncPrefixExpByDecPrefixExp() throws IOException {
    test("expr ++a", ReplaceIncPrefixExpByDecPrefixExp::new, "expr --a");
  }

  @Test
  public void testReplaceIncSuffixExpByDecSuffixExp() throws IOException {
    test("expr a++", ReplaceIncSuffixExpByDecSuffixExp::new, "expr a--");
  }

  @Test
  public void testReplaceIncSuffixExpByIncSuffixExp() throws IOException {
    test("expr a++", ReplaceIncSuffixExpByIncSuffixExp::new, "expr ++a");
  }

  @Test
  public void testIntroduceBrackets() throws IOException {
    test("expr a", IntroduceBrackets::new, "expr (a)");
    test("expr a=b", IntroduceBrackets::new, "expr (a=b)");
  }


  @Test
  public void testChangeSetterCall() throws IOException {
    test("expr a.y = true", ChangeSetterCall::new, "expr a.setY(true)");
    test("expr a.y = 42*x", ChangeSetterCall::new, "expr a.setY(42*x)");
  }

  @Test
  public void testExpressionInterfacePatternPriority() throws IOException {
    Optional<ASTExpression_Pat> astExpressionOpt = ExpressionDSLTRMill.parser().parse_StringExpression_Pat("Expression $name");
    Assert.assertTrue(astExpressionOpt.isPresent());
    Assert.assertEquals(ASTExpression_Pat.class.getName(), astExpressionOpt.get().getClass().getName());

    Optional<ASTITFExpression> astitfExpressionOpt = ExpressionDSLTRMill.parser().parse_StringITFExpression("Expression $name");
    Assert.assertTrue(astitfExpressionOpt.isPresent());
    Assert.assertEquals(ASTExpression_Pat.class.getName(), astitfExpressionOpt.get().getClass().getName());
  }


  @Test
  public void testCDAttributeChangeType() throws IOException {
    testCDAttribute("public String foo;", CDAttributeChangeType::new, "public boolean foo;");
  }

  @Test
  public void testCDAttributeChangeTypeFull() throws IOException {
    testCDAttribute("public String foo;", CDAttributeChangeTypeFull::new, "public boolean foo;");
  }


  protected void test(String input, Function<ASTFoo, ODRule> rule, String expected) throws IOException {
    Optional<ASTFoo> fooOpt = ExpressionDSLMill.parser().parse_String(input);
    Assert.assertTrue(fooOpt.isPresent());

    ODRule trafo = rule.apply(fooOpt.get());
    assertTrue("Failed to match pattern", trafo.doPatternMatching());
    trafo.doReplacement();

    testDeepEqualsFoo(fooOpt.get(), expected);
  }

  protected void testCDAttribute(String input, Function<ASTCDAttribute, ODRule> rule, String expected) throws IOException {
    Optional<ASTCDAttribute> fooOpt = ExpressionDSLMill.parser().parse_StringCDAttribute(input);
    Assert.assertTrue(fooOpt.isPresent());

    ODRule trafo = rule.apply(fooOpt.get());
    assertTrue("Failed to match pattern", trafo.doPatternMatching());
    trafo.doReplacement();

    testDeepEqualsCDAttribute(fooOpt.get(), expected);
  }

  protected void testDeepEqualsFoo(ASTFoo ast, String expected) throws IOException {
    Optional<ASTFoo> fooOpt = ExpressionDSLMill.parser().parse_String(expected);
    testDeepEquals(ast, expected, fooOpt);
  }

  protected void testDeepEqualsCDAttribute(ASTCDAttribute ast, String expected) throws IOException {
    Optional<ASTCDAttribute> fooOpt = ExpressionDSLMill.parser().parse_StringCDAttribute(expected);
    testDeepEquals(ast, expected, fooOpt);
  }

  private void testDeepEquals(ASTNode ast, String expected, Optional<? extends ASTNode> fooOpt) {
    Assert.assertTrue("Failed to parse expected", fooOpt.isPresent());
    if (!fooOpt.get().deepEquals(ast)) {
      Assert.assertEquals(ExpressionDSLMill.prettyPrint(fooOpt.get(), false), ExpressionDSLMill.prettyPrint(ast, false));
      Assert.assertEquals(astPrinter(fooOpt.get(), ExpressionDSLMill.inheritanceTraverser()),
          astPrinter(ast, ExpressionDSLMill.inheritanceTraverser()));

      Assert.fail("Failed to deep equal: " + ExpressionDSLMill.prettyPrint(fooOpt.get(), false) + ", expected " + expected);
    }
  }

  protected String astPrinter(ASTNode node, ITraverser traverser) {

    StringBuilder sb = new StringBuilder();
    traverser.add4IVisitor(new IVisitor() {
      int d = 0;

      protected String pp() {
        return Strings.repeat(" ", d);
      }

      @Override
      public void visit(ASTNode node) {
        sb.append(pp()).append("<" + node.getClass().getName() + "> ").append("\n");
        d++;
      }

      @Override
      public void endVisit(ASTNode node) {
        d--;
        sb.append(pp()).append("</" + node.getClass().getName() + ">").append("\n");
      }

    });
    node.accept(traverser);

    return sb.toString();
  }

}
