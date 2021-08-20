/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._cocos;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import mc.testcases.automaton.tr.automatontr._cocos.AutomatonTRCoCoChecker;
import mc.testcases.automaton.tr.automatontr._cocos.NoOptWithinNotCoCo;
import mc.testcases.automaton.tr.automatontr._ast.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by DW
 */
public class NoOptWithinNotCoCoTest {
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    Log.getFindings().clear();
  }

  @Test
  public void testAutomatonNoOptWithinNot() throws IOException {
    // parse valid transformation
    String inputFile = "src/test/resources/NotWithoutOpt.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptWithinNotCoCo noOptWithinNotCoCo = new NoOptWithinNotCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptWithinNotCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());

    // should not result in any errors
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAutomatonHasOptWithinNot() throws IOException {
    // parse invalid transformation
    String inputFile = "src/test/resources/NotWithOpt.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptWithinNotCoCo noOptWithinNotCoCo = new NoOptWithinNotCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptWithinNotCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());

    // should result in two errors
    assertEquals(2, Log.getFindings().size());
  }
}
