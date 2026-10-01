// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.keywordaddingtestprettyprinters.KeywordAddingTestPrettyPrintersMill;
import de.monticore.keywordaddingtestprettyprinters._ast.ASTKeywordAddingTestPrettyPrintersNode;
import de.monticore.keywordaddingtestprettyprinters._prettyprint.KeywordAddingTestPrettyPrintersFullPrettyPrinter;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@TestWithMCLanguage(KeywordAddingTestPrettyPrintersMill.class)
public class KeywordAddingTest extends PPTestClass {


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

  @Override
  protected String fullPrettyPrintV2(ASTNode node) {
    return new KeywordAddingTestPrettyPrintersFullPrettyPrinter(new FormattingPrinter(new IFormatter.DefaultIFormatter()), true).prettyprint(node);
  }
}
