/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class OptInListTest {

    @BeforeClass
    public static void disableFailQuick() {
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
        OptInList rule = new OptInList(aut.get());

        // no match found
        assertFalse(rule.doPatternMatching());
    }

    @Test
    public void testStateWithoutSubstate() throws IOException {
        String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());
        assertEquals(1, aut.get().getStateList().size());

        // execute tested code and store result
        OptInList rule = new OptInList(aut.get());

        // definition of test input
        assertTrue(rule.doPatternMatching());

        assertEquals(1, rule.get_list_1().size());
    }

    @Test
    public void testPosNegNegPos() throws IOException {
        String inputFile = "src/main/models/automaton/AutomatonSubstateList.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());
        assertEquals(4, aut.get().getStateList().size());

        // execute tested code and store result
        OptInList rule = new OptInList(aut.get());

        // definition of test input
        assertTrue(rule.doPatternMatching());

        assertEquals(4, rule.get_list_1().size());
        //assertTrue(rule.get_list_1().get(1).state_2.isPresent());
    }
}
