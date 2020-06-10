/* (c) https://github.com/MontiCore/monticore */

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata.cocos.TransitionSourceExists;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransitionSourceExistsTest {
  
  // setup the language infrastructure
  AutomataLanguage lang = new AutomataLanguage();
  AutomataParser parser = new AutomataParser() ;
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
  }
  

  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
  }
  

  // --------------------------------------------------------------------
  @Test
  public void testBasics() throws IOException {
    ASTAutomaton ast = parser.parse_String(
       "automaton Simple { state A;  state B;  A - x > A;  A - y > A; }"
    ).get();
    assertEquals("Simple", ast.getName());
    List<ASTState> st = ast.getStateList();
    assertEquals(2, st.size());
  }


  // --------------------------------------------------------------------
  @Test
  public void testRetrievalOfSymbol() throws IOException {
    ASTAutomaton ast = parser.parse_String(
       "automaton Simple { state A;  state B;  A - x > A;  B - y > A; }"
    ).get();
    
    // setup the symbol table
    AutomataArtifactScope modelTopScope = createSymbolTable(lang, ast);

    // can be used for resolving names in the model
    Optional<StateSymbol> aSymbol = modelTopScope.resolveState("Simple.A");
    assertTrue(aSymbol.isPresent());
    assertEquals("A", aSymbol.get().getName());
    ASTNode n = aSymbol.get().getAstNode();
    assertEquals("A", ((ASTState)n).getName());
  }


  // --------------------------------------------------------------------
  @Test
  public void testOnValidModel() throws IOException {
    ASTAutomaton ast = parser.parse_String(
      "automaton Simple { state A;  state B;  A -x> A;  B -y> A; }"
    ).get();
    
    // setup the symbol table
    AutomataArtifactScope modelTopScope = createSymbolTable(lang, ast);

    // setup context condition infrastructure & check
    AutomataCoCoChecker checker = new AutomataCoCoChecker();
    checker.addCoCo(new TransitionSourceExists());

    checker.checkAll(ast);

    assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testOnInvalidModel() throws IOException {
    ASTAutomaton ast = parser.parse_String(
       "automaton Simple { " +
       "  state A;  state B; A - x > A;  Blubb - y > A; }"
    ).get();
    
    // setup the symbol table
    AutomataArtifactScope modelTopScope = createSymbolTable(lang, ast);

    // setup context condition infrastructure & check
    AutomataCoCoChecker checker = new AutomataCoCoChecker();
    checker.addCoCo(new TransitionSourceExists());

    checker.checkAll(ast);
  
    // we expect one error in the findings
    assertEquals(1, Log.getFindings().size());
    assertEquals("0xADD03 Source state of transition missing.",
       		Log.getFindings().get(0).getMsg());
  }


  /**
   * Create the symbol table from the parsed AST.
   *
   * @param lang
   * @param ast
   * @return
   */
  public static AutomataArtifactScope createSymbolTable(AutomataLanguage lang, ASTAutomaton ast) {

    AutomataGlobalScope globalScope = new AutomataGlobalScope(new ModelPath(), lang);

    AutomataSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }

}
