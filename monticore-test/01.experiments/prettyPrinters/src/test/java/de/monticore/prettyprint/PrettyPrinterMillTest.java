package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.supertestprettyprinters.SuperTestPrettyPrintersMill;
import de.monticore.supertestprettyprinters._ast.ASTSuperTestPrettyPrintersNode;
import de.monticore.testprettyprinters.TestPrettyPrintersMill;
import de.monticore.testprettyprinters._ast.ASTTestPrettyPrintersNode;
import de.monticore.testprettyprinters._ast.ASTTypeInterface;
import de.monticore.testprettyprinters._ast.ASTUsingTestType;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Test whether the pretty print methods of Mills handle language composition
 */
public class PrettyPrinterMillTest {

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
    // We initiate mills in the various test methods
  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
    // Reset both mills (depending on Mill#reset even multiple times)
    SuperTestPrettyPrintersMill.reset();
    TestPrettyPrintersMill.reset();
  }

  @Test
  public void testMillTestType() throws Exception {
    // Test-Production + TestPrettyPrinter
    TestPrettyPrintersMill.init();
    Optional<ASTTypeInterface> astOpt = TestPrettyPrintersMill.parser().parse_StringTypeInterface("TestType");
    Assert.assertTrue(astOpt.isPresent());
    ASTTestPrettyPrintersNode astNode = astOpt.get();

    test(astNode, a -> TestPrettyPrintersMill.prettyPrint(a, true), TestPrettyPrintersMill.parser()::parse_StringTypeInterface);
  }

  @Test
  public void testSuperMill() throws Exception {
    // Test-Production + SuperTestPrettyPrinter
    // Aux: ASTTestType cannot be cast to class de.monticore.supertestprettyprinters._ast.ASTSuperTestPrettyPrintersNode

    SuperTestPrettyPrintersMill.init();
    Optional<ASTTypeInterface> astOpt = SuperTestPrettyPrintersMill.parser().parse_StringTypeInterface("TestType");
    Assert.assertTrue(astOpt.isPresent());
    ASTTestPrettyPrintersNode astNode = astOpt.get();

    test(astNode, a -> TestPrettyPrintersMill.prettyPrint(a, true), SuperTestPrettyPrintersMill.parser()::parse_StringTypeInterface);
  }

  @Test
  public void testMillTestSuperType() throws Exception {
    // SuperTest-Production + TestPrettyPrinter
    SuperTestPrettyPrintersMill.init();
    Optional<ASTTypeInterface> astOpt = TestPrettyPrintersMill.parser().parse_StringTypeInterface("SuperTestType");
    Assert.assertTrue(astOpt.isPresent());
    ASTTestPrettyPrintersNode astNode = astOpt.get();

    test(astNode, a -> TestPrettyPrintersMill.prettyPrint(a, true), TestPrettyPrintersMill.parser()::parse_StringTypeInterface);
  }

  @Test
  public void testSuperMillTestSuperType() throws Exception {
    // SuperTest-Production + SuperTestPrettyPrinter
    SuperTestPrettyPrintersMill.init();
    Optional<ASTTypeInterface> astOpt = SuperTestPrettyPrintersMill.parser().parse_StringTypeInterface("SuperTestType");
    Assert.assertTrue(astOpt.isPresent());
    ASTSuperTestPrettyPrintersNode astNode = (ASTSuperTestPrettyPrintersNode) astOpt.get();

    test(astNode, a -> SuperTestPrettyPrintersMill.prettyPrint(a, true), SuperTestPrettyPrintersMill.parser()::parse_StringTypeInterface);
  }

  protected <T extends ASTNode, A extends ASTNode> void test(T ast, Function<T, String> prettyPrint, ParseFunction<A> parse)
      throws IOException {
    String pretty = prettyPrint.apply(ast);
    Optional<A> prettyAstOpt = parse.parse(pretty);
    Assert.assertTrue("Failed to parse: " + pretty, prettyAstOpt.isPresent());
    Assert.assertTrue(ast.deepEquals(prettyAstOpt.get()));
  }

  @Test
  public void testUsingMill() throws Exception {
    TestPrettyPrintersMill.init();
    Optional<ASTUsingTestType> astOpt = TestPrettyPrintersMill.parser().parse_StringUsingTestType("abc TestType");
    Assert.assertTrue(astOpt.isPresent());

    test(astOpt.get(), a -> TestPrettyPrintersMill.prettyPrint(a, true), TestPrettyPrintersMill.parser()::parse_StringUsingTestType);
  }

  @Test
  public void testUsingSuperMill() throws Exception {
    SuperTestPrettyPrintersMill.init();
    Optional<ASTUsingTestType> astOpt = SuperTestPrettyPrintersMill.parser().parse_StringUsingTestType("abc TestType");
    Assert.assertTrue(astOpt.isPresent());

    test(astOpt.get(), a -> TestPrettyPrintersMill.prettyPrint(a, true), SuperTestPrettyPrintersMill.parser()::parse_StringUsingTestType);
  }

  @Test
  public void testUsingSuperMillSuperType() throws Exception {
    SuperTestPrettyPrintersMill.init();
    Optional<ASTUsingTestType> astOpt = SuperTestPrettyPrintersMill.parser().parse_StringUsingTestType("abc SuperTestType");
    Assert.assertTrue(astOpt.isPresent());

    test(astOpt.get(), a -> SuperTestPrettyPrintersMill.prettyPrint(a, true), SuperTestPrettyPrintersMill.parser()::parse_StringUsingTestType);
  }

  @FunctionalInterface
  interface ParseFunction<A> {
    Optional<A> parse(String string) throws IOException;
  }
}
