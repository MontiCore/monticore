/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.EliminateDo;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.*;
import mc.testcases.statechart.statechart._parser.StatechartParser;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(StatechartMill.class)
public class Test02_EliminateDoTest {
    
    @Test
    public void testDoAll() throws IOException {
        StatechartParser p = StatechartMill.parser();

        Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withDo.sc");

        assertTrue(scOpt.isPresent());
        assertFalse(p.hasErrors());
        
        ASTStatechart sc = scOpt.get();

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
