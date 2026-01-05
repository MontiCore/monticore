/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata.cocos.TransitionSourceExists;
import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomataMill.class)
public class TransitionSourceExistsTest {
  
  // --------------------------------------------------------------------
  @Test
  public void testBasics() throws IOException {
    ASTAutomaton ast = AutomataMill.parser().parse_String(
       "automaton Simple { state A;  state B;  A - x > A;  A - y > A; }"
    ).get();
    assertEquals("Simple", ast.getName());
    List<ASTState> st = (List<ASTState>) ast.getStateList();
    assertEquals(2, st.size());
    MCAssertions.assertNoFindings();
  }


  // --------------------------------------------------------------------
  @Test
  public void testRetrievalOfSymbol() throws IOException {
    ASTAutomaton ast = AutomataMill.parser().parse_String(
       "automaton Simple1 { state A;  state B;  A - x > A;  B - y > A; }"
    ).get();
    
    // setup the symbol table
    IAutomataArtifactScope modelTopScope = createSymbolTable(ast);
    modelTopScope.setName("Simple1");

    // can be used for resolving names in the model
    Optional<StateSymbol> aSymbol = modelTopScope.resolveState("Simple1.A");
    assertTrue(aSymbol.isPresent());
    assertEquals("A", aSymbol.get().getName());
    ASTNode n = aSymbol.get().getAstNode();
    assertEquals("A", ((ASTState)n).getName());
    MCAssertions.assertNoFindings();
  }


  // --------------------------------------------------------------------
  @Test
  public void testOnValidModel() throws IOException {
    ASTAutomaton ast = AutomataMill.parser().parse_String(
      "automaton Simple2 { state A;  state B;  A -x> A;  B -y> A; }"
    ).get();
    
    // setup the symbol table
    IAutomataArtifactScope modelTopScope = createSymbolTable(ast);

    // setup context condition infrastructure & check
    AutomataCoCoChecker checker = new AutomataCoCoChecker();
    checker.addCoCo(new TransitionSourceExists());

    checker.checkAll(ast);

    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testOnInvalidModel() throws IOException {
    ASTAutomaton ast = AutomataMill.parser().parse_String(
       "automaton Simple3 { " +
       "  state A;  state B; A - x > A;  Blubb - y > A; }"
    ).get();
    
    // setup the symbol table
    IAutomataArtifactScope modelTopScope = createSymbolTable(ast);

    // setup context condition infrastructure & check
    AutomataCoCoChecker checker = new AutomataCoCoChecker();
    checker.addCoCo(new TransitionSourceExists());

    checker.checkAll(ast);
  
    // we expect one error in the findings
    MCAssertions.assertHasFindingStartingWith("0xADD03 Source state of transition missing.");
  }


  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast
   * @return
   */
  public static IAutomataArtifactScope createSymbolTable(ASTAutomaton ast) {
    AutomataMill.globalScope().setFileExt("aut");
    return AutomataMill.scopesGenitorDelegator().createFromAST(ast);
  }

}
