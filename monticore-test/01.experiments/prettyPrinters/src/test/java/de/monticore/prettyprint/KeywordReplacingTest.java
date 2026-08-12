// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.keywordreplacingtestprettyprinters.KeywordReplacingTestPrettyPrintersMill;
import de.monticore.keywordreplacingtestprettyprinters._ast.ASTSomeProdWhichUsesReplacing;
import de.monticore.keywordreplacingtestprettyprinters._prettyprint.KeywordReplacingTestPrettyPrintersFullPrettyPrinter;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

@TestWithMCLanguage(KeywordReplacingTestPrettyPrintersMill.class)
public class KeywordReplacingTest extends PPTestClass {

  @Test
  public void testSomeProdForException() throws IOException {
    Optional<ASTSomeProdWhichUsesReplacing> astOpt = KeywordReplacingTestPrettyPrintersMill.parser().parse_StringSomeProdWhichUsesReplacing("notquiteA term notquiteA");
    Assertions.assertTrue(astOpt.isPresent());
    fullPrettyPrint(astOpt.get());
    MCAssertions.assertHasFinding(f->f.getMsg().endsWith("replacekeyword requires HC effort for pretty printing"));
  }

  @Override
  protected String fullPrettyPrint(ASTNode node) {
    return KeywordReplacingTestPrettyPrintersMill.prettyPrint(node, true);
  }

  @Override
  protected String fullPrettyPrintV2(ASTNode node) {
    return new KeywordReplacingTestPrettyPrintersFullPrettyPrinter(new FormattingPrinter(new IFormatter.DefaultIFormatter()), true).prettyprint(node);
  }
}
