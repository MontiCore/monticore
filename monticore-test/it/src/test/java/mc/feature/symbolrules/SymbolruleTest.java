/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.symbolrules.symbolrulelisttest.SymbolruleListTestMill;
import mc.feature.symbolrules.symbolrulelisttest._parser.SymbolruleListTestParser;
import mc.feature.symbolrules.symbolrulelisttest._symboltable.ISymbolruleListTestArtifactScope;
import mc.feature.symbolrules.symbolrulelisttest._symboltable.SymbolruleListTestScopesGenitorDelegator;
import mc.feature.symbolrules.symbolrulelisttest._symboltable.SymbolruleListTestSymbols2Json;
import mc.feature.symbolrules.symbolruletest.SymbolruleTestMill;
import mc.feature.symbolrules.symbolruletest._ast.ASTFoo;
import mc.feature.symbolrules.symbolruletest._parser.SymbolruleTestParser;
import mc.feature.symbolrules.symbolruletest._symboltable.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class SymbolruleTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setup(){
    SymbolruleTestMill.reset();
    SymbolruleTestMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testSymbols(){
    FooSymbol fooSymbol = SymbolruleTestMill.fooSymbolBuilder().setName("foofoo").build();
    fooSymbol.setSpannedScope(SymbolruleTestMill.scope());
    fooSymbol.setBarName("barbar");
    FooSymbolDeSer fooSymbolDeSer = new FooSymbolDeSer();
    String serializedFoo = fooSymbolDeSer.serialize(fooSymbol, new SymbolruleTestSymbols2Json());
    FooSymbol deserializedFoo = fooSymbolDeSer.deserialize(serializedFoo);
    Assertions.assertEquals(fooSymbol.getName(), deserializedFoo.getName());
    Assertions.assertEquals(fooSymbol.getBarName(), deserializedFoo.getBarName());
    
    
    ITestSymbol itest = SymbolruleTestMill.iTestSymbolBuilder().setName("itest").build();
    itest.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createPrimitive("int")));
    ITestSymbolDeSer iTestSymbolDeSer = new ITestSymbolDeSer();
    String serializedITest = iTestSymbolDeSer.serialize(itest, new SymbolruleTestSymbols2Json());
    ITestSymbol deserializedITest = iTestSymbolDeSer.deserialize(serializedITest);
    Assertions.assertEquals(itest.getName(), deserializedITest.getName());
    Assertions.assertEquals(1, deserializedITest.sizeSuperTypes());
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(deserializedITest.getSuperTypes(0)));
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(deserializedITest.getByName("int")));
    
    Test1Symbol test1 = SymbolruleTestMill.test1SymbolBuilder().setName("test11").build();
    test1.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createPrimitive("int")));
    test1.setIsPrivate(true);
    Test1SymbolDeSer test1SymbolDeSer = new Test1SymbolDeSer();
    String serializedTest1 = test1SymbolDeSer.serialize(test1, new SymbolruleTestSymbols2Json());
    Test1Symbol deserializedTest1 = test1SymbolDeSer.deserialize(serializedTest1);
    Assertions.assertEquals(test1.getName(), deserializedTest1.getName());
    Assertions.assertEquals(1, deserializedTest1.sizeSuperTypes());
    Assertions.assertTrue(deserializedTest1.isIsPrivate());
    Assertions.assertEquals(deserializedTest1, deserializedTest1.getIfPrivate());
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(deserializedTest1.getSuperTypes(0)));
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(deserializedTest1.getByName("int")));

    Test2Symbol test2 = SymbolruleTestMill.test2SymbolBuilder().setName("test22").build();
    test2.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createPrimitive("int")));
    test2.setIsPublic(true);
    Test2SymbolDeSer test2SymbolDeSer = new Test2SymbolDeSer();
    String serializedTest2 = test2SymbolDeSer.serialize(test2, new SymbolruleTestSymbols2Json());
    Test2Symbol deserializedTest2 = test2SymbolDeSer.deserialize(serializedTest2);
    Assertions.assertEquals(test2.getName(), deserializedTest2.getName());
    Assertions.assertEquals(1, deserializedTest2.sizeSuperTypes());
    Assertions.assertTrue(deserializedTest2.isIsPublic());
    Assertions.assertEquals(deserializedTest2, deserializedTest2.getIfPublic());
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(deserializedTest2.getSuperTypes(0)));
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(deserializedTest2.getByName("int")));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testModel() throws IOException {
    SymbolruleTestParser parser = SymbolruleTestMill.parser();
    Optional<ASTFoo> model = parser.parse("src/test/resources/mc/feature/symbolrules/SymbolruleTest.rule");
    Assertions.assertTrue(model.isPresent());

    SymbolruleTestScopesGenitorDelegator scopesGenitor = SymbolruleTestMill.scopesGenitorDelegator();
    ISymbolruleTestArtifactScope as = scopesGenitor.createFromAST(model.get());
    as.setName("SymbolruleTest");
    as.setBar(true);
    as.setNumber(17);
    as.setModifiedNameList(Lists.newArrayList("foo", "bar", "test"));
    as.setSymType(SymTypeExpressionFactory.createPrimitive("int"));

    SymbolruleTestSymbols2Json symbols2Json = new SymbolruleTestSymbols2Json();
    as.accept(symbols2Json.getTraverser());
    String serialized = symbols2Json.getSerializedString();

    ISymbolruleTestArtifactScope as2 = symbols2Json.deserialize(serialized);
    Assertions.assertEquals(as.getName(), as2.getName());
    Assertions.assertEquals(as.getSymbolsSize(), as2.getSymbolsSize());
    Assertions.assertTrue(as2.isBar());
    Assertions.assertEquals(17, as2.getNumber());
    Assertions.assertEquals(3, as.getModifiedNameList().size());
    Assertions.assertEquals("foo", as.getModifiedName(0));
    Assertions.assertEquals("bar", as.getModifiedName(1));
    Assertions.assertEquals("test", as.getModifiedName(2));
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("int").deepEquals(as2.getSymType()));
    Assertions.assertEquals(1, as2.getLocalFooSymbols().size());
    ISymbolruleTestScope fooSpannedScope = as2.getLocalFooSymbols().get(0).getSpannedScope();
    Assertions.assertEquals(2, fooSpannedScope.getLocalBarSymbols().size());
    Assertions.assertEquals("Test1", fooSpannedScope.getLocalBarSymbols().get(0).getName());
    Assertions.assertEquals("Test2", fooSpannedScope.getLocalBarSymbols().get(1).getName());
    ISymbolruleTestScope bar1SpannedScope = fooSpannedScope.getLocalBarSymbols().get(0).getSpannedScope();
    Assertions.assertEquals(2, bar1SpannedScope.getLocalTest1Symbols().size());
    Assertions.assertEquals("symbol1", bar1SpannedScope.getLocalTest1Symbols().get(0).getName());
    Assertions.assertEquals("symbol11", bar1SpannedScope.getLocalTest1Symbols().get(1).getName());
    Assertions.assertEquals(1, bar1SpannedScope.getLocalTest2Symbols().size());
    Assertions.assertEquals("symbol2", bar1SpannedScope.getLocalTest2Symbols().get(0).getName());
    ISymbolruleTestScope bar2SpannedScope = fooSpannedScope.getLocalBarSymbols().get(1).getSpannedScope();
    Assertions.assertEquals(2, bar2SpannedScope.getLocalTest1Symbols().size());
    Assertions.assertEquals("symbol3", bar2SpannedScope.getLocalTest1Symbols().get(0).getName());
    Assertions.assertEquals("symbol4", bar2SpannedScope.getLocalTest1Symbols().get(1).getName());
    Assertions.assertEquals(1, bar2SpannedScope.getLocalTest2Symbols().size());
    Assertions.assertEquals("symbol22", bar2SpannedScope.getLocalTest2Symbols().get(0).getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExtendsAndImplements(){
    IBarSymbol symbol = SymbolruleTestMill.barSymbolBuilder().setName("lala").build();
    Assertions.assertTrue(symbol instanceof Dummy);
    Dummy dummy = (Dummy) symbol;
    Assertions.assertTrue(symbol instanceof IDummy);
    IDummy iDummy = (IDummy) symbol;
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolruleListAttributes() throws IOException{
    SymbolruleListTestParser parser = SymbolruleListTestMill.parser();
    Optional<mc.feature.symbolrules.symbolrulelisttest._ast.ASTFoo> opt = parser.parse("src/test/resources/mc/feature/symbolrules/SymbolruleTest.rule");
    Assertions.assertTrue(opt.isPresent());
    SymbolruleListTestScopesGenitorDelegator scopesGenitor = SymbolruleListTestMill.scopesGenitorDelegator();
    ISymbolruleListTestArtifactScope as = scopesGenitor.createFromAST(opt.get());
    as.setName("SymbolruleTest");
    as.setNumbersList(Lists.newArrayList(3,4,5));
    as.setNamesList(Lists.newArrayList("A", "B", "C"));
    as.setSymTypesList(Lists.newArrayList(SymTypeExpressionFactory.createPrimitive("int"), SymTypeExpressionFactory.createPrimitive("double")));
    as.setArePresentList(Lists.newArrayList(false, true, true, false));
    as.setBigNumbersList(Lists.newArrayList(3l));
    as.setDoubleFloatingPointsList(Lists.newArrayList(3.4,6.3,5.5));
    as.setFloatingPointsList(Lists.newArrayList(3.4f,32.4f,1.3f));

    SymbolruleListTestSymbols2Json symbols2Json = new SymbolruleListTestSymbols2Json();
    as.accept(symbols2Json.getTraverser());
    String serialized = symbols2Json.getSerializedString();

    ISymbolruleListTestArtifactScope as2 = symbols2Json.deserialize(serialized);
    Assertions.assertEquals(as.getName(), as2.getName());
    Assertions.assertEquals(as.sizeNumbers(), as2.sizeNumbers());
    Assertions.assertEquals(as.getNumbers(0), as2.getNumbers(0));
    Assertions.assertEquals(as.getNumbers(1), as2.getNumbers(1));
    Assertions.assertEquals(as.getNumbers(2), as2.getNumbers(2));
    Assertions.assertEquals(as.sizeNames(), as2.sizeNames());
    Assertions.assertEquals(as.getNames(0), as2.getNames(0));
    Assertions.assertEquals(as.getNames(1), as2.getNames(1));
    Assertions.assertEquals(as.getNames(2), as2.getNames(2));
    Assertions.assertEquals(as.sizeSymTypes(), as2.sizeSymTypes());
    Assertions.assertTrue(as.getSymTypes(0).deepEquals(as2.getSymTypes(0)));
    Assertions.assertTrue(as.getSymTypes(1).deepEquals(as2.getSymTypes(1)));
    Assertions.assertEquals(as.sizeArePresent(), as2.sizeArePresent());
    Assertions.assertEquals(as.getArePresent(0), as2.getArePresent(0));
    Assertions.assertEquals(as.getArePresent(1), as2.getArePresent(1));
    Assertions.assertEquals(as.getArePresent(2), as2.getArePresent(2));
    Assertions.assertEquals(as.getArePresent(3), as2.getArePresent(3));
    Assertions.assertEquals(as.sizeBigNumbers(), as2.sizeBigNumbers());
    Assertions.assertEquals(as.getBigNumbers(0), as2.getBigNumbers(0));
    Assertions.assertEquals(as.sizeDoubleFloatingPoints(), as2.sizeDoubleFloatingPoints());
    Assertions.assertEquals(as.getDoubleFloatingPoints(0), as2.getDoubleFloatingPoints(0));
    Assertions.assertEquals(as.getDoubleFloatingPoints(1), as2.getDoubleFloatingPoints(1));
    Assertions.assertEquals(as.getDoubleFloatingPoints(2), as2.getDoubleFloatingPoints(2));
    Assertions.assertEquals(as.sizeFloatingPoints(), as2.sizeFloatingPoints());
    Assertions.assertEquals(as.getFloatingPoints(0), as2.getFloatingPoints(0));
    Assertions.assertEquals(as.getFloatingPoints(1), as2.getFloatingPoints(1));
    Assertions.assertEquals(as.getFloatingPoints(2), as2.getFloatingPoints(2));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


}
