package de.monticore.runtime.junit.jupyter;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.monticore.runtime.junit.jupyter.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offers functions to test pretty printers
 */
public abstract class PrettyPrinterTester {

  /**
   * Tests pretty printing.
   * The Model is parsed, pretty printed and parsed again.
   * The AST of the pretty printed model is expected
   * to be comparable to the AST of the original model.
   *
   * @param model           the model to be pretty printed
   * @param parser          the parser used to convert the model to an AST
   * @param parseFunc       the concrete parse function used, e.g.,
   *                        {@code myParser::parse_StringMyNonTerminal}
   * @param prettyPrintFunc the actual pretty printing operation, e.g.,
   *                        {@code ast -> MyMill.prettyPrint(ast, true)}
   * @param additionalCheck additional check on the pretty printed String
   * @param <N>             the type of the ASTNode after parsing
   * @throws IOException
   */
  public static <N extends ASTNode> void testPrettyPrinter(
      String model,
      MCConcreteParser parser,
      ParseFunction<N> parseFunc,
      Function<N, String> prettyPrintFunc,
      Predicate<String> additionalCheck
  ) throws IOException {
    // parse the model
    Optional<N> astOpt = parseFunc.apply(model);
    assertNoFindings();
    assertTrue(astOpt.isPresent(), "Failed to parse input");
    Assertions.assertFalse(parser.hasErrors(), "Parser has Errors");
    N ast = astOpt.get();
    // pretty print the model
    String prettyPrinted = prettyPrintFunc.apply(ast);
    assertNoFindings();
    // parse the pretty printed model
    Optional<N> prettyPrintedAstOpt = parseFunc.apply(prettyPrinted);
    assertNoFindings();
    Assertions.assertFalse(parser.hasErrors());
    assertTrue(prettyPrintedAstOpt.isPresent());
    // compare both ASTs
    assertTrue(ast.deepEquals(prettyPrintedAstOpt.get()),
        "ASTs are not deep equals"
    );
    // run an additional check
    if (!additionalCheck.test(prettyPrinted)) {
      Assertions.fail("Pretty Printer test: failed during additional check");
    }
  }

  /**
   * Tests pretty printing.
   * The Model is parsed, pretty printed and parsed again.
   * The AST of the pretty printed model is expected
   * to be comparable to the AST of the original model.
   *
   * @param model           the model to be pretty printed
   * @param parser          the parser used to convert the model to an AST
   * @param parseFunc       the concrete parse function used, e.g.,
   *                        {@code myParser::parse_StringMyNonTerminal}
   * @param prettyPrintFunc the actual pretty printing operation, e.g.,
   *                        {@code ast -> MyMill.prettyPrint(ast, true)}
   * @param <N>             the type of the ASTNode after parsing
   * @throws IOException
   */
  public static <N extends ASTNode> void testPrettyPrinter(
      String model,
      MCConcreteParser parser,
      ParseFunction<N> parseFunc,
      Function<N, String> prettyPrintFunc
  ) throws IOException {
    testPrettyPrinter(model, parser, parseFunc, prettyPrintFunc, m -> true);
  }

  /**
   * Represents the function that parses a String.
   * E.g., {@code myParser::parse_StringMyNonTerminal}
   *
   * @param <N> the type of the ASTNode after Parsing.
   */
  @FunctionalInterface
  public interface ParseFunction<N extends ASTNode> {
    Optional<N> apply(String t) throws IOException;
  }

}
