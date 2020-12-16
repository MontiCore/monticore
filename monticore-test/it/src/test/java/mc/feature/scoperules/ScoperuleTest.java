/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scoperules;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import mc.feature.scoperules.scoperuletest.ScoperuleTestMill;
import mc.feature.scoperules.scoperuletest._symboltable.*;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScoperuleTest {

  @BeforeClass
  public static void setup(){
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testScope(){
    IScoperuleTestScope scope = ScoperuleTestMill.scope();
    scope.setBar(true);
    scope.setNumber(17);
    scope.setModifiedNameList(Lists.newArrayList("foo", "bar", "test"));
    scope.setSymType(SymTypeExpressionFactory.createTypeConstant("int"));

    ScoperuleTestScopeDeSer deSer = new ScoperuleTestScopeDeSer();
    String serialized = deSer.serialize(scope);
    //assertEquals("{\"isShadowingScope\":false,\"number\":17,\"bar\":true,\"modifiedName\":[\"foo\",\"bar\",\"test\"],\"symType\":{\"kind\":\"de.monticore.types.check.SymTypeConstant\",\"constName\":\"int\"}}", serialized);
    IScoperuleTestScope as = deSer.deserialize(serialized);
    assertTrue(as.isBar());
    assertEquals(17, as.getNumber());
    assertEquals(3, as.getModifiedNameList().size());
    assertEquals("foo", as.getModifiedName(0));
    assertEquals("bar", as.getModifiedName(1));
    assertEquals("test", as.getModifiedName(2));
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(as.getSymType()));
  }

  @Test
  public void testExtendsAndImplements(){
    IScoperuleTestScope scope = ScoperuleTestMill.scope();

    assertTrue(scope instanceof ScoperuleTestScope);
    Dummy dummy = (ScoperuleTestScope) scope;

    assertTrue(scope instanceof IDummy);
    IDummy dummyI = (IDummy) scope;
  }

}
