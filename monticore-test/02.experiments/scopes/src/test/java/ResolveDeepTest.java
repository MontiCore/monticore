/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.AutomataSymbols2Json;
import automata._symboltable.IAutomataArtifactScope;
import automata._symboltable.IAutomataScope;
import automata._symboltable.StateSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;

public class ResolveDeepTest {

  protected static AutomataParser parser = new AutomataParser();

  @BeforeEach
  public void storeSymbols() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);

    //init global scopes with the model path and store symtab of test model
    String testmodel = "src/test/resources/example/HierarchyPingPong.aut";

    ASTAutomaton ast = parser.parse(testmodel).get();
    IAutomataArtifactScope as = AutomataMill.scopesGenitorDelegator().createFromAST(ast);
    AutomataSymbols2Json s2j = new AutomataSymbols2Json();
    s2j.store(as, "target/symbols/PingPong.autsym");
  }

  @Test
  public void testDeepResolve() throws IOException {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only

    AutomataMill.globalScope().clear();

    Path model = Paths.get("src/test/resources/example/HierarchyPingPong.aut");

    // parse model
    Optional<ASTAutomaton> ast = parser.parse(model.toString());
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());

    // build symbol table
    IAutomataArtifactScope scope = AutomataMill.scopesGenitorDelegator().createFromAST(ast.get());

    // test resolving
    // we only test the resolveDown methods as the other methods are not
    // modified (i.e., resolve and resolve locally methods are not affected)

    // test if default qualified resolveDown behavior is preserved
    Assertions.assertTrue(scope.resolveStateDown("PingPong.InGame.Ping").isPresent());
    Assertions.assertTrue(scope.resolveStateDown("PingPong.very.deep.substate").isPresent());

    // test deep resolving with unqualified symbol name
    Assertions.assertTrue(scope.resolveStateDown("PingPong.substate").isPresent());

    // test unqualified resolving with multiple occurrences: 2 Ping symbols
    Assertions.assertEquals(scope.resolveStateDownMany("PingPong.Ping").size(), 2, 0);

    // test negative case, where we try to resolve one Ping state
    boolean success = true;
    try {
      scope.resolveStateDown("PingPong.Ping");
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      success = false;
    }
    Assertions.assertFalse(success);

    // test "half"-qualified down resolving. We pass an incomplete qualification
    // for symbol Ping. Expected behavior: we handle the name as fully qualified
    // until there is only one part left and continue with deep resolving in all
    // substates. In this test case, we navigate to the scope spanning symbol
    // "very". From here, the symbol Ping lies several scopes beneath. However,
    // since Ping is uniquely accessible from this point, no error occurs and we
    // find exactly one symbol.
    Assertions.assertTrue(scope.resolveStateDown("PingPong.very.Ping").isPresent());

    // test down resolving with in-between steps
    Optional<StateSymbol> deep_sym = scope.resolveState("PingPong.deep");
    IAutomataScope deep_scope = deep_sym.get().getSpannedScope();
    Assertions.assertTrue(deep_scope.resolveStateDown("substate").isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  private void reInitGlobalScopes(MCPath mp1, MCPath mp2) {
    AutomataMill.globalScope().clear();
    AutomataMill.globalScope().setSymbolPath(mp1);
  }

  @Test
  public void testResolveFromGlobalScope() {
    // this test demonstrates that deeply nested symbols can now be found via
    // the default calculateModelNamesForState method

    MCPath mp1 = new MCPath(Paths.get("target/symbols"));
    MCPath mp2 = new MCPath(Paths.get("target/symbols2"));

    reInitGlobalScopes(mp1, mp2);

    // resolve for automaton symbol
    Assertions.assertTrue(AutomataMill.globalScope().resolveAutomaton("PingPong").isPresent());

    reInitGlobalScopes(mp1, mp2);

    // resolve for top level state symbol
    Assertions.assertTrue(AutomataMill.globalScope().resolveState("PingPong.very").isPresent());

    reInitGlobalScopes(mp1, mp2);

    // resolve for nested state symbol
    Assertions.assertTrue(AutomataMill.globalScope().resolveState("PingPong.very.deep").isPresent());

    reInitGlobalScopes(mp1, mp2);

    // resolve for deeply nested state symbol
    Assertions.assertTrue(AutomataMill.globalScope().resolveState("PingPong.very.deep.substate").isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
