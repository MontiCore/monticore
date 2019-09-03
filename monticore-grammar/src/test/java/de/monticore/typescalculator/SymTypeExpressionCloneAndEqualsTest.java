/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types2.SymGenericTypeExpression;
import de.monticore.types2.SymTypeExpression;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.types2.SymTypeExpressionFactory.*;
import static org.junit.Assert.*;

public class SymTypeExpressionCloneAndEqualsTest {
  
  /// This is testing the old functions ...
  
  @Deprecated
  private void assertSame(SymTypeExpression e, SymTypeExpression eClone) {
    assertFalse(e.equals(eClone));
    assertTrue(e.getName().equals(eClone.getName()));
    assertEquals(e.getTypeSymbol(), eClone.getTypeSymbol());
  }

  @Deprecated
  private void assertDeepEquals(SymTypeExpression a, SymTypeExpression b){
    assertTrue(a.deepEquals(b));
  }

  @Test @Ignore @Deprecated
  public void deepCloneTypeConstantTest() {
    SymTypeExpression e = createTypeConstant("double");
    TypeSymbol s = new TypeSymbol("double");
    e.setTypeSymbol(java.util.Optional.of(s));
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);

    assertDeepEquals(e, eClone);
  }

  @Test @Ignore @Deprecated
  public void deepCloneObjectTypeTest() {
    TypeSymbol s = new TypeSymbol("String");
    SymTypeExpression e = createObjectType("String", s);
    e.setTypeSymbol(java.util.Optional.of(s));
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);

    //example with super-/subclasses
    TypeSymbol b = new TypeSymbol("List");
    SymTypeExpression exp = createObjectType("List", b);
    exp.setTypeSymbol(Optional.of(b));

    TypeSymbol a = new TypeSymbol("ArrayList");
    SymTypeExpression ex = createObjectType("ArrayList", a);
    ex.setTypeSymbol(Optional.of(a));

    SymTypeExpression exClone = ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
  }

  @Test @Ignore @Deprecated
  public void deepCloneGenericTypeTest() {
    TypeSymbol d = new TypeSymbol("Bar");
    SymTypeExpression expre = createObjectType("Bar", d);
    expre.setTypeSymbol(Optional.of(d));
  
  
    TypeSymbol b = new TypeSymbol("Bar");
    SymTypeExpression exp = createObjectType("Bar", b);
    exp.setTypeSymbol(Optional.of(b));

    SymGenericTypeExpression ex = createGenericTypeExpression("Foo",Lists.newArrayList(exp));
    TypeSymbol a = new TypeSymbol("Foo");
    ex.setTypeSymbol(Optional.of(a));

    SymGenericTypeExpression exClone = (SymGenericTypeExpression) ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getArguments().get(0),exClone.getArguments().get(0));
    
    SymTypeExpression e = new SymGenericTypeExpression();
    e.setName("List");
    TypeSymbol s = new TypeSymbol("List");
    e.setTypeSymbol(java.util.Optional.of(s));
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);
  }
}
