package de.monticore.typescalculator;

import de.monticore.types.typesymbols._ast.TypeSymbolsMill;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TypeExpressionTest {

  private void assertSame(TypeExpression e, TypeExpression eClone) {
    assertFalse(e.equals(eClone));
    assertTrue(e.getName()==eClone.getName());
    assertEquals(e.typeSymbol, eClone.typeSymbol);
  }

  @Test
  public void deepCloneTypeConstantTest() {
    TypeExpression e = new TypeConstant();
    e.setName("double");
    TypeSymbol s = new TypeSymbol("double");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone );
  }

  @Test
  public void deepCloneObjectTypeTest() {
    TypeExpression e = new ObjectType();
    e.setName("String");
    TypeSymbol s = new TypeSymbol("String");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone );
  }

  @Test
  public void deepCloneGenericTypeTest() {
    TypeExpression e = new GenericTypeExpression();
    e.setName("List");
    TypeSymbol s = new TypeSymbol("List");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone );
  }


}
