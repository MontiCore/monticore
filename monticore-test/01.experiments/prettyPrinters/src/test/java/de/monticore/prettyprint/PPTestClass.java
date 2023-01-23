// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;

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
    Assert.assertTrue("Failed to parse input", parsedOpt.isPresent());
    String prettyInput = this.fullPrettyPrint(parsedOpt.get());
    Optional<A> parsedPrettyOpt = parserFunction.parse(prettyInput);
    String findings = Joiners.COMMA.join(Log.getFindings());
    if (parsedPrettyOpt.isEmpty())
      Assert.assertEquals("Failed to parse pretty: " + findings, input, prettyInput);
    if (!parsedOpt.get().deepEquals(parsedPrettyOpt.get()))
      Assert.assertEquals("Not deep equals: " + findings, input, prettyInput);
    if (!additionalCheck.apply(prettyInput))
      Assert.fail("Failed check, got pp-output: '" + prettyInput + "'");
  }

  protected <A extends ASTNode> void testPP(String input, ParserFunction<String, Optional<A>> parserFunction) throws IOException {
    testPP(input, parserFunction, s -> true);
  }

  @FunctionalInterface
  interface ParserFunction<P, R> {
    R parse(P a) throws IOException;
  }
}
