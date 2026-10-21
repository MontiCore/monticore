/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.InsertStateFirst;
import de.monticore.tf.InsertStateInplace;
import de.monticore.tf.InsertStateLast;
import de.monticore.tf.InsertStateRelative;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.*;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(StatechartMill.class)
public class Test08_InsertStatesTest {

  @Test
  public void testInsertStateFirst() throws IOException {
    StatechartParser p = StatechartMill.parser();
    Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withSubstates.sc");
    
    assertTrue(scOpt.isPresent());
    assertFalse(p.hasErrors());
    
    ASTStatechart sc = scOpt.get();

    InsertStateFirst testee = new InsertStateFirst(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertEquals("Top", topState.getName());

    assertEquals("New2", topState.getState(0).getName());
    assertEquals("New", topState.getState(1).getName());
    assertEquals("One", topState.getState(2).getName());
    assertEquals("Two", topState.getState(3).getName());
    assertEquals("Three", topState.getState(4).getName());

    testee.undoReplacement();

    assertEquals("One", topState.getState(0).getName());
    assertEquals("Two", topState.getState(1).getName());
    assertEquals("Three", topState.getState(2).getName());
  }

  @Test
  public void testInsertStateRelative() throws IOException {
    StatechartParser p = StatechartMill.parser();
    Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withSubstates.sc");

    assertFalse(p.hasErrors());
    assertTrue(scOpt.isPresent());
    
    ASTStatechart sc = scOpt.get();

    InsertStateRelative testee = new InsertStateRelative(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertEquals("Top", topState.getName());

    assertEquals("One", topState.getState(0).getName());
    assertEquals("Two", topState.getState(1).getName());
    assertEquals("New", topState.getState(2).getName());
    assertEquals("New2", topState.getState(3).getName());
    assertEquals("Three", topState.getState(4).getName());

    testee.undoReplacement();

    assertEquals("One", topState.getState(0).getName());
    assertEquals("Two", topState.getState(1).getName());
    assertEquals("Three", topState.getState(2).getName());
  }

  @Test
  public void testInsertStateLast() throws IOException {
    StatechartParser p = StatechartMill.parser();
    Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withSubstates.sc");
    
    assertFalse(p.hasErrors());
    assertTrue(scOpt.isPresent());
    
    ASTStatechart sc = scOpt.get();

    InsertStateLast testee = new InsertStateLast(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertEquals("Top", topState.getName());

    assertEquals("One", topState.getState(0).getName());
    assertEquals("Two", topState.getState(1).getName());
    assertEquals("Three", topState.getState(2).getName());
    assertEquals("New", topState.getState(3).getName());
    assertEquals("New2", topState.getState(4).getName());

    testee.undoReplacement();

    assertEquals("One", topState.getState(0).getName());
    assertEquals("Two", topState.getState(1).getName());
    assertEquals("Three", topState.getState(2).getName());
  }

  @Test
  public void testInsertStateInplace() throws IOException {
    StatechartParser p = StatechartMill.parser();
    Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withSubstates.sc");
    
    assertFalse(p.hasErrors());
    assertTrue(scOpt.isPresent());
    
    ASTStatechart sc = scOpt.get();

    InsertStateInplace testee = new InsertStateInplace(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertEquals("Top", topState.getName());

    assertEquals("One", topState.getState(0).getName());
    assertEquals("New", topState.getState(1).getName());
    assertEquals("Three", topState.getState(2).getName());


    testee.undoReplacement();

    assertEquals("One", topState.getState(0).getName());
    assertEquals("Two", topState.getState(1).getName());
    assertEquals("Three", topState.getState(2).getName());
  }

}
