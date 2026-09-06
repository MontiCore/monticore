/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.EliminateDo;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.statechart.statechart._ast.*;
import mc.testcases.statechart.statechart._parser.StatechartParser;

import java.io.IOException;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Test02_EliminateDoTest {
    
    @BeforeEach
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
    }
    
    @Test
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
        assertNotNull(entryAction, "entry action has not been added");
        assertNotNull(entryAction.getBlock(), "entry action is empty");

        assertFalse(state.isPresentDoAction(), "do action has not been removed");

        ASTExitAction exitAction =  state.getExitAction();
        assertNotNull(exitAction, "exit action has not been added");
        assertNotNull(exitAction.getBlock(), "exit action is empty");

        ASTInternTransition internTransition = state.getInternTransition(0);
        assertNotNull(internTransition, "intern transition has not been created");
        ASTBlockStatement internAction = internTransition.getAction();
        assertNotNull(internAction, "intern transition has no action");
        assertEquals(2, internAction.getStatementList().size(), "incorrect number of statements in intern action");

        testee.undoReplacement();
        assertFalse(state.isPresentEntryAction());
        assertFalse(state.isPresentExitAction());
        assertTrue(state.isPresentDoAction());
        assertTrue(Log.getFindings().isEmpty());
    }

}
