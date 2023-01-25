// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.keywordreplacingtestprettyprinters._ast.ASTKeywordReplacingTestPrettyPrintersNode;
import de.monticore.keywordreplacingtestprettyprinters.KeywordReplacingTestPrettyPrintersMill;
import de.monticore.keywordreplacingtestprettyprinters._ast.ASTSomeProdWhichUsesReplacing;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class KeywordReplacingTest extends PPTestClass {

  @BeforeClass
  public static void init() {
    KeywordReplacingTestPrettyPrintersMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void testSomeProdForException() throws IOException {
    Optional<ASTSomeProdWhichUsesReplacing> astOpt = KeywordReplacingTestPrettyPrintersMill.parser().parse_StringSomeProdWhichUsesReplacing("notquiteA term notquiteA");
    Assert.assertTrue(astOpt.isPresent());
    try {
      fullPrettyPrint(astOpt.get());
      Assert.fail("Did not fail when printing");
    }
    catch (IllegalStateException expected) {
      Assert.assertEquals("replacekeyword requires HC effort for pretty printing", expected.getMessage());
      Assert.assertEquals(1, Log.getErrorCount());
    }
  }

  @Override
  protected String fullPrettyPrint(ASTNode node) {
    return KeywordReplacingTestPrettyPrintersMill.prettyPrint((ASTKeywordReplacingTestPrettyPrintersNode) node, true);
  }
}
