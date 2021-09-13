/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.EliminateDo;
import junit.framework.TestCase;
import mc.testcases.statechart.statechart._ast.*;
import mc.testcases.statechart.statechart._parser.StatechartParser;

import java.io.IOException;

public class Test02_EliminateDoTest extends TestCase {

    public void testDoAll() throws IOException {
        StatechartParser p = new StatechartParser();

        ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withDo.sc").get();

        if (p.hasErrors()) {
            throw new RuntimeException("input file for test is corrupt");
        }

        EliminateDo testee = new EliminateDo(sc);
        testee.doAll();

        ASTState state = sc.getState(0);
        assertNotNull(state);

        ASTEntryAction entryAction =  state.getEntryAction();
        assertNotNull("entry action has not been added", entryAction);
        assertNotNull("entry action is empty", entryAction.getBlock());

        assertFalse("do action has not been removed", state.isPresentDoAction());

        ASTExitAction exitAction =  state.getExitAction();
        assertNotNull("exit action has not been added", exitAction);
        assertNotNull("exit action is empty", exitAction.getBlock());

        ASTInternTransition internTransition = state.getInternTransition(0);
        assertNotNull("intern transition has not been created", internTransition);
        ASTBlockStatement internAction = internTransition.getAction();
        assertNotNull("intern transition has no action", internAction);
        assertEquals("incorrect number of statements in intern action", 2, internAction.getStatementList().size());

        testee.undoReplacement();
        assertFalse(state.isPresentEntryAction());
        assertFalse(state.isPresentExitAction());
        assertTrue(state.isPresentDoAction());
    }

}
