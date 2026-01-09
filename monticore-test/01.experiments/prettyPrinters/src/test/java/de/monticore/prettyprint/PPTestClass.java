// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Common class for testing Pretty Printers
 */
public abstract class PPTestClass {


  protected abstract String fullPrettyPrint(ASTNode node);

  protected <A extends ASTNode> void testPP(String input, ParserFunction<String, Optional<A>> parserFunction, Function<String, Boolean> additionalCheck) throws IOException {
    Optional<A> parsedOpt = parserFunction.parse(input);
    Assertions.assertTrue(parsedOpt.isPresent(), "Failed to parse input");
    String prettyInput = this.fullPrettyPrint(parsedOpt.get());
    Optional<A> parsedPrettyOpt = parserFunction.parse(prettyInput);
    String findings = Joiners.COMMA.join(Log.getFindings());
    if (parsedPrettyOpt.isEmpty())
      Assertions.assertEquals(input, prettyInput, "Failed to parse pretty: " + findings);
    if (!parsedOpt.get().deepEquals(parsedPrettyOpt.get()))
      Assertions.assertEquals(input, prettyInput, "Not deep equals: " + findings);
    if (!additionalCheck.apply(prettyInput))
      Assertions.fail("Failed check, got pp-output: '" + prettyInput + "'");
  }

  protected <A extends ASTNode> void testPP(String input, ParserFunction<String, Optional<A>> parserFunction) throws IOException {
    testPP(input, parserFunction, s -> true);
  }

  @FunctionalInterface
  interface ParserFunction<P, R> {
    R parse(P a) throws IOException;
  }
}
