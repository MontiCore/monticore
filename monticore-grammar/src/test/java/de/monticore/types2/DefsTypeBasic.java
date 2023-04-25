/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypePrimitive;

import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;

/**
 * DefsTypeBasic offers one Symbol-Infrastructure
 * including Scopes etc. that is used to provide relevant Symbols.
 * This infrastructure can be used for testing
 */
public class DefsTypeBasic {

  /**
   * Initialization of the structure (can be called again to reinitialize).
   */
  public static void setup() {
    set_thePrimitives();
    set_boxedPrimitives();
    set_unboxedObjects();
    set_boxedObjects();
    set_unboxedCollections();
    set_boxedCollections();
    set_objectTypes();
    set_generics();
  }

  /*********************************************************************/

  /*
   * Helpers that efficiently create Scopes
   */
  public static IBasicSymbolsScope scope(String name) {
    return scope(name, true);
  }

  public static IBasicSymbolsScope scope(String name, boolean shadowing) {
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();
    scope.setName(name);
    scope.setShadowing(shadowing);
    scope.setExportingSymbols(true);
    return scope;
  }

  /*********************************************************************/

  /**
   * add a Scope to a Scope (bidirectional)
   */
  public static IBasicSymbolsScope inScope(IBasicSymbolsScope p, IBasicSymbolsScope s) {
    p.addSubScope(s);
    return s;
  }

  /**
   * add a Type to a Scope (bidirectional)
   */
  public static TypeSymbol inScope(IBasicSymbolsScope p, TypeSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
    return s;
  }

  /**
   * add a Variable to a Scope (bidirectional)
   */
  public static VariableSymbol inScope(IBasicSymbolsScope p, VariableSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
    return s;
  }

  /**
   * add a Function to a Scope (bidirectional)
   */
  public static FunctionSymbol inScope(IBasicSymbolsScope p, FunctionSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
    return s;
  }

  /**
   * add a TypeVariable to a Scope (bidirectional)
   */
  public static TypeVarSymbol inScope(IBasicSymbolsScope p, TypeVarSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
    return s;
  }

  /*********************************************************************/

  /*
   * Helpers that efficiently create Symbols
   * (which by the way can also later be extended)
   */

  /**
   * create TypeSymbols (some defaults apply)
   */
  public static TypeSymbol type(String name) {
    return BasicSymbolsMill.typeSymbolBuilder()
        .setSpannedScope(BasicSymbolsMill.scope())
        .setName(name)
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build();
  }

  public static TypeSymbol type(String name,
      List<SymTypeExpression> superTypeList) {
    TypeSymbol ts = type(name);
    ts.setSuperTypesList(superTypeList);
    return ts;
  }

  public static TypeSymbol type(String name,
      List<SymTypeExpression> superTypeList,
      List<TypeVarSymbol> typeVariableList) {
    TypeSymbol ts = type(name, superTypeList);
    typeVariableList.forEach(ts::addTypeVarSymbol);
    return ts;
  }

  public static TypeSymbol type(String name,
      List<SymTypeExpression> superTypeList,
      List<TypeVarSymbol> typeVariableList,
      List<FunctionSymbol> functionList,
      List<VariableSymbol> variableList) {
    TypeSymbol ts = type(name, superTypeList, typeVariableList);
    functionList.forEach(ts::addFunctionSymbol);
    variableList.forEach(ts::addVariableSymbol);
    return ts;
  }

  /**
   * create TypeVariableSymbols (some defaults apply)
   */
  public static TypeVarSymbol typeVariable(String name) {
    return BasicSymbolsMill.typeVarSymbolBuilder()
        .setName(name)
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();
  }

  public static TypeSymbol add(TypeSymbol t, VariableSymbol f) {
    t.addVariableSymbol(f);
    return t;
  }

  public static TypeSymbol add(TypeSymbol t, FunctionSymbol m) {
    t.addFunctionSymbol(m);
    return t;
  }

  /**
   * create FunctionSymbols (some defaults apply)
   */
  public static FunctionSymbol function(String name, SymTypeExpression returnType) {
    return BasicSymbolsMill.functionSymbolBuilder()
        .setSpannedScope(BasicSymbolsMill.scope())
        .setName(name)
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setType(returnType)
        .build();
  }

  public static FunctionSymbol add(FunctionSymbol f, VariableSymbol v) {
    f.getSpannedScope().add(v);
    return f;
  }

