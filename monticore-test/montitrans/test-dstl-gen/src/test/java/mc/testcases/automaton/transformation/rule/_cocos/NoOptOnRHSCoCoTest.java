/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._ast.*;
import mc.testcases.automaton.tr.automatontr._cocos.AutomatonTRCoCoChecker;
import mc.testcases.automaton.tr.automatontr._cocos.NoOptOnRHSCoCo;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonTRMill.class)
public class NoOptOnRHSCoCoTest {

  @Test
  public void testAutomatonNoOptOnRHS() throws IOException {
    // parse valid transformation
    String inputFile = "src/test/resources/OptNotOnRHS.mtr";
    AutomatonTRParser parser = AutomatonTRMill.parser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptOnRHSCoCo noOptOnRHSCoCo = new NoOptOnRHSCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptOnRHSCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());
  }

  @Test
  public void testAutomatonHasOptOnRHS() throws IOException {
    // parse invalid transformation
    String inputFile = "src/test/resources/OptOnRHS.mtr";
    AutomatonTRParser parser = AutomatonTRMill.parser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptOnRHSCoCo noOptOnRHSCoCo = new NoOptOnRHSCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptOnRHSCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());

    MCAssertions.assertHasFindingsStartingWith("0xF0C20x08188");
    MCAssertions.assertHasFindingsStartingWith("0xF0C20x37578");
  }
}
