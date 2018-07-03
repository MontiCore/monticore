/* (c) Monticore license: https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import automaton._ast.ASTAutomaton;
import automaton._ast.ASTState;
import automaton._cocos.AutomatonCoCoChecker;
import automaton._parser.AutomatonParser;
import automaton._symboltable.AutomatonLanguage;
import automaton._symboltable.AutomatonSymbolTableCreator;
import automaton._symboltable.StateSymbol;
import automaton.cocos.TransitionSourceExists;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;


public class TransitionSourceExistsTest {
    
  // setup the language infrastructure
  AutomatonLanguage lang = new AutomatonLanguage();
  AutomatonParser parser = new AutomatonParser() ;
  
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
    Scope modelTopScope = createSymbolTable(lang, ast);

    // can be used for resolving names in the model
    Optional<Symbol> aSymbol = modelTopScope.resolve("A", StateSymbol.KIND);
    assertTrue(aSymbol.isPresent());
    assertEquals("A", aSymbol.get().getName());
    ASTNode n = aSymbol.get().getAstNode().get();
    assertEquals("A", ((ASTState)n).getName());
  }


  // --------------------------------------------------------------------
  @Test
  public void testOnValidModel() throws IOException {
    ASTAutomaton ast = parser.parse_String(
      "automaton Simple { state A;  state B;  A -x> A;  B -y> A; }"
    ).get();
    
    // setup the symbol table
    Scope modelTopScope = createSymbolTable(lang, ast);

    // setup context condition infrastructure & check
    AutomatonCoCoChecker checker = new AutomatonCoCoChecker();
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
    Scope modelTopScope = createSymbolTable(lang, ast);

    // setup context condition infrastructure & check
    AutomatonCoCoChecker checker = new AutomatonCoCoChecker();
    checker.addCoCo(new TransitionSourceExists());

    checker.checkAll(ast);

    // we expect two errors in the findings
    assertEquals(1, Log.getFindings().size());
    assertEquals("0xAUT03 Source state of transition missing.",
       		Log.getFindings().get(0).getMsg());
  }


  /**
   * Create the symbol table from the parsed AST.
   *
   * @param lang
   * @param ast
   * @return
   */
  public static Scope createSymbolTable(AutomatonLanguage lang, ASTAutomaton ast) {
    ResolvingConfiguration rc = new ResolvingConfiguration();
    rc.addDefaultFilters(lang.getResolvingFilters());

    GlobalScope globalScope = new GlobalScope(new ModelPath(), lang, rc);

    Optional<AutomatonSymbolTableCreator> symbolTable = lang.getSymbolTableCreator(
        rc, globalScope);
    return symbolTable.get().createFromAST(ast);
  }

}
