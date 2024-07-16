/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scoperules;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.scoperules.scoperuletest.ScoperuleTestMill;
import mc.feature.scoperules.scoperuletest._parser.ScoperuleTestParser;
import mc.feature.scoperules.scoperuletest._symboltable.*;
import mc.feature.scoperules.scoperuletest._ast.ASTFoo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class ScoperuleTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setup(){
    ScoperuleTestMill.reset();
    ScoperuleTestMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testModel() throws IOException {
    ScoperuleTestParser parser = ScoperuleTestMill.parser();
    Optional<ASTFoo> optModel = parser.parse("src/test/resources/mc/feature/symbolrules/SymbolruleTest.rule");
    Assertions.assertTrue(optModel.isPresent());

    ScoperuleTestScopesGenitorDelegator scopesGenitorDelegator = ScoperuleTestMill.scopesGenitorDelegator();
    IScoperuleTestArtifactScope scope = scopesGenitorDelegator.createFromAST(optModel.get());
    scope.setName("SymbolruleTest");
    scope.setBar(true);
    scope.setNumber(17);
    scope.setModifiedNameList(Lists.newArrayList("foo", "bar", "test"));
    scope.setSymType(SymTypeExpressionFactory.createPrimitive("int"));
    ScoperuleTestSymbols2Json symbols2Json = new ScoperuleTestSymbols2Json();
    scope.accept(symbols2Json.getTraverser());
    String serialized = symbols2Json.getSerializedString();
    IScoperuleTestScope as = symbols2Json.deserialize(serialized);
    Assertions.assertTrue(as.isBar());
    Assertions.assertEquals(17, as.getNumber());
    Assertions.assertEquals(3, as.getModifiedNameList().size());
    Assertions.assertEquals("foo", as.getModifiedName(0));
    Assertions.assertEquals("bar", as.getModifiedName(1));
    Assertions.assertEquals("test", as.getModifiedName(2));
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(as.getSymType()));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExtendsAndImplements(){
    IScoperuleTestScope scope = ScoperuleTestMill.scope();

    Assertions.assertTrue(scope instanceof ScoperuleTestScope);
    Dummy dummy = (ScoperuleTestScope) scope;

    Assertions.assertTrue(scope instanceof IDummy);
    IDummy dummyI = (IDummy) scope;
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
