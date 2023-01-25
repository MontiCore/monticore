// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.keywordaddingtestprettyprinters.KeywordAddingTestPrettyPrintersMill;
import de.monticore.keywordaddingtestprettyprinters._ast.ASTKeywordAddingTestPrettyPrintersNode;
import de.se_rwth.commons.logging.Log;
import org.junit.*;

import java.io.IOException;

public class KeywordAddingTest extends PPTestClass {

  @BeforeClass
  public static void init() {
    KeywordAddingTestPrettyPrintersMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void testSomeProdOld() throws IOException {
    testPP("A term A", KeywordAddingTestPrettyPrintersMill.parser()::parse_StringSomeProdWhichMightUsesReplacing);
  }

  @Test
  public void testSomeProdNew() throws IOException {
    testPP("notquiteA term notquiteA", KeywordAddingTestPrettyPrintersMill.parser()::parse_StringSomeProdWhichMightUsesReplacing,
        s -> s.contains("A term A"));
    // The output should be A term A, but a comment should have been added to the FullPrettyPrinter
  }

  @Override
  protected String fullPrettyPrint(ASTNode node) {
    return KeywordAddingTestPrettyPrintersMill.prettyPrint((ASTKeywordAddingTestPrettyPrintersNode) node, true);
  }
}
