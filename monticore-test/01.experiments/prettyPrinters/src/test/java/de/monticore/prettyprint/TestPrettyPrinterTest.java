// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.testprettyprinters.TestPrettyPrintersMill;
import de.monticore.testprettyprinters._ast.ASTTestPrettyPrintersNode;
import de.monticore.testprettyprinters._prettyprint.TestPrettyPrintersFullPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.junit.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Test the PrettyPrinter Generation
 */
public class TestPrettyPrinterTest {

  @BeforeClass
  public static void setup() {
    TestPrettyPrintersMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void testA() throws IOException {
    testPP("A", TestPrettyPrintersMill.parser()::parse_StringA);
  }

  @Test
  public void testB() throws IOException {
    testPP("B", TestPrettyPrintersMill.parser()::parse_StringB);
  }

  @Test
  public void testC() throws IOException {
    testPP("C", TestPrettyPrintersMill.parser()::parse_StringC);
  }

  @Test
  public void testCPName() throws IOException {
    testPP("term n1 n2 n3", TestPrettyPrintersMill.parser()::parse_StringCPName);

  }

  @Test
  public void testCPNameOpt() throws IOException {
    testPP("term n1 n2 n3", TestPrettyPrintersMill.parser()::parse_StringCPNameOpt);
    testPP("term n1 n2   ", TestPrettyPrintersMill.parser()::parse_StringCPNameOpt);
    testPP("term n1    n3", TestPrettyPrintersMill.parser()::parse_StringCPNameOpt);
    testPP("term n1      ", TestPrettyPrintersMill.parser()::parse_StringCPNameOpt);
  }

  @Test
  public void testCPA() throws IOException {
    testPP("term A A A", TestPrettyPrintersMill.parser()::parse_StringCPA);
  }

  @Test
  public void testCPAOpt() throws IOException {
    testPP("term A A A", TestPrettyPrintersMill.parser()::parse_StringCPAOpt);
    testPP("term A A  ", TestPrettyPrintersMill.parser()::parse_StringCPAOpt);
    testPP("term A    ", TestPrettyPrintersMill.parser()::parse_StringCPAOpt);
  }


  @Test
  public void testCPAList() throws IOException {
    testPP("term A A A A", TestPrettyPrintersMill.parser()::parse_StringCPAList);
    testPP("term A A A  ", TestPrettyPrintersMill.parser()::parse_StringCPAList);
    testPP("term A A    ", TestPrettyPrintersMill.parser()::parse_StringCPAList);
    testPP("term A      ", TestPrettyPrintersMill.parser()::parse_StringCPAList);
    testPP("term        ", TestPrettyPrintersMill.parser()::parse_StringCPAList);
  }

  @Test
  public void testCPIteratorDef() throws IOException {
    testPP("A term A n1 term n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorDef);
  }

  @Test
  public void testCPIteratorOpt() throws IOException {
    testPP("A term A n1 term n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorOpt);
    testPP("A term A n1 term   ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorOpt);
    testPP("A term   n1 term n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorOpt);
    testPP("A term   n1 term   ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorOpt);
  }

  @Test
  public void testCPIteratorStar() throws IOException {
    testPP("A term A A n1 term n2 n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term A A n1 term n2   ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term A A n1 term      ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term A   n1 term n2 n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term A   n1 term n2   ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term A   n1 term      ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term     n1 term n2 n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term     n1 term n2   ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
    testPP("A term     n1 term      ", TestPrettyPrintersMill.parser()::parse_StringCPIteratorStar);
  }

  @Test
  public void testCPIteratorPlus() throws IOException {
    testPP("A term A A n1 term n2 n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorPlus);
    testPP("A term A A n1 term n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorPlus);
    testPP("A term A   n1 term n2 n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorPlus);
    testPP("A term A   n1 term n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorPlus);
  }

  @Test
  public void testCPIteratorAlt() throws IOException {
    testPP("A term A", TestPrettyPrintersMill.parser()::parse_StringCPIteratorAlt);
    testPP("A term n1", TestPrettyPrintersMill.parser()::parse_StringCPIteratorAlt);
  }

  @Test
  public void testCPIteratorAltName() throws IOException {
    testPP("n1 term A", TestPrettyPrintersMill.parser()::parse_StringCPIteratorAltName);
    testPP("n1 term n2", TestPrettyPrintersMill.parser()::parse_StringCPIteratorAltName);
  }

  @Test
  public void testCPNonSepS() throws IOException {
    testPP("term", TestPrettyPrintersMill.parser()::parse_StringCPNonSepS);
    testPP("term n1", TestPrettyPrintersMill.parser()::parse_StringCPNonSepS);
    testPP("term n1.n2", TestPrettyPrintersMill.parser()::parse_StringCPNonSepS);
    testPP("term n1.n2.n3", TestPrettyPrintersMill.parser()::parse_StringCPNonSepS);
  }

  @Test
  public void testCPNonSepP() throws IOException {
    testPP("term n1", TestPrettyPrintersMill.parser()::parse_StringCPNonSepP);
    testPP("term n1.n2", TestPrettyPrintersMill.parser()::parse_StringCPNonSepP);
    testPP("term n1.n2.n3", TestPrettyPrintersMill.parser()::parse_StringCPNonSepP);
  }

  @Test
  public void testCPAltBlock() throws IOException {
    testPP("term { }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
    testPP("term ;", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
    testPP("term { A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
    testPP("term { B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
    testPP("term { A B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
    testPP("term { B A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
    testPP("term { A B A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
    testPP("term { B A B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlock);
  }

  @Test
  public void testCPAltBlockReversed() throws IOException {
    testPP("term { }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
    testPP("term ;", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
    testPP("term { A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
    testPP("term { B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
    testPP("term { A B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
    testPP("term { B A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
    testPP("term { A B A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
    testPP("term { B A B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockReversed);
  }

  @Test
  public void testCPAltBlockOrEmpty() throws IOException {
    testPP("term ;", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockOrEmpty);
    testPP("term { A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockOrEmpty);
    testPP("term { B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockOrEmpty);
    testPP("term { A B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockOrEmpty);
    testPP("term { B A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockOrEmpty);
    testPP("term { A B A }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockOrEmpty);
    testPP("term { B A B }", TestPrettyPrintersMill.parser()::parse_StringCPAltBlockOrEmpty);
  }

  @Test
  public void testCPCGSingle() throws IOException {
    testPP("cg1", TestPrettyPrintersMill.parser()::parse_StringCPCGSingle);
  }

  @Test
  public void testCPCGSingleU1() throws IOException {
    testPP("cg1", TestPrettyPrintersMill.parser()::parse_StringCPCGSingleU1);
  }

  @Test
  public void testCPCGSingleU2() throws IOException {
    testPP("cg1", TestPrettyPrintersMill.parser()::parse_StringCPCGSingleU2);
  }

  @Test
  public void testCPCGMulti1() throws IOException {
    testPP("cg1", TestPrettyPrintersMill.parser()::parse_StringCPCGMulti1);
    testPP("cg2", TestPrettyPrintersMill.parser()::parse_StringCPCGMulti1);
  }

  @Test
  public void testCPCGMulti2() throws IOException {
    testPP("cg1", TestPrettyPrintersMill.parser()::parse_StringCPCGMulti2);
    testPP("cg2", TestPrettyPrintersMill.parser()::parse_StringCPCGMulti2);
  }

  @Test
  public void testCGs() throws IOException {
    for (String input : Arrays.asList("a", "b", "c",
            "a a", "a b", "a c",
            "a b a", "a b b", "a b c",
            "c a", "c b", "c c")) {
      testPP(input, TestPrettyPrintersMill.parser()::parse_StringMultiCGPlus);
      testPP(input, TestPrettyPrintersMill.parser()::parse_StringMultiCGStar);
      testPP(input, TestPrettyPrintersMill.parser()::parse_StringSingleCGPlus);
      testPP(input, TestPrettyPrintersMill.parser()::parse_StringSingleCGStar);
      testPP(input, TestPrettyPrintersMill.parser()::parse_StringMultiCGPlusAlt);
      testPP(input, TestPrettyPrintersMill.parser()::parse_StringMultiCGStarSkipAlt);

    }
    testPP("", TestPrettyPrintersMill.parser()::parse_StringMultiCGStar);
    testPP("", TestPrettyPrintersMill.parser()::parse_StringSingleCGStar);
    testPP("", TestPrettyPrintersMill.parser()::parse_StringMultiCGStarSkipAlt);

    testPP("empty", TestPrettyPrintersMill.parser()::parse_StringMultiCGPlusAlt);
    testPP("empty", TestPrettyPrintersMill.parser()::parse_StringMultiCGStarSkipAlt);
  }

  @Test
  public void testCPCGRepBlock() throws IOException {
    testPP("", TestPrettyPrintersMill.parser()::parse_StringCPCGRepBlock);
    testPP("<<cg1>>", TestPrettyPrintersMill.parser()::parse_StringCPCGRepBlock);
    testPP("<<cg2>>", TestPrettyPrintersMill.parser()::parse_StringCPCGRepBlock);
    testPP("<<cg1>><<cg2>>", TestPrettyPrintersMill.parser()::parse_StringCPCGRepBlock);
    testPP("<<cg2>><<cg1>>", TestPrettyPrintersMill.parser()::parse_StringCPCGRepBlock);
    testPP("<<cg1>><<cg1>>", TestPrettyPrintersMill.parser()::parse_StringCPCGRepBlock);
    testPP("<<cg2>><<cg2>>", TestPrettyPrintersMill.parser()::parse_StringCPCGRepBlock);
  }

  @Test
  public void testDuplicateUsageName() throws IOException {
    testPP("n1", TestPrettyPrintersMill.parser()::parse_StringDuplicateUsageName);
    testPP("n1 = n2", TestPrettyPrintersMill.parser()::parse_StringDuplicateUsageName);
    testPP("n1 = s1\n", TestPrettyPrintersMill.parser()::parse_StringDuplicateUsageName);
  }

  @Test
  @Ignore
  public void testCPAstList() throws IOException {
    // Currently throws "Contains a list of Alts where one is not iterator ready!"
    // as it generates a while ((node.isPresentA()) || (iter_b.hasNext()) || ...
    // This should be handled in a further update
    testPP("", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
    testPP("A", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
    testPP("B", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
    testPP("C", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
    testPP("A B C", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
    testPP("B B", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
    testPP("C C", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
    testPP("A B C B C", TestPrettyPrintersMill.parser()::parse_StringCPAstList);
  }

  @Test
  public void testCPCGSup() throws IOException {
    testPP("(c)", TestPrettyPrintersMill.parser()::parse_StringCPCGSup);
  }

  @Test
  public void testCPCGUnsupName() throws IOException {
    try {
      testPP(".dot", TestPrettyPrintersMill.parser()::parse_StringCPCGUnsupName);
      Assert.fail();
    } catch (IllegalStateException expected) {
      Assert.assertEquals("Unable to find good Constant name for getEnding and value ", expected.getMessage());
    }
  }

  @Test
  public void testCPCGUnsuppDup() throws IOException {
    try {
      testPP("*", TestPrettyPrintersMill.parser()::parse_StringCPCGUnsuppDup);
      Assert.fail();
    } catch (IllegalStateException expected) {
      Assert.assertEquals("Unable to handle ConstantGroup with size of 1, but multiple elements named op present", expected.getMessage());
    }
  }

  @Test
  public void testCPListInDList() throws IOException {
    testPP("B", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
    testPP("B B", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
    testPP("B B A", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
    testPP("B B A A", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
    testPP("B B A B", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
    testPP("B B A A B", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
    testPP("B B A B A", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
    testPP("B B A A B A", TestPrettyPrintersMill.parser()::parse_StringCPListInDList);
  }

  @Test
  public void testCPListInPList() throws IOException {
    testPP("B", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
    testPP("B B", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
    testPP("B B A", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
    testPP("B B A A", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
    testPP("B B A B", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
    testPP("B B A A B", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
    testPP("B B A B A", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
    testPP("B B A A B A", TestPrettyPrintersMill.parser()::parse_StringCPListInPList);
  }

  @Test
  public void testORA() throws IOException {
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringORA);
  }

  @Test
  public void testORB() throws IOException {
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringORB);
  }

  @Test
  public void testORC() throws IOException {
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringORC);
  }

  @Test
  public void testORD() throws IOException {
    // While the empty list is possible, we assume it is not desired by the ASTRule
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringD);
    testPP("n1 n11 < test n2 test n22 >", TestPrettyPrintersMill.parser()::parse_StringD);
  }

  @Test
  public void testORE() throws IOException {
    // While the empty list is possible, we assume it is not desired by the ASTRule
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringE);
    testPP("n1 n11 < test n2 test n22 >", TestPrettyPrintersMill.parser()::parse_StringE);
  }

  @Test
  public void testORF() throws IOException {
    // While the empty list is possible, we assume it is not desired by the ASTRule
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringF);
    testPP("n1 n11 < test n2 test n22 >", TestPrettyPrintersMill.parser()::parse_StringF);
  }


  @Test
  public void testORG() throws IOException {
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringG);
  }

  @Test
  public void testORH() throws IOException {
    testPP("      < test n2 >", TestPrettyPrintersMill.parser()::parse_StringH);
    testPP("n1    < test n2 >", TestPrettyPrintersMill.parser()::parse_StringH);
    testPP("n1 n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringH);
  }

  @Test
  public void testORI() throws IOException {
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringI);
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringI);
  }

  @Test
  public void testORJ() throws IOException {
    testPP("      <         >", TestPrettyPrintersMill.parser()::parse_StringJ);
    testPP("n1    <         >", TestPrettyPrintersMill.parser()::parse_StringJ);
    testPP("      < test n2 >", TestPrettyPrintersMill.parser()::parse_StringJ);
    testPP("n1    < test n2 >", TestPrettyPrintersMill.parser()::parse_StringJ);
  }

  @Test
  public void testORK() throws IOException {
    testPP("n1     < test n2 >", TestPrettyPrintersMill.parser()::parse_StringK);
    testPP("n1 n11 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringK);
    testPP("n1     < test n2 test n22 >", TestPrettyPrintersMill.parser()::parse_StringK);
  }

  @Test
  public void testORL() throws IOException {
    testPP("n1       < test n2 >", TestPrettyPrintersMill.parser()::parse_StringL);
    testPP("n1 n1    < test n2 >", TestPrettyPrintersMill.parser()::parse_StringL);
    testPP("n1       < test n2 test n2 >", TestPrettyPrintersMill.parser()::parse_StringL);
    testPP("n1 n1    < test n2 test n2 >", TestPrettyPrintersMill.parser()::parse_StringL);
  }

  @Test
  public void testORM() throws IOException {
    testPP("n1    < test n2         >", TestPrettyPrintersMill.parser()::parse_StringM);
    testPP("n1 n2 < test n2         >", TestPrettyPrintersMill.parser()::parse_StringM);
    testPP("n1    < test n2 test n2 >", TestPrettyPrintersMill.parser()::parse_StringM);
    testPP("n1 n2 < test n2 test n2 >", TestPrettyPrintersMill.parser()::parse_StringM);
  }

  @Test
  public void testIterationStatement() throws IOException {
    testPP("Do Statement While ( ExpressionSequence ) ;", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("While ( ExpressionSequence ) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( ; ;) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( ExpressionSequence ; ;) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( ExpressionSequence ;ExpressionSequence ;) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( ExpressionSequence ;ExpressionSequence ; ExpressionSequence) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Var ; ;) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Var ; ExpressionSequence  ;) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Var ; ExpressionSequence ;ExpressionSequence ) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Var ; ExpressionSequence ;ExpressionSequence ) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Var VariableDeclaration ; ExpressionSequence ;) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Var VariableDeclaration, VariableDeclaration ; ExpressionSequence ;) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Expression In  ExpressionSequence) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
    testPP("For ( Var VariableDeclaration In ExpressionSequence) Statement", TestPrettyPrintersMill.parser()::parse_StringIterationStatement);
  }

  @Test
  public void testLiterals() throws IOException {
    testPP("null", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("true", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("false", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("'a'", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("'0'", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("\"a\"", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("\"ab\"", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("0", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("1", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("1l", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("1.0f", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
    testPP("1.0", TestPrettyPrintersMill.parser()::parse_StringLiteralProd);
  }

  @Test
  public void testSignedLiterals() throws IOException {
    testPP("null", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("true", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("false", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("'a'", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("'0'", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("\"a\"", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("\"ab\"", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("0", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("1", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("-1", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("1l", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("-1l", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("1.0f", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("-1.0f", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("1.0", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
    testPP("-1.0", TestPrettyPrintersMill.parser()::parse_StringSignedLiteralProd);
  }


  protected <A extends ASTTestPrettyPrintersNode> void testPP(String input, ParserFunction<String, Optional<A>> parserFunction) throws IOException {
    Optional<A> parsedOpt = parserFunction.parse(input);
    Assert.assertTrue("Failed to parse input", parsedOpt.isPresent());
    String prettyInput = (new TestPrettyPrintersFullPrettyPrinter(new IndentPrinter())).prettyprint(parsedOpt.get());
    Optional<A> parsedPrettyOpt = parserFunction.parse(prettyInput);
    String findings = Joiners.COMMA.join(Log.getFindings());
    if (parsedPrettyOpt.isEmpty())
      Assert.assertEquals("Failed to parse pretty: " + findings, input, prettyInput);
    if (!parsedOpt.get().deepEquals(parsedPrettyOpt.get()))
      Assert.assertEquals("Not deep equals: " + findings, input, prettyInput);
  }

  @FunctionalInterface
  interface ParserFunction<P, R> {
    R parse(P a) throws IOException;
  }
}
