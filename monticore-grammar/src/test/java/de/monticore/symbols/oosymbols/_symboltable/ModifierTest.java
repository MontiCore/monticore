/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.modifiers.CompoundAccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.DefsTypeBasic;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ModifierTest {

  protected IOOSymbolsScope symbolTable;

  @BeforeClass
  public static void init(){
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Before
  public void setup(){
    symbolTable = OOSymbolsMill.scope();

    //FieldSymbols
    FieldSymbol barPackagePrivate = DefsTypeBasic.field("bar", SymTypeExpressionFactory.createPrimitive("int"));
    FieldSymbol barPublic = DefsTypeBasic.field("bar", SymTypeExpressionFactory.createPrimitive("int"));
    barPublic.setIsPublic(true);
    FieldSymbol barProtected = DefsTypeBasic.field("bar", SymTypeExpressionFactory.createPrimitive("int"));
    barProtected.setIsProtected(true);
    FieldSymbol barPrivate = DefsTypeBasic.field("bar", SymTypeExpressionFactory.createPrimitive("int"));
    barPrivate.setIsPrivate(true);
    FieldSymbol barStatic = DefsTypeBasic.field("bar", SymTypeExpressionFactory.createPrimitive("int"));
    barStatic.setIsStatic(true);
    FieldSymbol barPublicStatic = DefsTypeBasic.field("bar", SymTypeExpressionFactory.createPrimitive("int"));
    barPublicStatic.setIsPublic(true);
    barPublicStatic.setIsStatic(true);

    symbolTable.add(barPackagePrivate);
    symbolTable.add(barPublic);
    symbolTable.add(barProtected);
    symbolTable.add(barPrivate);
    symbolTable.add(barStatic);
    symbolTable.add(barPublicStatic);

    //MethodSymbols
    MethodSymbol fooPackagePrivate = DefsTypeBasic.method("foo", SymTypeExpressionFactory.createPrimitive("int"));
    MethodSymbol fooPublic = DefsTypeBasic.method("foo", SymTypeExpressionFactory.createPrimitive("int"));
    fooPublic.setIsPublic(true);
    MethodSymbol fooProtected = DefsTypeBasic.method("foo", SymTypeExpressionFactory.createPrimitive("int"));
    fooProtected.setIsProtected(true);
    MethodSymbol fooPrivate = DefsTypeBasic.method("foo", SymTypeExpressionFactory.createPrimitive("int"));
    fooPrivate.setIsPrivate(true);
    MethodSymbol fooStatic = DefsTypeBasic.method("foo", SymTypeExpressionFactory.createPrimitive("int"));
    fooPrivate.setIsStatic(true);
    MethodSymbol fooProtectedStatic = DefsTypeBasic.method("foo", SymTypeExpressionFactory.createPrimitive("int"));
    fooProtectedStatic.setIsProtected(true);
    fooProtectedStatic.setIsStatic(true);

    symbolTable.add(fooPackagePrivate);
    symbolTable.add(fooPublic);
    symbolTable.add(fooProtected);
    symbolTable.add(fooPrivate);
    symbolTable.add(fooStatic);
    symbolTable.add(fooProtectedStatic);

    //TypeSymbols
    OOTypeSymbol testPackagePrivate = DefsTypeBasic.type("Test");
    OOTypeSymbol testPublic = DefsTypeBasic.type("Test");
    testPublic.setIsPublic(true);
    OOTypeSymbol testProtected = DefsTypeBasic.type("Test");
    testProtected.setIsProtected(true);
    OOTypeSymbol testPrivate = DefsTypeBasic.type("Test");
    testPrivate.setIsPrivate(true);
    OOTypeSymbol testStatic = DefsTypeBasic.type("Test");
    testStatic.setIsStatic(true);
    OOTypeSymbol testPrivateStatic = DefsTypeBasic.type("Test");
    testPrivateStatic.setIsPrivate(true);
    testPrivateStatic.setIsStatic(true);

    symbolTable.add(testPackagePrivate);
    symbolTable.add(testPublic);
    symbolTable.add(testProtected);
    symbolTable.add(testPrivate);
    symbolTable.add(testStatic);
    symbolTable.add(testPrivateStatic);
  }

  @Test
  public void testType(){
    List<OOTypeSymbol> typesAllInclusion = symbolTable.resolveOOTypeMany("Test");
    assertEquals(6, typesAllInclusion.size());

    List<OOTypeSymbol> typesPublic = symbolTable.resolveOOTypeMany("Test", BasicAccessModifier.PUBLIC);
    assertEquals(1, typesPublic.size());

    List<OOTypeSymbol> typesProtected = symbolTable.resolveOOTypeMany("Test", BasicAccessModifier.PROTECTED);
    assertEquals(2, typesProtected.size());

    List<OOTypeSymbol> typesPrivate = symbolTable.resolveOOTypeMany("Test", BasicAccessModifier.PRIVATE);
    assertEquals(6, typesPrivate.size());

    List<OOTypeSymbol> typesStatic = symbolTable.resolveOOTypeMany("Test", StaticAccessModifier.STATIC);
    assertEquals(2, typesStatic.size());

    List<OOTypeSymbol> typesPrivateStatic = symbolTable
      .resolveOOTypeMany("Test", new CompoundAccessModifier(List.of(BasicAccessModifier.PRIVATE, StaticAccessModifier.STATIC)));
    assertEquals(2, typesPrivateStatic.size());
  }

  @Test
  public void testMethod(){
    List<MethodSymbol> methodsAllInclusion = symbolTable.resolveMethodMany("foo");
    assertEquals(6, methodsAllInclusion.size());

    List<MethodSymbol> methodsPublic = symbolTable.resolveMethodMany("foo", BasicAccessModifier.PUBLIC);
    assertEquals(1, methodsPublic.size());

    List<MethodSymbol> methodsProtected = symbolTable.resolveMethodMany("foo", BasicAccessModifier.PROTECTED);
    assertEquals(3, methodsProtected.size());

    List<MethodSymbol> methodsPrivate = symbolTable.resolveMethodMany("foo", BasicAccessModifier.PRIVATE);
    assertEquals(6, methodsPrivate.size());

    List<MethodSymbol> methodsStatic = symbolTable.resolveMethodMany("foo", StaticAccessModifier.STATIC);
    assertEquals(2, methodsStatic.size());

    List<MethodSymbol> methodsProtectedStatic = symbolTable
      .resolveMethodMany("foo", new CompoundAccessModifier(List.of(BasicAccessModifier.PROTECTED, StaticAccessModifier.STATIC)));
    assertEquals(1, methodsProtectedStatic.size());
  }

  @Test
  public void testField(){
    List<FieldSymbol> fieldsAllInclusion = symbolTable.resolveFieldMany("bar");
    assertEquals(6, fieldsAllInclusion.size());

    List<FieldSymbol> fieldsPublic = symbolTable.resolveFieldMany("bar", BasicAccessModifier.PUBLIC);
    assertEquals(2, fieldsPublic.size());

    List<FieldSymbol> fieldsProtected = symbolTable.resolveFieldMany("bar", BasicAccessModifier.PROTECTED);
    assertEquals(3, fieldsProtected.size());

    List<FieldSymbol> fieldsPrivate = symbolTable.resolveFieldMany("bar", BasicAccessModifier.PRIVATE);
    assertEquals(6, fieldsPrivate.size());

    List<FieldSymbol> fieldsStatic = symbolTable.resolveFieldMany("bar", StaticAccessModifier.STATIC);
    assertEquals(2, fieldsStatic.size());

    List<FieldSymbol> fieldsPublicStatic = symbolTable
      .resolveFieldMany("bar", new CompoundAccessModifier(BasicAccessModifier.PUBLIC, StaticAccessModifier.STATIC));
    assertEquals(1, fieldsPublicStatic.size());
  }

}
