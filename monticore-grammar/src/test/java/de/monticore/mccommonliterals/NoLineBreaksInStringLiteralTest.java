/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsCoCoChecker;
import de.monticore.literals.mccommonliterals.cocos.NoLineBreaksInStringLiteralCoCo;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.literals.mccommonliterals.cocos.NoLineBreaksInStringLiteralCoCo.ERROR_CODE;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class NoLineBreaksInStringLiteralTest {

  private void checkStringLiteral(String s) throws IOException {
    // Parsing
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTLiteral> lit = parser.parse_StringLiteral(s);
    assertTrue(lit.isPresent());

    // check CoCo
    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new NoLineBreaksInStringLiteralCoCo());
    checker.checkAll(lit.get());
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "\"okay\"" })
  public void testStringLiterals(String val) throws IOException {
    checkStringLiteral(val);
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "\"okay\n or not\"", "\"okay\r or not\"" })
  public void testFalseStringLiterals(String val) throws IOException {
    checkStringLiteral(val);
    
    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith(ERROR_CODE));
  }
}
