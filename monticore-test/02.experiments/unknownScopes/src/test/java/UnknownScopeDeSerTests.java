/* (c) https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import bar.BarMill;
import bar._symboltable.BarArtifactScope;
import bar._symboltable.IBarGlobalScope;
import bar._symboltable.IBarScope;
import de.monticore.symboltable.SymbolWithScopeOfUnknownKindDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.LogStub;
import foo.FooMill;
import foo._ast.ASTFooArtifact;
import foo._parser.FooParser;
import foo._symboltable.FooSymbols2Json;
import foo._symboltable.IFooArtifactScope;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class UnknownScopeDeSerTests {

  @BeforeClass
  public static void setup() {
    LogStub.init();
  }

  @Test
  public void test() throws IOException {
    FooParser fooParser = FooMill.parser();
    Optional<ASTFooArtifact> parsedFooArtifact = fooParser.parse_String(
        "nest TestNest {\n" +
        "  fun testFunction\n" +
        "  fun testFunction2\n" +
        "}\n"
    );

    assertTrue(parsedFooArtifact.isPresent());

    ASTFooArtifact fooArtifact = parsedFooArtifact.get();

    IFooArtifactScope fooArtifactScope = FooMill.scopesGenitorDelegator().createFromAST(fooArtifact);
    FooSymbols2Json fooSymbols2Json = new FooSymbols2Json();

    String serializedFoo = fooSymbols2Json.serialize(fooArtifactScope);
    JsonObject jsonFoo = JsonParser.parseJsonObject(serializedFoo);

    IBarGlobalScope barGlobalScope = BarMill.globalScope();
    barGlobalScope.putSymbolDeSer("foo._symboltable.NestSymbol", new SymbolWithScopeOfUnknownKindDeSer(barGlobalScope.getDeSer(), BarMill::scope));
    barGlobalScope.putSymbolDeSer("foo._symboltable.FunctionDeclarationSymbol", new SymbolWithScopeOfUnknownKindDeSer(barGlobalScope.getDeSer(), BarMill::scope));

    BarArtifactScope scope = (BarArtifactScope) barGlobalScope.getDeSer().deserializeArtifactScope(jsonFoo);

    assertTrue(scope.getUnknownSymbols().containsKey("TestNest"));
    assertEquals(1, scope.getUnknownSymbols().get("TestNest").size());

    IBarScope unknownNestScope = (IBarScope) scope.getUnknownSymbols().get("TestNest").get(0).getSpannedScope();
    assertEquals(2, unknownNestScope.getSubScopes().size());

    assertTrue(unknownNestScope.getSubScopes().stream().anyMatch(it -> Objects.equals(it.getName(), "testFunction")));
    assertTrue(unknownNestScope.getSubScopes().stream().anyMatch(it -> Objects.equals(it.getName(), "testFunction2")));
  }

}
