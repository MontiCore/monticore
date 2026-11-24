/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mclowlevelstatements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsMill;
import de.monticore.tests.expressionsandstatements.TestExpressionsAndStatementsTool;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._cocos.ExpressionsAndStatementsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(ExpressionsAndStatementsMill.class)
public class LabelIsUsedTest {
  TestExpressionsAndStatementsTool testTool;
  ExpressionsAndStatementsCoCoChecker checker;

  @BeforeEach
  public void init() {
    testTool = new TestExpressionsAndStatementsTool();
    checker = new ExpressionsAndStatementsCoCoChecker();
    checker.setTraverser(ExpressionsAndStatementsMill.inheritanceTraverser());
    checker.addCoCo(new LabelIsUsed());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "label: for (int i = 0; i < 5; i++) { continue label; }",
      "label: { label2: { break label2; } break label; }",
      "label: { break label; } label2: { break label2; }",
  })
  public void testValid(String labelBlock) throws IOException {
    ASTBehaviorInput behaviorInput = testTool.parse(labelBlock);
    testTool.createSymbolTable(behaviorInput);

    checker.checkAll(behaviorInput);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "label: for (int i = 0; i < 5; i++) { continue; }",
      "label: { label2: { break; } break label; }",
      "label: { } break label;",
      "label1: label2: { break label1; }",
  })
  public void testInvalid(String labelBlock) throws IOException {
    ASTBehaviorInput behaviorInput = testTool.parse(labelBlock);
    testTool.createSymbolTable(behaviorInput);

    checker.checkAll(behaviorInput);
    MCAssertions.assertHasFindingStartingWith("0xE0001");
  }

}
