/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The known incompatibilities of the temporal grammars with other grammars, found by
 * {@link AbstractTemporalCompatibilityTest}. Each entry skips the matching test cases and
 * names the reason, so that fixing a defect means removing its entries.
 */
public class KnownTemporalIncompatibilities {

  /**
   * @param host    the simple name of the test class, e.g. {@code TemporalsWithMCJavaLiteralsTest},
   *                or {@code null} for all hosts
   * @param context the context of the test, e.g. "standalone", or {@code null} for all contexts
   * @param input   the inputs that are affected
   * @param reason  the defect and its cause
   */
  protected record Incompatibility(String host, String context, Predicate<String> input,
                                   String reason) {
  }

  // The closing quote of an escaped literal and the opening quote of the next one enclose
  // a String token of MCCommonLiterals, e.g. '" < d"' in 'd"12:00" < d"13:00"'
  protected static final String TWO_ESCAPED_LITERALS =
      "D7: two escaped literals in one input are lexed as one String";

  protected static final List<Incompatibility> INCOMPATIBILITIES = List.of(
      new Incompatibility(null, "escaped", KnownTemporalIncompatibilities::hasTwoEscapedLiterals,
          TWO_ESCAPED_LITERALS),
      new Incompatibility(null, "escaped expression",
          KnownTemporalIncompatibilities::hasTwoEscapedLiterals, TWO_ESCAPED_LITERALS)
  );

  protected static boolean hasTwoEscapedLiterals(String input) {
    return input.indexOf("d\"") != input.lastIndexOf("d\"");
  }

  public static Optional<String> reason(String host, String context, String input) {
    return INCOMPATIBILITIES.stream()
        .filter(entry -> entry.host() == null || entry.host().equals(host))
        .filter(entry -> entry.context() == null || entry.context().equals(context))
        .filter(entry -> entry.input().test(input))
        .map(Incompatibility::reason)
        .findFirst();
  }
}
