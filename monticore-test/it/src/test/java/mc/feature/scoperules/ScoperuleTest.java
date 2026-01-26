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
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    assertTrue(optModel.isPresent());

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
    assertTrue(as.isBar());
    assertEquals(17, as.getNumber());
    assertEquals(3, as.getModifiedNameList().size());
    assertEquals("foo", as.getModifiedName(0));
    assertEquals("bar", as.getModifiedName(1));
    assertEquals("test", as.getModifiedName(2));
    assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(as.getSymType()));
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExtendsAndImplements(){
    IScoperuleTestScope scope = ScoperuleTestMill.scope();
    
    assertInstanceOf(ScoperuleTestScope.class, scope);
    Dummy dummy = (ScoperuleTestScope) scope;
    
    assertInstanceOf(IDummy.class, scope);
    IDummy dummyI = (IDummy) scope;
    assertTrue(Log.getFindings().isEmpty());
  }

}
