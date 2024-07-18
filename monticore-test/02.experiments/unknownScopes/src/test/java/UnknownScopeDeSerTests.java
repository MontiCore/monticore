/* (c) https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import bar.BarMill;
import bar._symboltable.BarArtifactScope;
import bar._symboltable.IBarGlobalScope;
import bar._symboltable.IBarScope;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.LogStub;
import foo.FooMill;
import foo._ast.ASTFooArtifact;
import foo._parser.FooParser;
import foo._symboltable.FooSymbols2Json;
import foo._symboltable.IFooArtifactScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class UnknownScopeDeSerTests {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
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

    Assertions.assertTrue(parsedFooArtifact.isPresent());

    ASTFooArtifact fooArtifact = parsedFooArtifact.get();

    IFooArtifactScope fooArtifactScope = FooMill.scopesGenitorDelegator().createFromAST(fooArtifact);
    FooSymbols2Json fooSymbols2Json = new FooSymbols2Json();

    String serializedFoo = fooSymbols2Json.serialize(fooArtifactScope);
    JsonObject jsonFoo = JsonParser.parseJsonObject(serializedFoo);

    IBarGlobalScope barGlobalScope = BarMill.globalScope();
    BarArtifactScope scope = (BarArtifactScope) barGlobalScope.getDeSer().deserializeArtifactScope(jsonFoo);

    Assertions.assertTrue(scope.getUnknownSymbols().containsKey("TestNest"));
    Assertions.assertEquals(1, scope.getUnknownSymbols().get("TestNest").size());

    IBarScope unknownNestScope = (IBarScope) scope.getUnknownSymbols().get("TestNest").get(0).getSpannedScope();
    Assertions.assertEquals(2, unknownNestScope.getSubScopes().size());

    Assertions.assertTrue(unknownNestScope.getSubScopes().stream().anyMatch(it -> Objects.equals(it.getName(), "testFunction")));
    Assertions.assertTrue(unknownNestScope.getSubScopes().stream().anyMatch(it -> Objects.equals(it.getName(), "testFunction2")));
  }

}
