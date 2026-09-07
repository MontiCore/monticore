/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
   */
  public static <N extends ASTNode> void testPrettyPrinter(
      String model,
      MCConcreteParser parser,
      ParseFunction<N> parseFunc,
      Function<N, String> prettyPrintFunc,
      Predicate<String> additionalCheck
  ) {
    // parse the model
    Optional<N> astOpt;
    try {
      astOpt = parseFunc.apply(model);
    }
    catch (IOException e) {
      fail("Failed to parse input, exception occurred", e);
      return;
    }
    MCAssertions.assertNoFindings();
    assertTrue(astOpt.isPresent(), "Failed to parse input");
    assertFalse(parser.hasErrors(), "Parser has Errors");
    N ast = astOpt.get();
    // pretty print the model
    String prettyPrinted = prettyPrintFunc.apply(ast);
    MCAssertions.assertNoFindings();
    // parse the pretty printed model
    Optional<N> prettyPrintedAstOpt;
    try {
      prettyPrintedAstOpt = parseFunc.apply(prettyPrinted);
    }
    catch (IOException e) {
      fail(
          "Failed to parse pretty printed model"
              + ", exception occurred", e
      );
      return;
    }
    MCAssertions.assertNoFindings();
    assertFalse(parser.hasErrors());
    assertTrue(prettyPrintedAstOpt.isPresent());
    // compare both ASTs
    assertTrue(ast.deepEquals(prettyPrintedAstOpt.get()),
        "ASTs are not deep equals"
    );
    // run an additional check
    if (!additionalCheck.test(prettyPrinted)) {
      fail("Pretty Printer test: failed during additional check");
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
   */
  public static <N extends ASTNode> void testPrettyPrinter(
      String model,
      MCConcreteParser parser,
      ParseFunction<N> parseFunc,
      Function<N, String> prettyPrintFunc
  ) {
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
