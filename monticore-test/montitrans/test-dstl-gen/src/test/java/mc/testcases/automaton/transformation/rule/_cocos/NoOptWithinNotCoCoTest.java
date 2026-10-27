/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton.tr.automatontr.AutomatonTRMill;
import mc.testcases.automaton.tr.automatontr._ast.ASTAutomatonTFRule;
import mc.testcases.automaton.tr.automatontr._cocos.AutomatonTRCoCoChecker;
import mc.testcases.automaton.tr.automatontr._cocos.NoOptWithinNotCoCo;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonTRMill.class)
public class NoOptWithinNotCoCoTest {

  @Test
  public void testAutomatonNoOptWithinNot() throws IOException {
    // parse valid transformation
    String inputFile = "src/test/resources/NotWithoutOpt.mtr";
    AutomatonTRParser parser = AutomatonTRMill.parser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptWithinNotCoCo noOptWithinNotCoCo = new NoOptWithinNotCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptWithinNotCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());
  }

  @Test
  public void testAutomatonHasOptWithinNot() throws IOException {
    // parse invalid transformation
    String inputFile = "src/test/resources/NotWithOpt.mtr";
    AutomatonTRParser parser = AutomatonTRMill.parser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptWithinNotCoCo noOptWithinNotCoCo = new NoOptWithinNotCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptWithinNotCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());

    // should result in two errors
    Log.getFindings().remove(MCAssertions.assertHasFindingStartingWith("0xF0C21x38778"));
    Log.getFindings().remove(MCAssertions.assertHasFindingStartingWith("0xF0C21x56704"));
  }
}
