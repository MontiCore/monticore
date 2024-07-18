/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This test checks if all (hand-written) MontiCore grammars are print-able using the generated pretty printers
 */
public class GrammarPrettyPrinterTest {
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
  }

  @Test
  public void testPrintMainGrammars() throws IOException {
    testPrintInPath("src/main/grammars");
  }


  @Test
  public void testPrintTestGrammars() throws IOException {
    testPrintInPath("src/test/grammars");
  }

  public void testPrintInPath(String grammarDir) throws IOException {
    File grammarFile = new File(grammarDir);
    if (!grammarFile.exists()) return;
    try (
        Stream<Path> stream = Files.walk(grammarFile.toPath())) {
      stream.filter(Files::isRegularFile).filter(f -> f.getFileName().toString().endsWith(".mc4")).forEach(path -> {
        try {
          this.testPrintGrammar(path);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  public void testPrintGrammar(Path path) throws IOException {
    System.err.println(path.toAbsolutePath());
    Optional<ASTMCGrammar> astOpt = Grammar_WithConceptsMill.parser().parse(path.toString());

    Assertions.assertTrue(astOpt.isPresent());

    String pretty = Grammar_WithConceptsMill.prettyPrint(astOpt.get(), true);
    Assertions.assertEquals(0, Log.getFindingsCount(), "Failed to pretty print without findings: " + path);
    Optional<ASTMCGrammar> parsedAST = Grammar_WithConceptsMill.parser().parse_String(pretty);
    if (parsedAST.isEmpty()) {
      Assertions.assertEquals(Files.readString(path), pretty, "Failed to parse " + path);
      Assertions.fail("Failed to parse " + path);
    }
    if (!Log.getFindings().isEmpty()) {
      Assertions.assertEquals(Files.readString(path), pretty, "Failed to parse " + path + " without findings");
      Assertions.fail("Failed to parse " + path + " without findings");
    }

    if (!astOpt.get().deepEquals(parsedAST.get())) {
      Assertions.assertEquals(Files.readString(path), pretty, "Failed to deep-equals " + path);
      Assertions.fail("Failed to deep-equals");
    }
  }

}
