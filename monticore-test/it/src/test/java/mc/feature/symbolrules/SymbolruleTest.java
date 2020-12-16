/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import mc.feature.symbolrules.symbolruletest.SymbolruleTestMill;
import mc.feature.symbolrules.symbolruletest._ast.ASTFoo;
import mc.feature.symbolrules.symbolruletest._parser.SymbolruleTestParser;
import mc.feature.symbolrules.symbolruletest._symboltable.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolruleTest {

  @BeforeClass
  public static void setup(){
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testScope(){
    ISymbolruleTestScope scope = SymbolruleTestMill.scope();
    scope.setBar(true);
    scope.setNumber(17);
    //scope.setModifiedNameList(Lists.newArrayList("foo", "bar", "test"));
    scope.setSymType(SymTypeExpressionFactory.createTypeConstant("int"));

    SymbolruleTestScopeDeSer deSer = new SymbolruleTestScopeDeSer();
    String serialized = deSer.serialize(scope);
    ISymbolruleTestArtifactScope as = deSer.deserialize(serialized);
    assertTrue(as.isBar());
    assertEquals(17, as.getNumber());

    assertEquals(3, as.getModifiedNameList().size());
    assertEquals("foo", as.getModifiedName(0));
    assertEquals("bar", as.getModifiedName(1));
    assertEquals("test", as.getModifiedName(2));
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(as.getSymType()));
  }

  @Test
  public void testSymbols(){
    FooSymbol fooSymbol = SymbolruleTestMill.fooSymbolBuilder().setName("foofoo").build();
    fooSymbol.setSpannedScope(SymbolruleTestMill.scope());
    fooSymbol.setBarName("barbar");
    FooSymbolDeSer fooSymbolDeSer = new FooSymbolDeSer();
    String serializedFoo = fooSymbolDeSer.serialize(fooSymbol);
    FooSymbol deserializedFoo = fooSymbolDeSer.deserialize(serializedFoo);
    assertEquals(fooSymbol.getName(), deserializedFoo.getName());
    assertEquals(fooSymbol.getBarName(), deserializedFoo.getBarName());
    
    
    ITestSymbol itest = SymbolruleTestMill.iTestSymbolBuilder().setName("itest").build();
    itest.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeConstant("int")));
    ITestSymbolDeSer iTestSymbolDeSer = new ITestSymbolDeSer();
    String serializedITest = iTestSymbolDeSer.serialize(itest);
    ITestSymbol deserializedITest = iTestSymbolDeSer.deserialize(serializedITest);
    assertEquals(itest.getName(), deserializedITest.getName());
    assertEquals(1, deserializedITest.sizeSuperTypes());
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(deserializedITest.getSuperTypes(0)));
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(deserializedITest.getByName("int")));
    
    Test1Symbol test1 = SymbolruleTestMill.test1SymbolBuilder().setName("test11").build();
    test1.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeConstant("int")));
    test1.setIsPrivate(true);
    Test1SymbolDeSer test1SymbolDeSer = new Test1SymbolDeSer();
    String serializedTest1 = test1SymbolDeSer.serialize(test1);
    Test1Symbol deserializedTest1 = test1SymbolDeSer.deserialize(serializedTest1);
    assertEquals(test1.getName(), deserializedTest1.getName());
    assertEquals(1, deserializedTest1.sizeSuperTypes());
    assertTrue(deserializedTest1.isIsPrivate());
    assertEquals(deserializedTest1, deserializedTest1.getIfPrivate());
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(deserializedTest1.getSuperTypes(0)));
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(deserializedTest1.getByName("int")));

    Test2Symbol test2 = SymbolruleTestMill.test2SymbolBuilder().setName("test22").build();
    test2.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeConstant("int")));
    test2.setIsPublic(true);
    Test2SymbolDeSer test2SymbolDeSer = new Test2SymbolDeSer();
    String serializedTest2 = test2SymbolDeSer.serialize(test2);
    Test2Symbol deserializedTest2 = test2SymbolDeSer.deserialize(serializedTest2);
    assertEquals(test2.getName(), deserializedTest2.getName());
    assertEquals(1, deserializedTest2.sizeSuperTypes());
    assertTrue(deserializedTest2.isIsPublic());
    assertEquals(deserializedTest2, deserializedTest2.getIfPublic());
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(deserializedTest2.getSuperTypes(0)));
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(deserializedTest2.getByName("int")));
  }

  @Test
  public void testModel() throws IOException {
    SymbolruleTestParser parser = SymbolruleTestMill.parser();
    Optional<ASTFoo> model = parser.parse("src/test/resources/mc/feature/symbolrules/SymbolruleTest.rule");
    assertTrue(model.isPresent());

    SymbolruleTestScopesGenitorDelegator scopesGenitor = SymbolruleTestMill.scopesGenitorDelegator();
    ISymbolruleTestArtifactScope as = scopesGenitor.createFromAST(model.get());
    as.setName("SymbolruleTest");

    SymbolruleTestScopeDeSer deSer = new SymbolruleTestScopeDeSer();
    String serialized = deSer.serialize(as);

    ISymbolruleTestArtifactScope as2 = deSer.deserialize(serialized);
    assertEquals(as.getName(), as2.getName());
    assertEquals(as.getSymbolsSize(), as2.getSymbolsSize());
    assertEquals(1, as2.getLocalFooSymbols().size());
    ISymbolruleTestScope fooSpannedScope = as2.getLocalFooSymbols().get(0).getSpannedScope();
    /* TODO wieder einkommentieren, wenn gefixt -> momentan Problem, dass Subsymbolen auch in
        Supersymbollisten von Scopes gespeichert und damit mehrmals serialisiert werden
    assertEquals(2, fooSpannedScope.getLocalBarSymbols().size());
    assertEquals("Test1", fooSpannedScope.getLocalBarSymbols().get(0).getName());
    assertEquals("Test2", fooSpannedScope.getLocalBarSymbols().get(1).getName());
    ISymbolruleTestScope bar1SpannedScope = fooSpannedScope.getLocalBarSymbols().get(0).getSpannedScope();
    assertEquals(2, bar1SpannedScope.getLocalTest1Symbols());
    assertEquals("symbol1", bar1SpannedScope.getLocalTest1Symbols().get(0).getName());
    assertEquals("symbol11", bar1SpannedScope.getLocalTest1Symbols().get(1).getName());
    assertEquals(1, bar1SpannedScope.getLocalTest2Symbols());
    assertEquals("symbol2", bar1SpannedScope.getLocalTest2Symbols().get(0).getName());
    ISymbolruleTestScope bar2SpannedScope = fooSpannedScope.getLocalBarSymbols().get(1).getSpannedScope();
    assertEquals(2, bar2SpannedScope.getLocalTest1Symbols());
    assertEquals("symbol3", bar2SpannedScope.getLocalTest1Symbols().get(0).getName());
    assertEquals("symbol4", bar2SpannedScope.getLocalTest1Symbols().get(1).getName());
    assertEquals(1, bar2SpannedScope.getLocalTest2Symbols());
    assertEquals("symbol22", bar2SpannedScope.getLocalTest2Symbols().get(0).getName());*/
  }

  @Test
  public void testExtendsAndImplements(){
    IBarSymbol symbol = SymbolruleTestMill.barSymbolBuilder().setName("lala").build();
    assertTrue(symbol instanceof Dummy);
    Dummy dummy = (Dummy) symbol;
    assertTrue(symbol instanceof IDummy);
    IDummy iDummy = (IDummy) symbol;
  }


}
