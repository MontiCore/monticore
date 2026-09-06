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

  protected String fullPrettyPrintV2(ASTNode node) {
    return "--not-implemented--";
  }

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
    // Also check v2 (if present)
    String prettyInputV2 = this.fullPrettyPrintV2(parsedOpt.get());
    if ("--not-implemented--".equals(prettyInputV2)) return;
    Optional<A> parsedPrettyOptV2 = parserFunction.parse(prettyInputV2);
    findings = Joiners.COMMA.join(Log.getFindings());
    if (parsedPrettyOptV2.isEmpty())
      Assertions.assertEquals(input, prettyInputV2, "Failed to parse pretty v2: " + findings);
    if (!parsedOpt.get().deepEquals(parsedPrettyOptV2.get()))
      Assertions.assertEquals(input, prettyInputV2, "Not deep equals v2 vs orig: " + findings);
    if (!additionalCheck.apply(prettyInputV2))
      Assertions.fail("Failed check v2, got pp-output: '" + prettyInputV2 + "'");
  }

  protected <A extends ASTNode> void testPP(String input, ParserFunction<String, Optional<A>> parserFunction) throws IOException {
    testPP(input, parserFunction, s -> true);
  }

  @FunctionalInterface
  interface ParserFunction<P, R> {
    R parse(P a) throws IOException;
  }
}
