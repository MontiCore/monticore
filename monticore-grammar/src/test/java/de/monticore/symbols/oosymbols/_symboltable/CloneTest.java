/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.types.check.DefsTypeBasic;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CloneTest {

  protected IOOSymbolsScope symbolTable;

  @BeforeEach
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
    Assertions.assertEquals(1, methodsAllInclusion.size());

    MethodSymbol methodSymbol = methodsAllInclusion.get(0);
    MethodSymbol cloneSymbol = methodSymbol.deepClone();

    Assertions.assertEquals(methodSymbol.getName(), cloneSymbol.getName());
    Assertions.assertEquals(methodSymbol.getParameterList().size(), cloneSymbol.getParameterList().size());
    Assertions.assertEquals(methodSymbol.isIsMethod(), cloneSymbol.isIsMethod());
    Assertions.assertEquals(methodSymbol.isIsPrivate(), cloneSymbol.isIsPrivate());
    Assertions.assertEquals(methodSymbol.isIsPublic(), cloneSymbol.isIsPublic());
    Assertions.assertEquals(methodSymbol.isIsProtected(), cloneSymbol.isIsProtected());
    Assertions.assertEquals(methodSymbol.isIsAbstract(), cloneSymbol.isIsAbstract());
    Assertions.assertEquals(methodSymbol.isIsStatic(), cloneSymbol.isIsStatic());
    Assertions.assertEquals(methodSymbol.isIsFinal(), cloneSymbol.isIsFinal());
    Assertions.assertEquals(methodSymbol.isIsElliptic(), cloneSymbol.isIsElliptic());
    Assertions.assertEquals(methodSymbol.isIsConstructor(), cloneSymbol.isIsConstructor());
    Assertions.assertEquals(methodSymbol.getType().asPrimitive().getPrimitiveName(), cloneSymbol.getType().asPrimitive().getPrimitiveName());
  }

  @Test
  public void testField(){
    List<FieldSymbol> fieldsAllInclusion = symbolTable.resolveFieldMany("bar");
    Assertions.assertEquals(1, fieldsAllInclusion.size());

    FieldSymbol fieldSymbol = fieldsAllInclusion.get(0);
    FieldSymbol cloneSymbol = fieldSymbol.deepClone();

    Assertions.assertEquals(fieldSymbol.getName(), cloneSymbol.getName());
    Assertions.assertEquals(fieldSymbol.getType().asPrimitive().getPrimitiveName(), cloneSymbol.getType().asPrimitive().getPrimitiveName());
    Assertions.assertEquals(fieldSymbol.isIsFinal(), cloneSymbol.isIsFinal());
    Assertions.assertEquals(fieldSymbol.isIsPrivate(), cloneSymbol.isIsPrivate());
    Assertions.assertEquals(fieldSymbol.isIsProtected(), cloneSymbol.isIsProtected());
    Assertions.assertEquals(fieldSymbol.isIsPublic(), cloneSymbol.isIsPublic());
    Assertions.assertEquals(fieldSymbol.isIsStatic(), cloneSymbol.isIsStatic());
    Assertions.assertEquals(fieldSymbol.isIsDerived(), cloneSymbol.isIsDerived());
    Assertions.assertEquals(fieldSymbol.isIsReadOnly(), cloneSymbol.isIsReadOnly());
  }

}
