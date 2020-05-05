/* (c) https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.AutomataArtifactScope;
import automata._symboltable.AutomataGlobalScope;
import automata._symboltable.AutomataLanguage;
import automata._symboltable.AutomataSymTabMill;
import automata._symboltable.AutomataSymbolTableCreatorDelegator;
import automata._symboltable.IAutomataScope;
import automata._symboltable.StateSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.se_rwth.commons.logging.Log;

public class ResolveDeepTest {
  
  @Test
  public void testDeepResolve() throws IOException {
    Log.init();
    Path model = Paths.get("src/test/resources/example/HierarchyPingPong.aut");
    AutomataParser parser = new AutomataParser();
    
    // parse model
    Optional<ASTAutomaton> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // build symbol table
    final AutomataLanguage lang = AutomataSymTabMill.automataLanguageBuilder().build();
    AutomataArtifactScope scope = createSymbolTable(lang, jsonDoc.get());
    
    // test resolving
    // we only test the resolveDown methods as the other methods are not
    // modified (i.e., resolve and resolve locally methods are not affected)
    
    // test if default qualified resolveDown behavior is preserved
    assertTrue(scope.resolveStateDown("PingPong.InGame.Ping").isPresent());
    assertTrue(scope.resolveStateDown("PingPong.very.deep.substate").isPresent());
    
    // test deep resolving with unqualified symbol name
    assertTrue(scope.resolveStateDown("substate").isPresent());
    
    // test unqualified resolving with multiple occurrences: 2 Ping symbols
    assertEquals(scope.resolveStateDownMany("Ping").size(), 2, 0);
    
    // test negative case, where we try to resolve one Ping state
    boolean success = true;
    try {
      scope.resolveStateDown("Ping");
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
  
  /**
   * Creates the symbol table from the parsed AST.
   *
   * @param lang The automata language.
   * @param ast The top AST node.
   * @return The artifact scope derived from the parsed AST
   */
  public static AutomataArtifactScope createSymbolTable(AutomataLanguage lang, ASTAutomaton ast) {
    
    AutomataGlobalScope globalScope = AutomataSymTabMill.automataGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setAutomataLanguage(lang)
        .build();
    AutomataSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }
  
}
