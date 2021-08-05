/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoBlockTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDoAll() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    ASTState s = parser.parseState("src/main/models/automaton/initialState.aut").get();

    assertTrue(s.isInitial());

    DoBlock sitf = new DoBlock(s);

    assertTrue(s.isInitial());

    sitf.doAll();

    assertFalse(s.isInitial());
  }

}
