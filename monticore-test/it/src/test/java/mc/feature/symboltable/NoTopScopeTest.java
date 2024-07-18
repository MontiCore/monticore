/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symboltable;

import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.symboltable.notopscope.NoTopScopeMill;
import mc.feature.symboltable.notopscope._ast.ASTFoo;
import mc.feature.symboltable.notopscope._parser.NoTopScopeParser;
import mc.feature.symboltable.notopscope._symboltable.FooSymbol;
import mc.feature.symboltable.notopscope._symboltable.INoTopScopeArtifactScope;
import mc.feature.symboltable.notopscope._symboltable.INoTopScopeGlobalScope;
import mc.feature.symboltable.subnotopscope.SubNoTopScopeMill;
import mc.feature.symboltable.subnotopscope._ast.ASTSubFoo;
import mc.feature.symboltable.subnotopscope._parser.SubNoTopScopeParser;
import mc.feature.symboltable.subnotopscope._symboltable.ISubNoTopScopeGlobalScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;

public class NoTopScopeTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * test the generation of the method getTopLevelSymbol() of an ArtifactScope
   * if the start production is only a symbol and not a scope spanning symbol
   * -> check if exactly one symbol is in the ArtifactScope
   * -> do NOT check for SubScopes since there exists none here
   */

  @Test
  public void testGetTopLevelSymbol() throws IOException {
    // parse model
    NoTopScopeParser scopeAttributesParser = new NoTopScopeParser();
    Optional<ASTFoo> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/NoTopScope.st");
    Assertions.assertFalse(scopeAttributesParser.hasErrors());
    Assertions.assertTrue(astSup.isPresent());

    // create symboltable
    INoTopScopeGlobalScope globalScope = NoTopScopeMill.globalScope();
    globalScope.setFileExt("st");
    globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));

    INoTopScopeArtifactScope scope = NoTopScopeMill
        .scopesGenitorDelegator().createFromAST(astSup.get());

    // only one symbol
    Optional<ISymbol> topLevelSymbol = scope.getTopLevelSymbol();
    Assertions.assertTrue(topLevelSymbol.isPresent());
    Assertions.assertEquals("A", topLevelSymbol.get().getName());

    // two symbols
    FooSymbol eSymbol = new FooSymbol("E");
    scope.add(eSymbol);
    topLevelSymbol = scope.getTopLevelSymbol();
    Assertions.assertFalse(topLevelSymbol.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetTopLevelSymbolWithInherited() throws IOException {
    // parse model
    SubNoTopScopeParser scopeAttributesParser = new SubNoTopScopeParser();
    Optional<ASTSubFoo> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/SubNoTopScope.st");
    Assertions.assertFalse(scopeAttributesParser.hasErrors());
    Assertions.assertTrue(astSup.isPresent());

    // create symboltable
    ISubNoTopScopeGlobalScope globalScope = SubNoTopScopeMill.globalScope();
    globalScope.setFileExt("st");
    globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));

    INoTopScopeArtifactScope scope = SubNoTopScopeMill
        .scopesGenitorDelegator().createFromAST(astSup.get());

    // only one symbol
    Optional<ISymbol> topLevelSymbol = scope.getTopLevelSymbol();
    Assertions.assertTrue(topLevelSymbol.isPresent());
    Assertions.assertEquals("A", topLevelSymbol.get().getName());

    // two symbols (add symbol from super grammar)
    FooSymbol eSymbol = NoTopScopeMill.fooSymbolBuilder().setName("E").build();
    scope.add(eSymbol);
    topLevelSymbol = scope.getTopLevelSymbol();
    Assertions.assertFalse(topLevelSymbol.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
