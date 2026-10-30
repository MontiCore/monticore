/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals;

import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(CombineExpressionsWithLiteralsMill.class)
class ParserTest {

  static final int REPETITIONS = 1;
  static final int REPETITIONS_WARM_UP = 20;

  static final Map<String, List<Long>> times = new HashMap<>();

  @RepeatedTest(REPETITIONS)
  void parseBigExpr() throws IOException {
    String expr = "(!(x1 && x2) || !(x1 && x3) || !(x1 && x4) || !(x1 && x5) " +
            "|| !(x1 && x6) || !(x1 && x7) || !(x1 && x8) || !(x1 && x9) " +
            "|| !(x1 && x10) || !(x1 && x11) || !(x1 && x12) || !(x1 && x13) " +
            "|| !(x1 && x14) || !(x1 && x15) " +
            "|| !(x2 && x3) || !(x2 && x4) || !(x2 && x5) || !(x2 && x6) " +
            "|| !(x2 && x7) || !(x2 && x8) || !(x2 && x9) || !(x2 && x10) " +
            "|| !(x2 && x11) || !(x2 && x12) || !(x2 && x13) || !(x2 && x14) " +
            "|| !(x2 && x15)) ";

    long start = System.currentTimeMillis();
    Optional<ASTExpression> ast = CombineExpressionsWithLiteralsMill.parser().parse_StringExpression(expr);
    assertTrue(ast.isPresent());
    addTime("parseBigExpr", System.currentTimeMillis() - start);
  }

  @RepeatedTest(REPETITIONS)
  void parseBigExpr2() throws IOException {

    String expr =
            "((f1 + f2) * (f1 + f3) * (f1 + f4) / (f1 + f5) + (f1 + f6) / (f1 + f7) + (f1 + f8) * (f1 + f9) + (f1 + f10) + (f1 + f11) / (f1 + f12) + (f1 + f13) + (f + f14) +\n" +
                    " (f1 + f3) * (f1 + f4) / (f1 + f5) + (f1 + f6) / (f1 + f7) + (f1 + f8) * (f1 + f9) + (f1 + f10) + (f1 + f11) / (f1 + f12) + (f1 + f13) + (f + f14) +\n" +
                    "  (f2 + f3) * (f2 + f4) * (f2 + f5) * (f2 + f6) / (f2 + f7) / (f2 + f8) * (f2 + f9) + (f2 + f10) * (f2 + f11) * (f2 + f12) * (f2 + f13) * (f2 + f14))";

    long start = System.currentTimeMillis();
    Optional<ASTExpression> ast = CombineExpressionsWithLiteralsMill.parser().parse_StringExpression(expr);
    assertTrue(ast.isPresent());
    addTime("parseBigExpr2", System.currentTimeMillis() - start);

  }

  @RepeatedTest(REPETITIONS)
  void parseNoKeywordExpr() throws IOException {
    String expr =
            "((new List<String>() + f2) * (f1 + f3) * (f1 + f4) / (f1 + f5) + (new List<String>() + f6) / (f1 + f7) + (f1 + f8) * (f1 + f9) + (f1 + f10) + (f1 + f11) / (f1 + f12) + (f1 + f13) + (f + f14) +\n" +
                    " (f1 + f3) * (f1 + f4) / (f1 + f5) + (f1 + new Set<String>()) / (f1 + f7) + (f1 + f8) * (f1 + f9) + (f1 + f10) + (f1 + f11) / (f1 + f12) + (f1 + f13) + (f + new List<String>()) +\n" +
                    "  (f2 + f3) * (f2 + f4) * (f2 + new List<String>()) * (f2 + f6) / (f2 + f7) / (f2 + f8) * (f2 + f9) + (f2 + f10) * (f2 + f11) * (f2 + f12) * (new List<String>() + f13) * (f2 + f14))";


    long start = System.currentTimeMillis();
    Optional<ASTExpression> ast = CombineExpressionsWithLiteralsMill.parser().parse_StringExpression(expr);
    assertTrue(ast.isPresent());
    addTime("parseNoKeywordExpr", System.currentTimeMillis() - start);
  }

  @RepeatedTest(REPETITIONS)
  void parseWithMode() throws IOException {
    long start = System.currentTimeMillis();
    CombineExpressionsWithLiteralsParser parser = CombineExpressionsWithLiteralsMill.parser();
    parser.setLexerMode("REGEX");
    parser.parse_StringCharRange("a-c").get();
    addTime("parseWithMode", System.currentTimeMillis() - start);
  }

  protected void addTime(String key, long time) {
    times.computeIfAbsent(key, s -> new ArrayList<>()).add(time);
  }

  @AfterAll
  public static void summarizeTimes() {
    StringBuilder csv = new StringBuilder();
    csv.append("test,run,time").append(System.lineSeparator());
    for (Map.Entry<String, List<Long>> test : times.entrySet()) {
      for (int run = 0; run < test.getValue().size(); run++) {
        csv.append(String.format("%s,%s,%s%n", test.getKey(), run, test.getValue().get(run)));
      }
      List<Long> timesAfterWarum;
      int warmupSize = REPETITIONS_WARM_UP;
      if (REPETITIONS_WARM_UP >= test.getValue().size()) {
        timesAfterWarum = test.getValue();
        warmupSize = 0;
      } else {
        timesAfterWarum = test.getValue().subList(REPETITIONS_WARM_UP, test.getValue().size());
      }
      Collections.sort(timesAfterWarum);

      int middle = timesAfterWarum.size() / 2;
      double medianValue = timesAfterWarum.size() % 2 == 1 ? timesAfterWarum.get(middle) : (timesAfterWarum.get(
              middle - 1) + timesAfterWarum.get(middle)) / 2d;

      double a = timesAfterWarum.stream().mapToLong(Long::longValue).average().orElseThrow();

      System.out.println(test.getKey() + ": median=" + medianValue + " avg=" + a + "(warmup=" + warmupSize + ")");
    }
    // You can also print the csv StringBuilder to a file
  }

}