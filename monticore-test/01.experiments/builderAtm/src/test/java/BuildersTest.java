/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.*;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomataMill.class)
public class BuildersTest {


  // tests whether handcoded subclass of Builder is used
  // (which should be included upon generation)
  @Test
  public void testMyTransitionBuilder() throws IOException {
    ASTTransition transition = AutomataMill
        .transitionBuilder()
        .setFrom("setByGenBuilder")
        .setInput("xxxx")
        .setTo("setByGenBuilder").build();
    assertEquals("xxxxSuf2", transition.getInput());
    MCAssertions.assertNoFindings();
  }

  // tests whether handcoded subclass of Builder is used
  // even in sub-nonterminal builder:
  // And no it obviously isn't, because ActT-Builder inherits from
  // T-Builder. This is also why we cannot completely build a chain of
  // setters: the setters of the superclass loose typeinformation
  @Test
  public void testMyTransitionBuilderInSubNT() throws IOException {
    ASTActTransitionBuilder b = AutomataMill.actTransitionBuilder();
    b.setFrom("setByGenBuilder");
    b.setAction("Boom");
    b.setInput("xxxx");
    b.setTo("setByGenBuilder");
    ASTActTransition transition = b.build();
    assertEquals("xxxx", transition.getInput());
    assertEquals("Boom", transition.getAction());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testHWCClassGeneratedBuilder() throws IOException {
    ASTAutomaton aut = AutomataMill
        .automatonBuilder()
        .setName("setByGeneratedBuilder").build();
    assertEquals("setByGeneratedBuilder", aut.getName());
    MCAssertions.assertNoFindings();
  }

  // tests whether handcoded subclass of Builder is used
  // (which should be included upon generation)
  @Test
  public void testHWCClassHWCBuilder() throws IOException {
    ASTState state = AutomataMill
        .stateBuilder()
        .setName("x2")
        .setFinal(true)
        .setName("state1").build();
    assertEquals(state.getName(), "state1Suf1");
    MCAssertions.assertNoFindings();
  }

  // tests whether handcoded subclass of Builder is used
  // for the subclass-NT as well
  // Yes: here it works
  @Test
  public void testHWCClassHWCBuilderInSubNT() throws IOException {
    ASTActStateBuilder b = AutomataMill.actStateBuilder();
    b.setName("x2");
    b.setFinal(true);
    b.setEntry("Blubb");
    b.setName("state1");
    ASTActState state = b.build();
    assertTrue(state.isFinal());
    assertEquals(state.getName(), "state1Suf1");
    assertEquals(state.getEntry(), "Blubb");
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testGetFunctions() throws IOException {
     ASTStateBuilderTOP sb = AutomataMill
        .stateBuilder()
        .setName("x2")
        .setFinal(true)
        .setName("state1");
    assertTrue(sb.isFinal());
    assertEquals(sb.getName(), "state1Suf1");
    MCAssertions.assertNoFindings();
  }

}
