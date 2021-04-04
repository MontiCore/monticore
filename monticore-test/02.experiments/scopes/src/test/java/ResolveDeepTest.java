/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.AutomataSymbols2Json;
import automata._symboltable.IAutomataArtifactScope;
import automata._symboltable.IAutomataScope;
import automata._symboltable.StateSymbol;
import automata2.Automata2Mill;
import automata2._parser.Automata2Parser;
import automata2._symboltable.Automata2Symbols2Json;
import automata2._symboltable.IAutomata2ArtifactScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class ResolveDeepTest {

  protected static AutomataParser parser = new AutomataParser();

  protected static Automata2Parser parser2 = new Automata2Parser();

  @Test
  public void testDeepResolve() throws IOException {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only

    AutomataMill.globalScope().clear();

    Path model = Paths.get("src/test/resources/example/HierarchyPingPong.aut");

    // parse model
    Optional<ASTAutomaton> ast = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());

    // build symbol table
    IAutomataArtifactScope scope = AutomataMill.scopesGenitorDelegator().createFromAST(ast.get());

    // test resolving
    // we only test the resolveDown methods as the other methods are not
    // modified (i.e., resolve and resolve locally methods are not affected)

    // test if default qualified resolveDown behavior is preserved
    assertTrue(scope.resolveStateDown("PingPong.InGame.Ping").isPresent());
    assertTrue(scope.resolveStateDown("PingPong.very.deep.substate").isPresent());

    // test deep resolving with unqualified symbol name
    assertTrue(scope.resolveStateDown("PingPong.substate").isPresent());

    // test unqualified resolving with multiple occurrences: 2 Ping symbols
    assertEquals(scope.resolveStateDownMany("PingPong.Ping").size(), 2, 0);

    // test negative case, where we try to resolve one Ping state
    boolean success = true;
    try {
      scope.resolveStateDown("PingPong.Ping");
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      success = false;
    }
    assertFalse(success);

    // test "half"-qualified down resolving. We pass an incomplete qualification
    // for symbol Ping. Expected behavior: we handle the name as fully qualified
    // until there is only one part left and continue with deep resolving in all
    // substates. In this test case, we navigate to the scope spanning symbol
    // "very". From here, the symbol Ping lies several scopes beneath. However,
    // since Ping is uniquely accessible from this point, no error occurs and we
    // find exactly one symbol.
    assertTrue(scope.resolveStateDown("PingPong.very.Ping").isPresent());

    // test down resolving with in-between steps
    Optional<StateSymbol> deep_sym = scope.resolveState("PingPong.deep");
    IAutomataScope deep_scope = deep_sym.get().getSpannedScope();
    assertTrue(deep_scope.resolveStateDown("substate").isPresent());
  }

  private void reInitGlobalScopes(ModelPath mp1, ModelPath mp2) {
    AutomataMill.globalScope().clear();
    Automata2Mill.globalScope().clear();
    AutomataMill.globalScope().setModelPath(mp1);
    Automata2Mill.globalScope().setModelPath(mp2);
  }

  @BeforeClass
  public static void storeSymbols() throws IOException {
    //init global scopes with the model path and store symtab of test model
    String testmodel = "src/test/resources/example/HierarchyPingPong.aut";

    ASTAutomaton ast1 = parser.parse(testmodel).get();
    IAutomataArtifactScope as1 = AutomataMill.scopesGenitorDelegator().createFromAST(ast1);
    AutomataSymbols2Json s2j = new AutomataSymbols2Json();
    s2j.store(as1, "target/symbols/PingPong.autsym");

    automata2._ast.ASTAutomaton ast2 = parser2.parse(testmodel).get();
    IAutomata2ArtifactScope as2 = Automata2Mill.scopesGenitorDelegator().createFromAST(ast2);
    Automata2Symbols2Json s2j2 = new Automata2Symbols2Json();
    s2j2.store(as2, "target/symbols2/PingPong.autsym");

  }

  @Test
  public void testResolveFromGlobalScope() {
    // this test shows that deeply nested symbols cannot be found by the default
    // calculateModelNamesForState method (case: Automata language), but with a
    // handritten adjustment (case Automata2 language)



    ModelPath mp1 = new ModelPath(Paths.get("target/symbols"));
    ModelPath mp2 = new ModelPath(Paths.get("target/symbols2"));

    reInitGlobalScopes(mp1, mp2);

    // resolve for automaton symbol
    assertTrue(AutomataMill.globalScope().resolveAutomaton("PingPong").isPresent());
    assertTrue(Automata2Mill.globalScope().resolveAutomaton("PingPong").isPresent());

    reInitGlobalScopes(mp1, mp2);

    // resolve for top level state symbol
    assertTrue(AutomataMill.globalScope().resolveState("PingPong.very").isPresent());
    assertTrue(Automata2Mill.globalScope().resolveState("PingPong.very").isPresent());

    reInitGlobalScopes(mp1, mp2);

    // resolve for nested state symbol
    assertTrue(!AutomataMill.globalScope().resolveState("PingPong.very.deep").isPresent());
    assertTrue(Automata2Mill.globalScope().resolveState("PingPong.very.deep").isPresent());

    reInitGlobalScopes(mp1, mp2);

    // resolve for deeply nested state symbol
    assertTrue(!AutomataMill.globalScope().resolveState("PingPong.very.deep.substate").isPresent());
    assertTrue(Automata2Mill.globalScope().resolveState("PingPong.very.deep.substate").isPresent());
  }

}
