/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class DoBlockTest {

  @Test
  public void testDoAll() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTState> sOpt = parser.parseState("src/main/models/automaton/initialState.aut");
    assertTrue(sOpt.isPresent());
    
    ASTState s = sOpt.get();

    assertTrue(s.isInitial());

    DoBlock sitf = new DoBlock(s);

    assertTrue(s.isInitial());

    sitf.doAll();

    assertFalse(s.isInitial());
  }

}
