package mc.feature.scopes;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.feature.scopes.supautomaton._ast.ASTSup;
import mc.feature.scopes.supautomaton._parser.SupAutomatonParser;
import mc.feature.scopes.supautomaton._symboltable.SupAutomatonLanguage;
import mc.feature.scopes.supautomaton._symboltable.SupAutomatonScope;
import mc.feature.scopes.supautomaton._symboltable.SupAutomatonSymbolTableCreator;
import mc.feature.scopes.superautomaton._symboltable.AutomatonSymbol;
import mc.feature.scopes.superautomaton._symboltable.StateSymbol;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScopesTest {

  private ASTSup astSup;
  private SupAutomatonSymbolTableCreator symbolTableCreator;
  private GlobalScope globalScope;


  @Before
  public void testResolvingFilter() throws IOException {

    SupAutomatonParser supAutomatonParser = new SupAutomatonParser();
    Optional<ASTSup> astSup = supAutomatonParser.parse("src/test/resources/mc/feature/scopes/SupAutomatonModel.aut");
    assertFalse(supAutomatonParser.hasErrors());
    assertTrue(astSup.isPresent());

    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/feature/scopes"));
    ModelingLanguage lang = new SupAutomatonLanguage();
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(lang.getResolvingFilters());
    GlobalScope globalScope = new GlobalScope(modelPath, lang, resolvingConfiguration);
    SupAutomatonSymbolTableCreator symbolTableCreator = new SupAutomatonSymbolTableCreator(resolvingConfiguration, globalScope);

    this.astSup = astSup.get();
    this.globalScope = globalScope;
    this.symbolTableCreator = symbolTableCreator;
  }

  @Test
  public void testResolvingFromGrammarScope(){

    SupAutomatonScope fromAST = (SupAutomatonScope) symbolTableCreator.createFromAST(astSup);

    //findet, denn liegt im ersten Scope
    Optional<AutomatonSymbol> pingPongAutomatonSymbolLokal = fromAST.resolveAutomaton("PingPong");
    //findet, denn sucht dann von oben aus
    Optional<AutomatonSymbol> pingPongAutomatonSymbolGlobal = fromAST.resolveAutomaton("TopPingPong.PingPong");
    //mit resolveDown kann der SupScope aus dem SupAutomatonScope heraus gefunden werden
    Optional<StateSymbol> pingStateSymbol = fromAST.resolveDown("PingPong.Ping", StateSymbol.KIND);
    //Muss hier mit resolve vollqualifiziert angegeben werden, denn sonst findet der SupAutomatonScope nichts in SupScopes
    //resolved hier also einmal nach ganz oben und von da aus wieder nach unten
    Optional<StateSymbol> pongStateSymbol = fromAST.resolveState("TopPingPong.PingPong.Pong");
    Optional<StateSymbol> noGameStateSymbol = fromAST.resolveState("TopPingPong.PingPong.NoGame");
    //findet also voll qualifiziert auch vom global aus
    Optional<StateSymbol> pingStateSymbolGlobal = globalScope.resolve("TopPingPong.PingPong.Ping", StateSymbol.KIND);

    assertTrue(pingPongAutomatonSymbolLokal.isPresent());
    assertTrue(pingPongAutomatonSymbolGlobal.isPresent());
    assertTrue(pingStateSymbol.isPresent());
    assertTrue(pongStateSymbol.isPresent());
    assertTrue(noGameStateSymbol.isPresent());
    assertTrue(pingStateSymbolGlobal.isPresent());

  }
}
