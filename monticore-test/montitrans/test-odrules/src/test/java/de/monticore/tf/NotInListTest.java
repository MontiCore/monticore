/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class NotInListTest {
    
    @Before
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
    }
    
    ASTAutomaton aut;

    @Test
    public void testEmptyAutomat() throws IOException {
        String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());
        assertEquals(0, aut.get().getStateList().size());

        // execute tested code and store result
        NotInList rule = new NotInList(aut.get());

        // no match found
        assertFalse(rule.doPatternMatching());
        
        assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testStateWithoutSubstate() throws IOException {
        String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());
        assertEquals(1, aut.get().getStateList().size());

        // execute tested code and store result
        NotInList rule = new NotInList(aut.get());

        // definition of test input
        assertTrue(rule.doPatternMatching());

        assertEquals(1, rule.get_list_1().size());
    
        assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testPosNegNegPos() throws IOException {
        String inputFile = "src/main/models/automaton/AutomatonSubstateList.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());
        assertEquals(4, aut.get().getStateList().size());

        // execute tested code and store result
        NotInList rule = new NotInList(aut.get());

        // definition of test input
        assertTrue(rule.doPatternMatching());

        assertEquals(2, rule.get_list_1().size());
    
        assertTrue(Log.getFindings().isEmpty());
    }
}
