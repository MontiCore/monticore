package mc.feature.symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ISymbol;
import mc.feature.symboltable.notopscope._ast.ASTFoo;
import mc.feature.symboltable.notopscope._parser.NoTopScopeParser;
import mc.feature.symboltable.notopscope._symboltable.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class NoTopScopeTest {

  private NoTopScopeArtifactScope scope;

  @Before
  public void setUp() throws IOException {
    NoTopScopeParser scopeAttributesParser = new NoTopScopeParser();
    Optional<ASTFoo> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/NoTopScope.st");
    assertFalse(scopeAttributesParser.hasErrors());
    assertTrue(astSup.isPresent());

    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/feature/symboltable"));
    NoTopScopeLanguage lang = new NoTopScopeLanguage();
    NoTopScopeGlobalScope globalScope = new NoTopScopeGlobalScope(modelPath, lang);
    NoTopScopeSymbolTableCreatorDelegator symbolTableCreator = new NoTopScopeSymbolTableCreatorDelegator(globalScope);
    scope = symbolTableCreator.createFromAST(astSup.get());
  }

  /**
   * test the generation of the method getTopLevelSymbol() of an ArtifactScope
   * if the start production is only a symbol and not a scope spanning symbol
   * -> check if exactly one symbol is in the ArtifactScope
   * -> do NOT check for SubScopes since there exists none here
   */

  @Test
  public void testGetTopLevelSymbol(){
    Optional<ISymbol> topLevelSymbol = scope.getTopLevelSymbol();
    assertTrue(topLevelSymbol.isPresent());
    assertEquals("A", topLevelSymbol.get().getName());
  }
}