  /**
   * create VariableSymbols (some defaults apply)
   */
  public static VariableSymbol variable(String name, SymTypeExpression type) {
    return BasicSymbolsMill.variableSymbolBuilder()
        .setName(name)
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setType(type)
        .build();
  }

  /*********************************************************************/

  /*
   * This is the predefined Symbol for all Primitives, such as "int"
   * which has empty Variables and Functions
   */

  public static SymTypePrimitive _intSymType;

  public static SymTypePrimitive _charSymType;

  public static SymTypePrimitive _booleanSymType;

  public static SymTypePrimitive _doubleSymType;

  public static SymTypePrimitive _floatSymType;

  public static SymTypePrimitive _longSymType;

  public static SymTypePrimitive _byteSymType;

  public static SymTypePrimitive _shortSymType;

  public static void set_thePrimitives() {
    IBasicSymbolsGlobalScope typeSymbolsScope = BasicSymbolsMill.globalScope();
    if (typeSymbolsScope.resolveType(BasicSymbolsMill.INT).isEmpty()) {
      BasicSymbolsMill.initializePrimitives();
    }
    _intSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.INT).get());
    _charSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.CHAR).get());
    _booleanSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.BOOLEAN).get());
    _doubleSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.DOUBLE).get());
    _floatSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.FLOAT).get());
    _longSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.LONG).get());
    _byteSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.BYTE).get());
    _shortSymType = createPrimitive(
        typeSymbolsScope.resolveType(BasicSymbolsMill.SHORT).get());
  }

  /*********************************************************************/

  /*
   * These are the predefined Symbol for boxed Primitives, such as "Integer"
   */

  public static SymTypeOfObject _IntegerSymType;

  public static SymTypeOfObject _CharacterSymType;

  public static SymTypeOfObject _BooleanSymType;

  public static SymTypeOfObject _DoubleSymType;

  public static SymTypeOfObject _FloatSymType;

  public static SymTypeOfObject _LongSymType;

  public static SymTypeOfObject _ByteSymType;

  public static SymTypeOfObject _ShortSymType;

  public static void set_boxedPrimitives() {
    // add java.lang scope
    IBasicSymbolsScope javaScope = scope("java");
    IBasicSymbolsScope langScope = inScope(javaScope, scope("lang"));
    BasicSymbolsMill.globalScope().addSubScope(javaScope);
    // create boxed primitives
    _IntegerSymType =
        createTypeObject(inScope(langScope, type("Integer")));
    _CharacterSymType =
        createTypeObject(inScope(langScope, type("Character")));
    _BooleanSymType =
        createTypeObject(inScope(langScope, type("Boolean")));
    _DoubleSymType =
        createTypeObject(inScope(langScope, type("Double")));
    _FloatSymType =
        createTypeObject(inScope(langScope, type("Float")));
    _LongSymType =
        createTypeObject(inScope(langScope, type("Long")));
    _ByteSymType =
        createTypeObject(inScope(langScope, type("Byte")));
    _ShortSymType =
        createTypeObject(inScope(langScope, type("Short")));
  }

  /*********************************************************************/

  /*
   * These are the predefined Symbol for unboxed Objects like "String"
   */

  public static SymTypeOfObject _unboxedString;

  public static void set_unboxedObjects() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    _unboxedString = createTypeObject(inScope(gs, type("String")));
  }

  /*********************************************************************/

  /*
   * These are the predefined Symbol for boxed Objects like "String"
   */

  public static SymTypeOfObject _boxedString;

  public static void set_boxedObjects() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    IBasicSymbolsScope javaScope = inScope(gs, scope("java"));
    IBasicSymbolsScope langScope = inScope(javaScope, scope("lang"));
    _boxedString = createTypeObject(inScope(langScope, type("String")));
  }

  /*********************************************************************/

  /*
   * These are the predefined Symbol for unboxed Collections like "List"
   */

  public static SymTypeOfGenerics _unboxedOptionalSymType;

  public static SymTypeOfGenerics _unboxedSetSymType;

  public static SymTypeOfGenerics _unboxedListSymType;

  public static SymTypeOfGenerics _unboxedMapSymType;

  public static void set_unboxedCollections() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    TypeVarSymbol optVar = typeVariable("T");
    _unboxedOptionalSymType = createGenerics(
        inScope(gs, type("Optional", List.of(), List.of(optVar))),
        createTypeVariable(optVar)
    );
    TypeVarSymbol setVar = typeVariable("T");
    _unboxedSetSymType = createGenerics(
        inScope(gs, type("Set", List.of(), List.of(setVar))),
        createTypeVariable(setVar)
    );
    TypeVarSymbol listVar = typeVariable("T");
    _unboxedListSymType = createGenerics(
        inScope(gs, type("List", List.of(), List.of(listVar))),
        createTypeVariable(listVar)
    );
    TypeVarSymbol mapVar1 = typeVariable("T");
    TypeVarSymbol mapVar2 = typeVariable("U");
    _unboxedMapSymType = createGenerics(
        inScope(gs, type("Map", List.of(), List.of(mapVar1, mapVar2))),
        createTypeVariable(mapVar1), createTypeVariable(mapVar2)
    );
  }

  /*********************************************************************/

  /*
   * These are the predefined Symbol for unboxed Collections like "List"
   */

  public static SymTypeOfGenerics _boxedOptionalSymType;

  public static SymTypeOfGenerics _boxedSetSymType;

  public static SymTypeOfGenerics _boxedListSymType;

  public static SymTypeOfGenerics _boxedMapSymType;

  public static void set_boxedCollections() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    IBasicSymbolsScope javaScope = inScope(gs, scope("java"));
    IBasicSymbolsScope utilScope = inScope(javaScope, scope("util"));
    TypeVarSymbol optVar = typeVariable("T");
    _boxedOptionalSymType = createGenerics(
        inScope(utilScope, type("Optional", List.of(), List.of(optVar))),
        createTypeVariable(optVar)
    );
    TypeVarSymbol setVar = typeVariable("T");
    _boxedSetSymType = createGenerics(
        inScope(utilScope, type("Set", List.of(), List.of(setVar))),
        createTypeVariable(setVar)
    );
    TypeVarSymbol listVar = typeVariable("T");
    _boxedListSymType = createGenerics(
        inScope(utilScope, type("List", List.of(), List.of(listVar))),
        createTypeVariable(listVar)
    );
    TypeVarSymbol mapVar1 = typeVariable("T");
    TypeVarSymbol mapVar2 = typeVariable("U");
    _boxedMapSymType = createGenerics(
        inScope(utilScope, type("Map", List.of(), List.of(mapVar1, mapVar2))),
        createTypeVariable(mapVar1), createTypeVariable(mapVar2)
    );
  }

  /*********************************************************************/

  /*
   * These are some predefined Symbols for Object Types
   */

  public static SymTypeOfObject _personSymType;

  public static SymTypeOfObject _teachableSymType;

  // student is a subtype of person, teachable
  public static SymTypeOfObject _studentSymType;

  // computer science student is a subtype of student;
  public static SymTypeOfObject _csStudentSymType;

  // child is a subtype of person, teachable
  public static SymTypeOfObject _childSymType;

  // teacher is a subtype of person
  public static SymTypeOfObject _teacherSymType;

  public static SymTypeOfObject _carSymType;

  public static SymTypeOfObject _schoolSymType;

  public static void set_objectTypes() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    _personSymType = createTypeObject(inScope(gs, type("Person")));
    _teachableSymType = createTypeObject(inScope(gs, type("Teachable")));
    _studentSymType = createTypeObject(inScope(gs,
        type("Student", List.of(_personSymType, _teachableSymType)))
    );
    _csStudentSymType = createTypeObject(inScope(gs,
        type("CsStudent", List.of(_studentSymType)))
    );
    _childSymType = createTypeObject(inScope(gs,
        type("Child", List.of(_personSymType, _teachableSymType)))
    );
    _teacherSymType = createTypeObject(inScope(gs,
        type("Teacher", List.of(_personSymType)))
    );
    _carSymType = createTypeObject(inScope(gs, type("Car")));
    _schoolSymType = createTypeObject(inScope(gs, type("School")));
  }

  /*********************************************************************/

  /*
   * These are the predefined Symbol for further generics
   * in most cases, the collections ought to be enough
   */

  public static SymTypeOfGenerics _linkedListSymType;

  public static void set_generics() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    TypeVarSymbol listVar = typeVariable("T");
    _linkedListSymType = createGenerics(
        inScope(gs, type("LinkedList",
            List.of(createGenerics(_boxedListSymType.getTypeInfo(),
                createTypeVariable(listVar))),
            List.of(listVar))),
        createTypeVariable(listVar)
    );
  }

}
