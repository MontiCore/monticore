/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.InsertStateFirst;
import de.monticore.tf.InsertStateInplace;
import de.monticore.tf.InsertStateLast;
import de.monticore.tf.InsertStateRelative;
import junit.framework.TestCase;
import mc.testcases.statechart.statechart._ast.*;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.Test;

import java.io.IOException;

public class Test08_InsertStatesTest extends TestCase {

  @Test
  public void testInsertStateFirst() throws IOException {
    StatechartParser p = new StatechartParser();
    ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withSubstates.sc").get();

    assertNotNull(sc);
    assertFalse(p.hasErrors());

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
    StatechartParser p = new StatechartParser();
    ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withSubstates.sc").get();

    assertNotNull(sc);
    assertFalse(p.hasErrors());

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
    StatechartParser p = new StatechartParser();
    ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withSubstates.sc").get();

    assertNotNull(sc);
    assertFalse(p.hasErrors());

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
    StatechartParser p = new StatechartParser();
    ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withSubstates.sc").get();

    assertNotNull(sc);
    assertFalse(p.hasErrors());

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
