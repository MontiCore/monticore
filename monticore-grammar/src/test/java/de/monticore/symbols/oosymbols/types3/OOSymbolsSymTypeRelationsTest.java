// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.oosymbols.types3;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.AbstractTypeVisitorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedString;
import static de.monticore.types3.util.DefsTypesForTests.field;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.method;
import static de.monticore.types3.util.DefsTypesForTests.oOtype;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OOSymbolsSymTypeRelationsTest extends AbstractTypeVisitorTest {

  protected SymTypeExpression myClassType;
  protected SymTypeExpression myClassConstructorType;
  protected SymTypeExpression myClassGetNameType;
  protected SymTypeExpression myInterfaceType;
  protected SymTypeExpression myFunctionalInterfaceType;
  protected SymTypeExpression myFunctionalInterfaceApplyType;
  protected SymTypeExpression myFunctionalInterfaceSubType;
  protected SymTypeExpression myNonFunctionalInterfaceSubType;
  protected SymTypeExpression myEnumType;
  protected SymTypeExpression myEnumVal1Type;
  protected SymTypeExpression myEnumVal2Type;
  protected SymTypeExpression myEnumDefaultType;

  @BeforeEach
  public void init() throws IOException {
    OOSymbolsSymTypeRelations.init();
    initTypes();
  }

  protected void initTypes() throws IOException {
    IOOSymbolsScope gs = OOSymbolsMill.globalScope();

    OOTypeSymbol myClassSym = inScope(gs, oOtype("MyClass"));
    myClassSym.setIsClass(true);
    VariableSymbol myClassVar = inScope(gs,
        variable("myClass", createTypeObject(myClassSym))
    );
    MethodSymbol myClassGetNameSym = inScope(myClassSym.getSpannedScope(),
        method("getName", _unboxedString)
    );
    myClassGetNameSym.setIsMethod(true);
    MethodSymbol myClassConstructorSym = inScope(myClassSym.getSpannedScope(),
        method("MyClass", createTypeObject(myClassSym))
    );
    myClassConstructorSym.setIsConstructor(true);

    OOTypeSymbol myInterfaceSym = inScope(gs, oOtype("MyInterface"));
    myInterfaceSym.setIsInterface(true);

    OOTypeSymbol myFunctionalInterfaceSym = inScope(gs,
        oOtype("MyFunctionalInterface")
    );
    myFunctionalInterfaceSym.setIsInterface(true);
    MethodSymbol myFunctionalInterfaceApplySym =
        inScope(myFunctionalInterfaceSym.getSpannedScope(),
            method("apply", _intSymType)
        );
    myFunctionalInterfaceApplySym.setIsAbstract(true);
    MethodSymbol myFunctionalInterfaceGetNameSym =
        inScope(myFunctionalInterfaceSym.getSpannedScope(),
            method("getName", _unboxedString)
        );

    OOTypeSymbol myFunctionalInterfaceSub = inScope(gs, oOtype(
        "MyFunctionalInterfaceSub",
        List.of(createTypeObject(myFunctionalInterfaceSym))
    ));
    myFunctionalInterfaceSub.setIsInterface(true);

    OOTypeSymbol myNonFunctionalInterfaceSub = inScope(gs, oOtype(
        "MyNonFunctionalInterfaceSub",
        List.of(createTypeObject(myFunctionalInterfaceSym))
    ));
    myNonFunctionalInterfaceSub.setIsInterface(true);
    MethodSymbol myNonFunctionalInterfaceApply = inScope(
        myNonFunctionalInterfaceSub.getSpannedScope(),
        method("apply", _intSymType)
    );

    OOTypeSymbol myEnumSym = inScope(gs, oOtype("MyEnum"));
    myEnumSym.setIsEnum(true);
    FieldSymbol enumVal1Sym = inScope(myEnumSym.getSpannedScope(),
        field("ENUM_VAL1", createTypeObject(myEnumSym))
    );
    enumVal1Sym.setIsStatic(true);
    enumVal1Sym.setIsFinal(true);
    enumVal1Sym.setIsEnumConstant(true);
    FieldSymbol enumVal2Sym = inScope(myEnumSym.getSpannedScope(),
        field("ENUM_VAL2", createTypeObject(myEnumSym))
    );
    enumVal2Sym.setIsStatic(true);
    enumVal2Sym.setIsFinal(true);
    enumVal2Sym.setIsEnumConstant(true);
    FieldSymbol enumDefaultSym = inScope(myEnumSym.getSpannedScope(),
        field("ENUM_DEFAULT", createTypeObject(myEnumSym))
    );
    enumDefaultSym.setIsStatic(true);
    enumDefaultSym.setIsFinal(true);

    myClassType = getTypeOfMCType("MyClass");
    myClassConstructorType = createFunction(myClassConstructorSym);
    myClassGetNameType = createFunction(myClassGetNameSym);
    myInterfaceType = getTypeOfMCType("MyInterface");
    myFunctionalInterfaceType = createTypeObject(myFunctionalInterfaceSym);
    myFunctionalInterfaceApplyType = createFunction(myFunctionalInterfaceApplySym);
    myFunctionalInterfaceSubType = getTypeOfMCType("MyFunctionalInterfaceSub");
    myNonFunctionalInterfaceSubType = getTypeOfMCType("MyNonFunctionalInterfaceSub");
    myEnumType = getTypeOfMCType("MyEnum");
    myEnumVal1Type = getTypeOfExpr("MyEnum.ENUM_VAL1");
    myEnumVal2Type = getTypeOfExpr("MyEnum.ENUM_VAL2");
    myEnumDefaultType = getTypeOfExpr("MyEnum.ENUM_DEFAULT");
    assertNoFindings();
  }

  @Test
  public void recognizeClasses() {
    assertFalse(OOSymbolsSymTypeRelations.isClass(_intSymType));
    assertFalse(OOSymbolsSymTypeRelations.isClass(_unboxedString));
    assertTrue(OOSymbolsSymTypeRelations.isClass(myClassType));
    assertFalse(OOSymbolsSymTypeRelations.isClass(myInterfaceType));
    assertFalse(OOSymbolsSymTypeRelations.isClass(myEnumType));
  }

  @Test
  public void recognizeInterfaces() {
    assertFalse(OOSymbolsSymTypeRelations.isInterface(_intSymType));
    assertFalse(OOSymbolsSymTypeRelations.isInterface(_unboxedString));
    assertFalse(OOSymbolsSymTypeRelations.isInterface(myClassType));
    assertTrue(OOSymbolsSymTypeRelations.isInterface(myInterfaceType));
    assertFalse(OOSymbolsSymTypeRelations.isInterface(myEnumType));
  }

  @Test
  public void recognizeEnums() {
    assertFalse(OOSymbolsSymTypeRelations.isEnum(_intSymType));
    assertFalse(OOSymbolsSymTypeRelations.isEnum(_unboxedString));
    assertFalse(OOSymbolsSymTypeRelations.isEnum(myClassType));
    assertFalse(OOSymbolsSymTypeRelations.isEnum(myInterfaceType));
    assertTrue(OOSymbolsSymTypeRelations.isEnum(myEnumType));
  }

  @Test
  public void recognizeMethods() {
    assertFalse(OOSymbolsSymTypeRelations.isMethod(_intSymType));
    assertFalse(OOSymbolsSymTypeRelations.isMethod(_unboxedString));
    assertFalse(OOSymbolsSymTypeRelations.isMethod(myFunctionalInterfaceType));
    assertFalse(OOSymbolsSymTypeRelations.isMethod(
        createFunction(_intSymType)
    ));
    assertTrue(OOSymbolsSymTypeRelations.isMethod(myClassGetNameType));
    assertFalse(OOSymbolsSymTypeRelations.isMethod(myClassConstructorType));
  }

  @Test
  public void recognizeConstructor() {
    assertFalse(OOSymbolsSymTypeRelations.isConstructor(_intSymType));
    assertFalse(OOSymbolsSymTypeRelations.isConstructor(_unboxedString));
    assertFalse(OOSymbolsSymTypeRelations.isConstructor(myFunctionalInterfaceType));
    assertFalse(OOSymbolsSymTypeRelations.isConstructor(
        createFunction(_intSymType)
    ));
    assertFalse(OOSymbolsSymTypeRelations.isConstructor(myClassGetNameType));
    assertTrue(OOSymbolsSymTypeRelations.isConstructor(myClassConstructorType));
  }

  @Test
  public void recognizeEnumConstant() {
    assertFalse(OOSymbolsSymTypeRelations.sourceIsEnumConstant(_intSymType));
    assertFalse(OOSymbolsSymTypeRelations.sourceIsEnumConstant(_unboxedString));
    assertFalse(OOSymbolsSymTypeRelations.sourceIsEnumConstant(myEnumType));
    assertTrue(OOSymbolsSymTypeRelations.sourceIsEnumConstant(myEnumVal1Type));
    assertFalse(OOSymbolsSymTypeRelations.sourceIsEnumConstant(myEnumDefaultType));
  }

  @Test
  public void recognizeFunctionalInterface() {
    Optional<SymTypeOfFunction> abstractMethod;
    assertFalse(OOSymbolsSymTypeRelations
        .getAbstractFunctionOfFunctionalInterFace(_intSymType)
        .isPresent()
    );
    assertFalse(OOSymbolsSymTypeRelations
        .getAbstractFunctionOfFunctionalInterFace(_unboxedString)
        .isPresent()
    );
    assertFalse(OOSymbolsSymTypeRelations
        .getAbstractFunctionOfFunctionalInterFace(myInterfaceType)
        .isPresent()
    );
    assertFalse(OOSymbolsSymTypeRelations
        .getAbstractFunctionOfFunctionalInterFace(myFunctionalInterfaceApplyType)
        .isPresent()
    );
    abstractMethod = OOSymbolsSymTypeRelations
        .getAbstractFunctionOfFunctionalInterFace(myFunctionalInterfaceType);
    assertTrue(abstractMethod.isPresent());
    assertSame(
        abstractMethod.get().getSymbol(),
        myFunctionalInterfaceApplyType.asFunctionType().getSymbol()
    );
    abstractMethod = OOSymbolsSymTypeRelations
        .getAbstractFunctionOfFunctionalInterFace(myFunctionalInterfaceSubType);
    assertTrue(abstractMethod.isPresent());
    assertSame(
        abstractMethod.get().getSymbol(),
        myFunctionalInterfaceApplyType.asFunctionType().getSymbol()
    );
    assertFalse(OOSymbolsSymTypeRelations
        .getAbstractFunctionOfFunctionalInterFace(myNonFunctionalInterfaceSubType)
        .isPresent()
    );
  }

}
