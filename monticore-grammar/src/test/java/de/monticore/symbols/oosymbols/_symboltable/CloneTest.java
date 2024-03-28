/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.modifiers.CompoundAccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.DefsTypeBasic;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CloneTest {

  protected IOOSymbolsScope symbolTable;

  @Before
  public void init(){
    LogStub.init();
    Log.enableFailQuick(false);

    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();

    symbolTable = OOSymbolsMill.scope();

    //FieldSymbols
    FieldSymbol bar = DefsTypeBasic.field("bar", SymTypeExpressionFactory.createPrimitive("int"));
    bar.setIsPublic(true);
    bar.setIsStatic(true);
    bar.setIsReadOnly(true);
    bar.setIsFinal(true);
    bar.setIsDerived(true);
    symbolTable.add(bar);

    //MethodSymbols
    MethodSymbol foo = DefsTypeBasic.method("foo", SymTypeExpressionFactory.createPrimitive("int"));
    foo.setIsPublic(true);
    foo.setIsStatic(true);
    foo.setIsAbstract(true);
    foo.setIsMethod(true);
    foo.setIsElliptic(true);
    symbolTable.add(foo);

  }


  @Test
  public void testMethod(){
    List<MethodSymbol> methodsAllInclusion = symbolTable.resolveMethodMany("foo");
    assertEquals(1, methodsAllInclusion.size());

    MethodSymbol methodSymbol = methodsAllInclusion.get(0);
    MethodSymbol cloneSymbol = methodSymbol.deepClone();

    assertEquals(methodSymbol.getName(), cloneSymbol.getName());
    assertEquals(methodSymbol.getParameterList().size(), cloneSymbol.getParameterList().size());
    assertEquals(methodSymbol.isIsMethod(), cloneSymbol.isIsMethod());
    assertEquals(methodSymbol.isIsPrivate(), cloneSymbol.isIsPrivate());
    assertEquals(methodSymbol.isIsPublic(), cloneSymbol.isIsPublic());
    assertEquals(methodSymbol.isIsProtected(), cloneSymbol.isIsProtected());
    assertEquals(methodSymbol.isIsAbstract(), cloneSymbol.isIsAbstract());
    assertEquals(methodSymbol.isIsStatic(), cloneSymbol.isIsStatic());
    assertEquals(methodSymbol.isIsFinal(), cloneSymbol.isIsFinal());
    assertEquals(methodSymbol.isIsElliptic(), cloneSymbol.isIsElliptic());
    assertEquals(methodSymbol.isIsConstructor(), cloneSymbol.isIsConstructor());
    assertEquals(methodSymbol.getType().asPrimitive().getPrimitiveName(), cloneSymbol.getType().asPrimitive().getPrimitiveName());
  }

  @Test
  public void testField(){
    List<FieldSymbol> fieldsAllInclusion = symbolTable.resolveFieldMany("bar");
    assertEquals(1, fieldsAllInclusion.size());

    FieldSymbol fieldSymbol = fieldsAllInclusion.get(0);
    FieldSymbol cloneSymbol = fieldSymbol.deepClone();

    assertEquals(fieldSymbol.getName(), cloneSymbol.getName());
    assertEquals(fieldSymbol.getType().asPrimitive().getPrimitiveName(), cloneSymbol.getType().asPrimitive().getPrimitiveName());
    assertEquals(fieldSymbol.isIsFinal(), cloneSymbol.isIsFinal());
    assertEquals(fieldSymbol.isIsPrivate(), cloneSymbol.isIsPrivate());
    assertEquals(fieldSymbol.isIsProtected(), cloneSymbol.isIsProtected());
    assertEquals(fieldSymbol.isIsPublic(), cloneSymbol.isIsPublic());
    assertEquals(fieldSymbol.isIsStatic(), cloneSymbol.isIsStatic());
    assertEquals(fieldSymbol.isIsDerived(), cloneSymbol.isIsDerived());
    assertEquals(fieldSymbol.isIsReadOnly(), cloneSymbol.isIsReadOnly());
  }

}
