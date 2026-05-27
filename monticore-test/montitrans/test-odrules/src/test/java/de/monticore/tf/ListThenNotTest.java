/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListThenNotTest {
    
    @BeforeEach
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
    }
    
    @Test
    public void testEmptyAutomat() throws IOException {
        String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());

        // execute tested code and store result
        ListThenNot rule = new ListThenNot(aut.get());

        // definition of test input
        assertFalse(rule.doPatternMatching());
    
        assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testNoNotInitialState() throws IOException {
        String inputFile = "src/main/models/automaton/AutomatonWithInitialState.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());

        ListThenNot rule = new ListThenNot(aut.get());

        assertTrue(rule.doPatternMatching());
    
        assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testNegativ() throws IOException {
        String inputFile = "src/main/models/automaton/AutomatonWithTwoMatches.aut";
        AutomatonParser parser = new AutomatonParser();
        Optional<ASTAutomaton> aut = parser.parse(inputFile);

        assertTrue(aut.isPresent());

        ListThenNot rule = new ListThenNot(aut.get());

        assertFalse(rule.doPatternMatching());
    
        assertTrue(Log.getFindings().isEmpty());
    }
}
