/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.automaton.transformation.rule._cocos;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.tr.automatontr._ast.*;
import mc.testcases.automaton.tr.automatontr._cocos.AutomatonTRCoCoChecker;
import mc.testcases.automaton.tr.automatontr._cocos.NoOptOnRHSCoCo;
import mc.testcases.automaton.tr.automatontr._parser.AutomatonTRParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by DW
 */
public class NoOptOnRHSCoCoTest {
  
  @Before
  public void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    Log.getFindings().clear();
  }

  @Test
  public void testAutomatonNoOptOnRHS() throws IOException {
    // parse valid transformation
    String inputFile = "src/test/resources/OptNotOnRHS.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptOnRHSCoCo noOptOnRHSCoCo = new NoOptOnRHSCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptOnRHSCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());

    // should not result in any errors
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAutomatonHasOptOnRHS() throws IOException {
    // parse invalid transformation
    String inputFile = "src/test/resources/OptOnRHS.mtr";
    AutomatonTRParser parser = new AutomatonTRParser();
    Optional<ASTAutomatonTFRule> ast = parser.parse(inputFile);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // check with CoCo
    NoOptOnRHSCoCo noOptOnRHSCoCo = new NoOptOnRHSCoCo();
    AutomatonTRCoCoChecker cocoChecker = new AutomatonTRCoCoChecker();
    noOptOnRHSCoCo.addTo(cocoChecker);
    cocoChecker.checkAll(ast.get());

    // should result in four errors
    assertEquals(4, Log.getFindings().size());
  }
}
