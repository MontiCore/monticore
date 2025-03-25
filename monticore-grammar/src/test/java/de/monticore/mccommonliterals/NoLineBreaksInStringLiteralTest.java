/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsCoCoChecker;
import de.monticore.literals.mccommonliterals.cocos.NoLineBreaksInStringLiteralCoCo;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static de.monticore.literals.mccommonliterals.cocos.NoLineBreaksInStringLiteralCoCo.ERROR_CODE;

public class NoLineBreaksInStringLiteralTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }

  private void checkStringLiteral(String s) throws IOException {
    // Parsing
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    Assertions.assertTrue(lit.isPresent());

    // check CoCo
    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new NoLineBreaksInStringLiteralCoCo());
    checker.checkAll(lit.get());
  }

  @Test
  public void testStringLiterals() {
    try {
      checkStringLiteral("\"okay\"");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFalseStringLiterals() {
    try {
      checkStringLiteral("\"okay\n or not\"");
      checkStringLiteral("\"okay\r or not\"");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertEquals(2, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(ERROR_CODE));
    Assertions.assertTrue(Log.getFindings().get(1).getMsg().startsWith(ERROR_CODE));
  }
}
