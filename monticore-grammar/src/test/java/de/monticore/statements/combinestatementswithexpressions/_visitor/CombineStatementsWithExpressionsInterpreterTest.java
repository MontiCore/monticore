package de.monticore.statements.combinestatementswithexpressions._visitor;

import de.monticore.interpreter.MIValue;
import de.monticore.statements.AbstractStatementInterpreterTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class CombineStatementsWithExpressionsInterpreterTest
        extends AbstractStatementInterpreterTest {

  @Test
  public void testVarDeclarationStatement() {
    testValidModel("int i = 0;");
    assertValueEquals(createValue(0), loadVariable("i"));
    
    testValidModel("int j = 1, k = 2;");
    assertValueEquals(createValue(1), loadVariable("j"));
    assertValueEquals(createValue(2), loadVariable("k"));
  }
  
  @Test
  public void testJavaBlockStatement() {
    MIValue result = testValidModel(
        "{                  \n" +
        "  int i = 0;       \n" +
        "  int j = 1;       \n" +
        "  {                \n" +
        "    int k = 2 * j; \n" +
        "    k += 40;       \n" +
        "    j = k;         \n" +
        "  }                \n" +
        "  return [i, j];   \n" +
        "}                  "
    );
    assertValueEquals(createValue(List.of(0, 42)), result);
  }
  
  @Test
  public void testCommonWhileStatement() {
    MIValue result = testValidModel(
        "{                          \n" +
        "  int i = 255, count = 0;  \n" +
        "  while (i != 0) {         \n" +
        "    i /= 2;                \n" +
        "    count++;               \n" +
        "  }                        \n" +
        "  return count;            \n" +
        "}                          "
    );
    assertValueEquals(createValue(8), result);

    testInvalidModel("while (2);");
  }
  
  @Test
  public void testCommonDoWhileStatement() {
    MIValue result = testValidModel(
        "{                        \n" +
        "  int i = 0, count = 0;  \n" +
        "  do {                   \n" +
        "    i /= 2;              \n" +
        "    count++;             \n" +
        "  } while (false);       \n" +
        "  return count;          \n" +
        "}                        "
    );
    assertValueEquals(createValue(1), result);

    testInvalidModel("do; while (2);");
  }
  
  @Test
  public void testCommonForStatement() {
    MIValue result = testValidModel(
        "{                                \n" +
        "  int count = 0;                 \n" +
        "  for (int i = 0; i < 10; i++) { \n" +
        "    count++;                     \n" +
        "  }                              \n" +
        "  return count;                  \n" +
        "}                                "
    );
    assertValueEquals(createValue(10), result);

    testInvalidModel("for (;);");
  }
  
  @Test
  public void testForEachStatement() {
    MIValue result = testValidModel(
        "{                        \n" +
        "  int sum = 0;           \n" +
        "  for (int i : [1..4]) { \n" +
        "    sum += i;            \n" +
        "  }                      \n" +
        "  return sum;            \n" +
        "}"
    );
    assertValueEquals(createValue(10), result);

    testInvalidModel("for (int i : 1);");
  }
  
  @Test
  public void testContinueStatement() {
    MIValue result = testValidModel(
        "{                                \n" +
        "  int count = 0;                 \n" +
        "  for (int i = 0; i < 10; i++) { \n" +
        "    if (i % 2 != 0) {            \n" +
        "      continue;                  \n" +
        "    }                            \n" +
        "    count++;                     \n" +
        "  }                              \n" +
        "  return count;                  \n" +
        "}                                "
    );
    assertValueEquals(createValue(5), result);

  }
  
  @Test
  public void testBreakStatement() {
    MIValue result = testValidModel(
        "{                                \n" +
        "  int count = 0;                 \n" +
        "  for (int i = 0; i < 10; i++) { \n" +
        "    count++;                     \n" +
        "    if (i > 5) {                 \n" +
        "      break;                     \n" +
        "    }                            \n" +
        "  }                              \n" +
        "  return count;                  \n" +
        "}                                "
    );
    assertValueEquals(createValue(7), result);
  }
  
  @Test
  public void testLeibnizEfficiency() {
    long startTime = System.nanoTime();
    MIValue result = testValidModel(
        "{                                      \n" +
        "  double pi = 1;                       \n" +
        "  double x = 1;                        \n" +
        "  for (int i = 2; i < 100002; i++) { \n" +
        "    x = x * -1;                        \n" +
        "    pi = pi + x / (2 * i - 1);         \n" +
        "  }                                    \n" +
        "  pi = pi * 4;                         \n" +
        "  return pi;                           \n" +
        "}                                      "
    );
    System.out.println(result.asDouble());
    long endTime = System.nanoTime();
    System.out.println("Elapsed time: " + ((endTime - startTime) / 1000000) + " ms");
    /* Iterations | Elapsed Time
      ------------|--------------
         100      |     1266 ms
           1k     |     1300 ms
          10k     |     1371 ms
         100k     |     1792 ms
           1M     |     3581 ms
          10M     |    19059 ms
         100M     |   171227 ms
     */
  }

}
