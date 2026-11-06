/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UndoBlockTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testUndoExecutesUndoBlock() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    ASTState s = parser.parseState("src/main/models/automaton/initialState.aut").orElseThrow();

    UndoBlock rule = new UndoBlock(s);
    rule.doAll();
    assertFalse(s.isInitial());

    // the undo block (and not the do block) is executed
    rule.undoReplacement();
    assertTrue(s.isInitial());

    assertTrue(Log.getFindings().isEmpty());
  }

}
