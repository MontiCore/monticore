// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.keywordaddingtestprettyprinters.KeywordAddingTestPrettyPrintersMill;
import de.monticore.testprettyprinters.TestPrettyPrintersMill;
import de.monticore.testprettyprinters._ast.ASTProdNamedTerminal;
import de.monticore.testprettyprinters._ast.ASTToBeReplacedKeyword;
import de.monticore.testprettyprinters._parser.TestPrettyPrintersParser;
import de.monticore.testprettyprinters._prettyprint.TestPrettyPrintersFullPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.junit.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * Test the PrettyPrinter Generation
 */
public class TestPrettyPrinterTest extends PPTestClass {

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

  @Override
  protected String fullPrettyPrint(ASTNode astNode){
    return  (new TestPrettyPrintersFullPrettyPrinter(new IndentPrinter())).prettyprint(astNode);
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

      // And test with D
      input = input + " D";
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
    testPP("n1 < test n2 >", TestPrettyPrintersMill.parser()::parse_StringORD);
    testPP("n1 n11 < test n2 test n22 >", TestPrettyPrintersMill.parser()::parse_StringORD);
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

  @Test
  public void testOptEnd() throws IOException {
    testPP("A -> B;", TestPrettyPrintersMill.parser()::parse_StringOptEnd);
    testPP("A -> B C", TestPrettyPrintersMill.parser()::parse_StringOptEnd);
  }

  @Test
  public void testSDCall() throws IOException {
    testPP("A", TestPrettyPrintersMill.parser()::parse_StringSDCall);
    testPP("A B", TestPrettyPrintersMill.parser()::parse_StringSDCall);
    testPP("static A B", TestPrettyPrintersMill.parser()::parse_StringSDCall);
    testPP("trigger static A B", TestPrettyPrintersMill.parser()::parse_StringSDCall);
    testPP("trigger A B", TestPrettyPrintersMill.parser()::parse_StringSDCall);
  }

  @Test
  public void testNoSpace() throws IOException {
    testPP("foo::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredA, s -> s.contains("::"));
    testPP("foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredA, s -> s.contains("::"));
    testPP("foo::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredB, s -> s.contains("::"));
    testPP("foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredB, s -> s.contains("::"));
    testPP("foo::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredC, s -> s.contains("::"));
    testPP("foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredC, s -> s.contains("::"));
    testPP("foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredD, s -> s.contains("::"));
    testPP("yes foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredE, s -> s.contains("::"));
    testPP("foo:::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredF, s -> s.contains(":::"));
    testPP("foo ::: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredF, s -> s.contains(":::"));
    testPP("foo:::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredG, s -> s.contains(":::"));
    testPP("foo ::: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpacePredG, s -> s.contains(":::"));
  }

  @Test
  public void testNoSpace2017() throws IOException {
    try {
      testPP("  n1.n2", TestPrettyPrintersMill.parser()::parse_StringNoWhiteSpaceA2017);
      Assert.fail();
    } catch (IllegalStateException expected) {
      Assert.assertEquals("Unable to target the previous token using the noSpace control directive. You may also need to override the printing methods of productions using this NonTerminal", expected.getMessage());
    }
  }

  @Test
  public void testNoSpaceAlts() throws IOException {
    testPP("-n1", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAlts, s -> s.contains("-n1"));
    testPP("+n1", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAlts, s -> s.contains("+n1"));
    testPP("-n1 n2", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAltsO, s -> s.contains("-n1 "));
    testPP("-n1", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAlts2, s -> s.contains("-n1"));
    testPP("a+ n1", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAlts2, s -> s.contains("a+n1"));
  }

  @Test
  public void testNoSpaceAltsOpt() throws IOException {
    try {
      testPP("-n1 +", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAltsOpt);
      Assert.fail();
    } catch (IllegalStateException expected) {
      Assert.assertEquals("Unable to handle noSpace control directive for block of non-default iteration", expected.getMessage());
    }
    try {
      testPP("n1+", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAltsOpt);
      Assert.fail();
    } catch (IllegalStateException expected) {
      Assert.assertEquals("Unable to handle noSpace control directive for block of non-default iteration", expected.getMessage());
    }
  }

  @Test
  public void testNoSpaceAltsOverflow() throws IOException {
    try {
      testPP("-n1.n2", TestPrettyPrintersMill.parser()::parse_StringNoSpaceAltsOverflow);
      Assert.fail();
    } catch (IllegalStateException expected) {
      Assert.assertEquals("Unable to handle noSpace control directive for block with multiple alts of different length", expected.getMessage());
    }
  }

  @Test
  public void testNoSpaceExp() throws IOException {
    // Due to us ignoring any expression for simplicity, all productions will use the noSpace control directive
    testPP("foo::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpaceExpOrT, s -> s.contains("::"));
    testPP("foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpaceExpOrT, s -> s.contains("::"));
    testPP("foo::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpaceExpOrF, s -> s.contains("::"));
    testPP("foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpaceExpOrF, s -> s.contains("::"));
    testPP("foo::bar", TestPrettyPrintersMill.parser()::parse_StringNoSpaceExpAndT, s -> s.contains("::"));
    testPP("foo :: bar", TestPrettyPrintersMill.parser()::parse_StringNoSpaceExpAndT, s -> s.contains("::"));
    // NoSpaceExpAndF: && false will never parse
  }

  @Test
  public void testNoSpaceSpecial() throws IOException {
    testPP("{a}", TestPrettyPrintersMill.parser()::parse_StringNoSpaceSpecialB, s -> s.equals("{a}"));
    testPP(";a;", TestPrettyPrintersMill.parser()::parse_StringNoSpaceSpecialS, s -> s.equals(";a;"));
  }

  @Test
  public void testUsedTerminal() throws IOException {
    testPP("a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalD);
    testPP("a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalQ);
    testPP("  b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalQ);
    testPP("a   b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalP);
    testPP("a a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalP);
    testPP("    b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalS);
    testPP("a   b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalS);
    testPP("a a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalS);
  }

  @Test
  public void testUsedTerminalBlocks() throws IOException {
    testPP("a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBD);
    testPP("a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBQ);
    testPP("  b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBQ);
    testPP("a   b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBP);
    testPP("a a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBP);
    testPP("    b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBS);
    testPP("a   b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBS);
    testPP("a a b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalBS);

    testPP("a c b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2D);
    testPP("a c b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2Q);
    testPP("    b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2Q);
    testPP("a c     b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2P);
    testPP("a c a c b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2P);
    testPP("        b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2S);
    testPP("a c     b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2S);
    testPP("a c a c b", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalB2S);
  }

  @Test
  public void testUsedTerminalAlt() throws IOException {
    testPP("A", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalAlt);
    testPP("( A )", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalAlt);
    testPP("(A)", TestPrettyPrintersMill.parser()::parse_StringUsedTerminalAlt);
  }

  @Test
  public void testMethodDeclarationStub() throws IOException {
    testPP("MCReturnType foob FormalParameters ;", TestPrettyPrintersMill.parser()::parse_StringMethodDeclarationStub);
    testPP("MCReturnType foob FormalParameters [ ] ;", TestPrettyPrintersMill.parser()::parse_StringMethodDeclarationStub);
  }

  @Test
  public void testTokens() throws IOException {
    testPP("a \"foob\"", TestPrettyPrintersMill.parser()::parse_StringTokenString);
    testPP("a foob", TestPrettyPrintersMill.parser()::parse_StringTokenName);
    testPP("a 'f'", TestPrettyPrintersMill.parser()::parse_StringTokenChar);
    testPP("a 12", TestPrettyPrintersMill.parser()::parse_StringTokenDigits);
  }

  @Test
  public void testGuessSpace() throws IOException {
    testPP("A a A", TestPrettyPrintersMill.parser()::parse_StringGuessSpace1, s -> s.equals("A a A"));
    testPP("A * A", TestPrettyPrintersMill.parser()::parse_StringGuessSpace2, s -> s.equals("A*A"));
    testPP("A*A", TestPrettyPrintersMill.parser()::parse_StringGuessSpace2, s -> s.equals("A*A"));
  }


  @Test
  public void testTrimRight() {
    IndentPrinter printer = new IndentPrinter();
    printer.stripTrailing();
    Assert.assertEquals("", printer.getContent());
    printer.print("Hello world");
    printer.stripTrailing();
    Assert.assertEquals("Hello world", printer.getContent());
    printer.print(" ");
    printer.stripTrailing();
    Assert.assertEquals("Hello world", printer.getContent());
    printer.println();
    printer.stripTrailing();
    Assert.assertEquals("Hello world\n", printer.getContent());
  }

  @Test
  public void testInterfaceI() throws IOException {
    testPP("InterfaceImpl1", TestPrettyPrintersMill.parser()::parse_StringInterfaceImpl1);
    testPP("InterfaceImpl2", TestPrettyPrintersMill.parser()::parse_StringInterfaceImpl2);
    testPP("InterfaceImpl1", TestPrettyPrintersMill.parser()::parse_StringInterfaceI);
    testPP("InterfaceImpl2", TestPrettyPrintersMill.parser()::parse_StringInterfaceI);
  }

  @Test
  public void testReplaceKeyword() throws IOException {
    testPP("ActuallyReplacedKeyword", TestPrettyPrintersMill.parser()::parse_StringToBeReplacedKeyword);
    testPP("ActuallyReplacedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeReplacedKeywordA);
    testPP("other ActuallyReplacedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeReplacedKeywordB);
//    testPP("othercg ActuallyReplacedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeReplacedKeywordC); // See #3335
  }

  @Test
  public void testReplaceKeywordFail() throws IOException {
    TestPrettyPrintersParser parser = TestPrettyPrintersMill.parser();
    Optional<ASTToBeReplacedKeyword> astOpt = parser.parse_StringToBeReplacedKeyword("ReplacedKeyword");
    Assert.assertTrue(astOpt.isEmpty());
  }

  @Test
  public void testAddReplaceKeyword() throws IOException {
    // added keyword
    testPP("ActuallyAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringToBeAddedKeyword);
    testPP("ActuallyAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeAddedKeywordA);
    testPP("other ActuallyAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeAddedKeywordB);
//    testPP("othercg ActuallyAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeAddedKeywordC); // #3335
    // and the original
    testPP("ToBeAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringToBeAddedKeyword);
    testPP("ToBeAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeAddedKeywordA);
    testPP("other ToBeAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeAddedKeywordB);
    testPP("othercg ToBeAddedKeyword", TestPrettyPrintersMill.parser()::parse_StringProdUsingToBeAddedKeywordC);
  }


  @Test
  @Ignore // Ignored - see #3328
  public void testProdNamedTerminalParser() throws IOException {
    Optional<ASTProdNamedTerminal> astOpt = KeywordAddingTestPrettyPrintersMill.parser().parse_StringProdNamedTerminal("newprodNamedTerminal");
    Assert.assertTrue(astOpt.isPresent());
    Assert.assertEquals("newprodNamedTerminal", astOpt.get().getTerm()); // Parser Action does otherwise
  }

  @Test
  public void testAddingProdNamedTerminalOld() throws IOException {
    testPP("prodNamedTerminal", KeywordAddingTestPrettyPrintersMill.parser()::parse_StringProdNamedTerminal);
  }

  @Test
  @Ignore // Ignored - see #3328
  public void testAddingProdNamedTerminalNew() throws IOException {
    testPP("newprodNamedTerminal", KeywordAddingTestPrettyPrintersMill.parser()::parse_StringProdNamedTerminal);
  }
}
