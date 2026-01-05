/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._cocos.AutomataCoCoChecker;
import automata._symboltable.*;
import automata.cocos.TransitionSourceExists;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.MCPath;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.LogStub;
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
       "automaton Simple { state A;  state B;  A - x > A;  B - y > A; }"
    ).get();
    
    // setup the symbol table
    IAutomataArtifactScope modelTopScope = createSymbolTable(ast);
    modelTopScope.setName("Simple");

    // can be used for resolving names in the model
    Optional<StateSymbol> aSymbol = modelTopScope.resolveState("Simple.A");
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
      "automaton Simple { state A;  state B;  A -x> A;  B -y> A; }"
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
       "automaton Simple { " +
       "  state A;  state B; A - x > A;  Blubb - y > A; }"
    ).get();
    
    // setup the symbol table
    IAutomataArtifactScope modelTopScope = createSymbolTable(ast);

    // setup context condition infrastructure & check
    AutomataCoCoChecker checker = new AutomataCoCoChecker();
    checker.addCoCo(new TransitionSourceExists());

    checker.checkAll(ast);
  
    // we expect one error in the findings
    MCAssertions.assertHasFindingsStartingWith("0xADD31 Source state of transition missing.");
  }


  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast
   * @return
   */
  public static IAutomataArtifactScope createSymbolTable(ASTAutomaton ast) {
    IAutomataGlobalScope globalScope = AutomataMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath());
    globalScope.setFileExt("aut");

    AutomataScopesGenitorDelegator symbolTable = AutomataMill
        .scopesGenitorDelegator();


    return symbolTable.createFromAST(ast);
  }

}
