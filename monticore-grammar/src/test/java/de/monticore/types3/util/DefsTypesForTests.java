/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SIUnitBasic;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.check.SymTypeVoid;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createBottomType;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnit;
import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnitBasic;
import static de.monticore.types.check.SymTypeExpressionFactory.createTopType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;

/**
 * offers one Symbol-Infrastructure
 * including Scopes etc. that is used to provide relevant Symbols.
 * This infrastructure can be used for testing
 */
public class DefsTypesForTests {

  /**
   * Initialization of the structure (can be called again to reinitialize).
   */
  public static void setup() {
    set_specialSymTypes();
    set_thePrimitives();
    set_boxedPrimitives();
    set_unboxedObjects();
    set_boxedObjects();
    set_unboxedCollections();
    set_boxedCollections();
    set_genericsRecursive();
    set_objectTypes();
    set_generics();
    set_bottomTopTypes();
    set_siUnitBasic();
    set_siUnitTypes();
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
  public static <T extends IBasicSymbolsScope> T inScope(IBasicSymbolsScope p, T s) {
    p.addSubScope(s);
    return s;
  }

  /**
   * add a Type to a Scope (bidirectional)
   */
  public static TypeSymbol inScope(IBasicSymbolsScope p, TypeSymbol s) {
    s.setEnclosingScope(p);
    s.getSpannedScope().setEnclosingScope(p);
    p.add(s);
    p.addSubScope(s.getSpannedScope());
    return s;
  }

  public static OOTypeSymbol inScope(IOOSymbolsScope p, OOTypeSymbol s) {
    s.setEnclosingScope(p);
    s.getSpannedScope().setEnclosingScope(p);
    p.add(s);
    p.addSubScope(s.getSpannedScope());
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

  public static FieldSymbol inScope(IOOSymbolsScope p, FieldSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
    return s;
  }

  /**
   * add a Function to a Scope (bidirectional)
   */
  public static FunctionSymbol inScope(IBasicSymbolsScope p, FunctionSymbol s) {
    s.setEnclosingScope(p);
    s.getSpannedScope().setEnclosingScope(p);
    p.add(s);
    p.addSubScope(s.getSpannedScope());
    return s;
  }

  public static MethodSymbol inScope(IOOSymbolsScope p, MethodSymbol s) {
    s.setEnclosingScope(p);
    s.getSpannedScope().setEnclosingScope(p);
    p.add(s);
    p.addSubScope(s.getSpannedScope());
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

  // create TypeSymbols (some defaults apply)
  public static TypeSymbol type(String name) {
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();
    scope.setShadowing(true);
    return BasicSymbolsMill.typeSymbolBuilder()
        .setSpannedScope(scope)
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
    typeVariableList.forEach(tv -> inScope(ts.getSpannedScope(), tv));
    return ts;
  }

  public static TypeSymbol type(String name,
      List<SymTypeExpression> superTypeList,
      List<TypeVarSymbol> typeVariableList,
      List<FunctionSymbol> functionList,
      List<VariableSymbol> variableList) {
    TypeSymbol ts = type(name, superTypeList, typeVariableList);
    functionList.forEach(fs -> inScope(ts.getSpannedScope(), fs));
    variableList.forEach(vs -> inScope(ts.getSpannedScope(), vs));
    return ts;
  }

  // create OOTypeSymbols (some defaults apply)

  public static OOTypeSymbol oOtype(String name) {
    IOOSymbolsScope scope = OOSymbolsMill.scope();
    scope.setShadowing(true);
    return OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(scope)
        .setName(name)
        .setIsPublic(true)
        .setIsStatic(true)
        .build();
  }

  public static OOTypeSymbol oOtype(String name,
      List<SymTypeExpression> superTypeList) {
    OOTypeSymbol ts = oOtype(name);
    ts.setSuperTypesList(superTypeList);
    return ts;
  }

  public static OOTypeSymbol oOtype(String name,
      List<SymTypeExpression> superTypeList,
      List<TypeVarSymbol> typeVariableList) {
    OOTypeSymbol ts = oOtype(name, superTypeList);
    typeVariableList.forEach(tv -> inScope(ts.getSpannedScope(), tv));
    return ts;
  }

  public static OOTypeSymbol oOtype(String name,
      List<SymTypeExpression> superTypeList,
      List<TypeVarSymbol> typeVariableList,
      List<MethodSymbol> methodList,
      List<FieldSymbol> fieldList) {
    OOTypeSymbol ts = oOtype(name, superTypeList, typeVariableList);
    methodList.forEach(fs -> inScope(ts.getSpannedScope(), fs));
    fieldList.forEach(vs -> inScope(ts.getSpannedScope(), vs));
    return ts;
  }

  // create TypeVarSymbols (some defaults apply)

  public static TypeVarSymbol typeVariable(String name) {
    return typeVariable(name, new ArrayList<>());
  }

  public static TypeVarSymbol typeVariable(String name,
      List<SymTypeExpression> superTypeList) {
    return BasicSymbolsMill.typeVarSymbolBuilder()
        .setName(name)
        .setSuperTypesList(superTypeList)
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();
  }

  // create FunctionSymbols (some defaults apply)

  public static FunctionSymbol function(String name, SymTypeExpression returnType,
      SymTypeExpression... argumentTypes) {
    return function(name, returnType, List.of(argumentTypes));
  }

  public static FunctionSymbol function(String name, SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes) {
    return function(name, returnType, argumentTypes, false);
  }

  public static FunctionSymbol function(String name, SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes, boolean elliptic) {
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();
    scope.setOrdered(true);
    scope.setShadowing(true);
    for (int i = 0; i < argumentTypes.size(); i++) {
      scope.add(
          BasicSymbolsMill.variableSymbolBuilder()
              .setType(argumentTypes.get(i))
              .setName("arg" + i)
              .build()
      );
    }
    return BasicSymbolsMill.functionSymbolBuilder()
        .setSpannedScope(scope)
        .setName(name)
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setType(returnType)
        .setIsElliptic(elliptic)
        .build();
  }

  // create MethodSymbols (some defaults apply)

  public static MethodSymbol method(String name, SymTypeExpression returnType,
      SymTypeExpression... argumentTypes) {
    return method(name, returnType, List.of(argumentTypes));
  }

  public static MethodSymbol method(String name, SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes) {
    return method(name, returnType, argumentTypes, false);
  }

  public static MethodSymbol method(String name, SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes, boolean elliptic) {
    IOOSymbolsScope scope = OOSymbolsMill.scope();
    scope.setOrdered(true);
    scope.setShadowing(true);
    for (int i = 0; i < argumentTypes.size(); i++) {
      scope.add(
          OOSymbolsMill.fieldSymbolBuilder()
              .setType(argumentTypes.get(i))
              .setName("arg" + i)
              .build()
      );
    }
    return OOSymbolsMill.methodSymbolBuilder()
        .setSpannedScope(scope)
        .setName(name)
        .setType(returnType)
        .setIsElliptic(elliptic)
        .setIsPublic(true)
        .setIsStatic(false)
        .build();
  }

  // create VariableSymbols (some defaults apply)

  public static VariableSymbol variable(String name, SymTypeExpression type) {
    return BasicSymbolsMill.variableSymbolBuilder()
        .setName(name)
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setType(type)
        .build();
  }

  // create FieldSymbols (some defaults apply)

  public static FieldSymbol field(String name, SymTypeExpression type) {
    return OOSymbolsMill.fieldSymbolBuilder()
        .setName(name)
        .setType(type)
        .setIsPublic(true)
        .setIsStatic(false)
        .build();
  }

  /*********************************************************************/

  /*
   * This is the predefined Symbol for 'special' types like void
   * these are usually the ones with a fixed amount of values
   */

  public static SymTypeOfNull _nullSymType;

  public static SymTypeVoid _voidSymType;

  public static SymTypeObscure _obscureSymType;

  public static void set_specialSymTypes() {
    _nullSymType = SymTypeExpressionFactory.createTypeOfNull();
    _voidSymType = SymTypeExpressionFactory.createTypeVoid();
    _obscureSymType = SymTypeExpressionFactory.createObscureType();
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
    IBasicSymbolsGlobalScope typeSymbolsScope = BasicSymbolsMill.globalScope();
    if (typeSymbolsScope.resolveType(BasicSymbolsMill.STRING).isEmpty()) {
      BasicSymbolsMill.initializeString();
    }
    _unboxedString = createTypeObject(
        typeSymbolsScope.resolveType(BasicSymbolsMill.STRING).get());
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
   * They have separate setup function as different languages (s. OCL)
   * have their own version of some of them
   */

  public static SymTypeOfGenerics _unboxedOptionalSymType;

  public static SymTypeOfGenerics _unboxedSetSymType;

  public static SymTypeOfGenerics _unboxedListSymType;

  public static SymTypeOfGenerics _unboxedMapSymType;

  public static void set_unboxedCollections() {
    set_unboxedOptionalSymType();
    set_unboxedSetSymType();
    set_unboxedListSymType();
    set_unboxedMapSymType();
  }

  public static void set_unboxedOptionalSymType() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    TypeVarSymbol optVar = typeVariable("T");
    _unboxedOptionalSymType = createGenerics(
        inScope(gs, type("Optional", List.of(), List.of(optVar))),
        createTypeVariable(optVar)
    );
  }

  public static void set_unboxedSetSymType() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    TypeVarSymbol setVar = typeVariable("T");
    _unboxedSetSymType = createGenerics(
        inScope(gs, type("Set", List.of(), List.of(setVar))),
        createTypeVariable(setVar)
    );
  }

  public static void set_unboxedListSymType() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    TypeVarSymbol listVar = typeVariable("T");
    _unboxedListSymType = createGenerics(
        inScope(gs, type("List", List.of(), List.of(listVar))),
        createTypeVariable(listVar)
    );
  }

  public static void set_unboxedMapSymType() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
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
    TypeVarSymbol optVar = typeVariable("OptT");
    _boxedOptionalSymType = createGenerics(
        inScope(utilScope, type("Optional", List.of(), List.of(optVar))),
        createTypeVariable(optVar)
    );
    TypeVarSymbol setVar = typeVariable("SetT");
    _boxedSetSymType = createGenerics(
        inScope(utilScope, type("Set", List.of(), List.of(setVar))),
        createTypeVariable(setVar)
    );
    TypeVarSymbol listVar = typeVariable("ListT");
    _boxedListSymType = createGenerics(
        inScope(utilScope, type("List", List.of(), List.of(listVar))),
        createTypeVariable(listVar)
    );
    TypeVarSymbol mapVar1 = typeVariable("KeyT");
    TypeVarSymbol mapVar2 = typeVariable("ValueT");
    _boxedMapSymType = createGenerics(
        inScope(utilScope, type("Map", List.of(), List.of(mapVar1, mapVar2))),
        createTypeVariable(mapVar1), createTypeVariable(mapVar2)
    );
  }

  /*********************************************************************/

  /*
   * These are some predefined Symbols for recursively defined generic types
   */

  /**
   * s. simple curiously recurring template pattern
   */
  public static SymTypeOfGenerics _simpleCrtSymType;

  // Graph example out of Wild FJ (2015)
  //class Node <N extends Node<N,E>, E extends Edge<N,E>>
  //class Edge <N extends Node<N,E>, E extends Edge<N,E>>
  //class Graph<N extends Node<N,E>, E extends Edge<N,E>>

  public static SymTypeOfGenerics _graphNodeSymType;

  public static SymTypeOfGenerics _graphEdgeSymType;

  public static SymTypeOfGenerics _graphSymType;

  public static void set_genericsRecursive() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // SimpleCrt<CrtT extends SimpleCrt<CrtT>>
    TypeVarSymbol crtVar = typeVariable("CrtT");
    _simpleCrtSymType = createGenerics(
        inScope(gs, type("SimpleCrt", List.of(), List.of(crtVar))),
        createTypeVariable(crtVar)
    );
    crtVar.addSuperTypes(_simpleCrtSymType);

    // Graph example out of Wild FJ (2015)
    TypeSymbol nodeSymbol = inScope(gs, type("Node"));
    TypeSymbol edgeSymbol = inScope(gs, type("Edge"));
    TypeSymbol graphSymbol = inScope(gs, type("Graph"));
    //class Node <N extends Node<N,E>, E extends Edge<N,E>>
    SymTypeVariable nodeNodeVar = createTypeVariable(typeVariable("NodeN"));
    SymTypeVariable nodeEdgeVar = createTypeVariable(typeVariable("NodeE"));
    nodeSymbol.getSpannedScope().add(nodeNodeVar.getTypeVarSymbol());
    nodeSymbol.getSpannedScope().add(nodeEdgeVar.getTypeVarSymbol());
    nodeNodeVar.getTypeVarSymbol().addSuperTypes(createGenerics(
        nodeSymbol, List.of(nodeNodeVar, nodeEdgeVar)
    ));
    nodeEdgeVar.getTypeVarSymbol().addSuperTypes(createGenerics(
        edgeSymbol, List.of(nodeNodeVar, nodeEdgeVar)
    ));
    _graphNodeSymType = createGenerics(nodeSymbol, nodeNodeVar, nodeEdgeVar);
    //class Edge <N extends Node<N,E>, E extends Edge<N,E>>
    SymTypeVariable edgeNodeVar = createTypeVariable(typeVariable("EdgeN"));
    SymTypeVariable edgeEdgeVar = createTypeVariable(typeVariable("EdgeE"));
    edgeSymbol.getSpannedScope().add(edgeNodeVar.getTypeVarSymbol());
    edgeSymbol.getSpannedScope().add(edgeEdgeVar.getTypeVarSymbol());
    edgeNodeVar.getTypeVarSymbol().addSuperTypes(createGenerics(
        nodeSymbol, List.of(edgeNodeVar, edgeEdgeVar)
    ));
    edgeEdgeVar.getTypeVarSymbol().addSuperTypes(createGenerics(
        edgeSymbol, List.of(edgeNodeVar, edgeEdgeVar)
    ));
    _graphEdgeSymType = createGenerics(edgeSymbol, edgeNodeVar, edgeEdgeVar);
    //class Graph<N extends Node<N,E>, E extends Edge<N,E>>
    SymTypeVariable graphNodeVar = createTypeVariable(typeVariable("GraphN"));
    SymTypeVariable graphEdgeVar = createTypeVariable(typeVariable("GraphE"));
    graphSymbol.getSpannedScope().add(graphNodeVar.getTypeVarSymbol());
    graphSymbol.getSpannedScope().add(graphEdgeVar.getTypeVarSymbol());
    graphNodeVar.getTypeVarSymbol().addSuperTypes(createGenerics(
        nodeSymbol, List.of(graphNodeVar, graphEdgeVar)
    ));
    graphEdgeVar.getTypeVarSymbol().addSuperTypes(createGenerics(
        edgeSymbol, List.of(graphNodeVar, graphEdgeVar)
    ));
    _graphSymType = createGenerics(graphSymbol, graphNodeVar, graphEdgeVar);
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

  // first semester computer science student
  // is a subType of computer science student
  public static SymTypeOfObject _firstSemesterCsStudentSymType;

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
    _firstSemesterCsStudentSymType = createTypeObject(inScope(gs,
        type("FirstSemesterCsStudent", List.of(_csStudentSymType)))
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

  public static SymTypeOfGenerics _hashMapSymType;

  public static void set_generics() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    TypeVarSymbol listVar = typeVariable("LinkedListT");
    _linkedListSymType = createGenerics(
        inScope(gs, type("LinkedList",
            List.of(createGenerics(_boxedListSymType.getTypeInfo(),
                createTypeVariable(listVar))),
            List.of(listVar)
        ))
    );
    TypeVarSymbol mapKVar = typeVariable("HashMapK");
    TypeVarSymbol mapVVar = typeVariable("HashMapV");
    _hashMapSymType = createGenerics(
        inScope(gs, type("HashMap",
            List.of(createGenerics(
                _boxedMapSymType.getTypeInfo(),
                createTypeVariable(mapKVar),
                createTypeVariable(mapVVar))
            ),
            List.of(mapKVar, mapVVar))
        )
    );
  }

  /*********************************************************************/

  /*
   * These are predefined symbols for bottom and top types
   */

  public static SymTypeExpression _bottomType;

  public static SymTypeExpression _topType;

  public static void set_bottomTopTypes() {
    _bottomType = createBottomType();
    _topType = createTopType();
  }

  /*********************************************************************/

  /*
   * These are SIUnitBasics to create SymTypeOfSIUnits
   */

  // The seven base units (kg with and without prefix k)
  public static SIUnitBasic _s_SIUnitBasic;
  public static SIUnitBasic _m_SIUnitBasic;
  public static SIUnitBasic _g_SIUnitBasic;
  public static SIUnitBasic _A_SIUnitBasic;
  public static SIUnitBasic _K_SIUnitBasic;
  public static SIUnitBasic _mol_SIUnitBasic;
  public static SIUnitBasic _cd_SIUnitBasic;
  public static SIUnitBasic _kg_SIUnitBasic;
  // Further supported SIUnitBasics
  public static SIUnitBasic _Hz_SIUnitBasic;
  public static SIUnitBasic _N_SIUnitBasic;
  public static SIUnitBasic _Pa_SIUnitBasic;
  public static SIUnitBasic _J_SIUnitBasic;
  public static SIUnitBasic _W_SIUnitBasic;
  public static SIUnitBasic _C_SIUnitBasic;
  public static SIUnitBasic _V_SIUnitBasic;
  public static SIUnitBasic _F_SIUnitBasic;
  public static SIUnitBasic _Ohm_SIUnitBasic;
  public static SIUnitBasic _Ω_SIUnitBasic;
  public static SIUnitBasic _S_SIUnitBasic;
  public static SIUnitBasic _Wb_SIUnitBasic;
  public static SIUnitBasic _T_SIUnitBasic;
  public static SIUnitBasic _H_SIUnitBasic;
  public static SIUnitBasic _lm_SIUnitBasic;
  public static SIUnitBasic _lx_SIUnitBasic;
  public static SIUnitBasic _Bq_SIUnitBasic;
  public static SIUnitBasic _Gy_SIUnitBasic;
  public static SIUnitBasic _Sv_SIUnitBasic;
  public static SIUnitBasic _kat_SIUnitBasic;
  public static SIUnitBasic _l_SIUnitBasic;
  public static SIUnitBasic _L_SIUnitBasic;
  public static SIUnitBasic _min_SIUnitBasic;
  public static SIUnitBasic _h_SIUnitBasic;
  public static SIUnitBasic _d_SIUnitBasic;
  public static SIUnitBasic _ha_SIUnitBasic;
  public static SIUnitBasic _t_SIUnitBasic;
  public static SIUnitBasic _au_SIUnitBasic;
  public static SIUnitBasic _eV_SIUnitBasic;
  public static SIUnitBasic _Da_SIUnitBasic;
  public static SIUnitBasic _u_SIUnitBasic;
  public static SIUnitBasic _ºC_SIUnitBasic;
  public static SIUnitBasic _ªF_SIUnitBasic;
  public static SIUnitBasic _Np_SIUnitBasic;
  public static SIUnitBasic _B_SIUnitBasic;
  public static SIUnitBasic _dB_SIUnitBasic;
  public static SIUnitBasic _degSym_SIUnitBasic;
  public static SIUnitBasic _deg_SIUnitBasic;
  public static SIUnitBasic _rad_SIUnitBasic;
  public static SIUnitBasic _sr_SIUnitBasic;

  public static void set_siUnitBasic() {
    _s_SIUnitBasic = createSIUnitBasic("s");
    _m_SIUnitBasic = createSIUnitBasic("m");
    _g_SIUnitBasic = createSIUnitBasic("g");
    _A_SIUnitBasic = createSIUnitBasic("A");
    _K_SIUnitBasic = createSIUnitBasic("K");
    _mol_SIUnitBasic = createSIUnitBasic("mol");
    _cd_SIUnitBasic = createSIUnitBasic("cd");
    _kg_SIUnitBasic = createSIUnitBasic("g", "k", 1);

    _Hz_SIUnitBasic = createSIUnitBasic("Hz");
    _N_SIUnitBasic = createSIUnitBasic("N");
    _Pa_SIUnitBasic = createSIUnitBasic("Pa");
    _J_SIUnitBasic = createSIUnitBasic("J");
    _W_SIUnitBasic = createSIUnitBasic("W");
    _C_SIUnitBasic = createSIUnitBasic("C");
    _V_SIUnitBasic = createSIUnitBasic("V");
    _F_SIUnitBasic = createSIUnitBasic("F");
    _Ohm_SIUnitBasic = createSIUnitBasic("Ohm");
    _Ω_SIUnitBasic = createSIUnitBasic("Ω");
    _S_SIUnitBasic = createSIUnitBasic("S");
    _Wb_SIUnitBasic = createSIUnitBasic("Wb");
    _T_SIUnitBasic = createSIUnitBasic("T");
    _H_SIUnitBasic = createSIUnitBasic("H");
    _lm_SIUnitBasic = createSIUnitBasic("lm");
    _lx_SIUnitBasic = createSIUnitBasic("lx");
    _Bq_SIUnitBasic = createSIUnitBasic("Bq");
    _Gy_SIUnitBasic = createSIUnitBasic("Gy");
    _Sv_SIUnitBasic = createSIUnitBasic("Sv");
    _kat_SIUnitBasic = createSIUnitBasic("kat");
    _l_SIUnitBasic = createSIUnitBasic("l");
    _L_SIUnitBasic = createSIUnitBasic("L");
    _min_SIUnitBasic = createSIUnitBasic("min");
    _h_SIUnitBasic = createSIUnitBasic("h");
    _d_SIUnitBasic = createSIUnitBasic("d");
    _ha_SIUnitBasic = createSIUnitBasic("ha");
    _t_SIUnitBasic = createSIUnitBasic("t");
    _au_SIUnitBasic = createSIUnitBasic("au");
    _eV_SIUnitBasic = createSIUnitBasic("eV");
    _Da_SIUnitBasic = createSIUnitBasic("Da");
    _u_SIUnitBasic = createSIUnitBasic("u");
    _ºC_SIUnitBasic = createSIUnitBasic("ºC");
    _ªF_SIUnitBasic = createSIUnitBasic("ªF");
    _Np_SIUnitBasic = createSIUnitBasic("Np");
    _B_SIUnitBasic = createSIUnitBasic("B");
    _dB_SIUnitBasic = createSIUnitBasic("dB");
    _degSym_SIUnitBasic = createSIUnitBasic("°");
    _deg_SIUnitBasic = createSIUnitBasic("deg");
    _rad_SIUnitBasic = createSIUnitBasic("rad");
    _sr_SIUnitBasic = createSIUnitBasic("sr");
  }

  /*********************************************************************/

  /*
   * These are simple(!) SymTypeOfSIUnits,
   * one for each supported SIUnitBasic
   *
   * Hint: you may instead want SIUnitIteratorForTests
   */

  // The seven base units (kg with and without prefix k)
  public static SymTypeOfSIUnit _m_SISymType;
  public static SymTypeOfSIUnit _g_SISymType;
  public static SymTypeOfSIUnit _s_SISymType;
  public static SymTypeOfSIUnit _A_SISymType;
  public static SymTypeOfSIUnit _K_SISymType;
  public static SymTypeOfSIUnit _mol_SISymType;
  public static SymTypeOfSIUnit _cd_SISymType;
  public static SymTypeOfSIUnit _kg_SISymType;
  // Further supported SIUnits
  public static SymTypeOfSIUnit _Hz_SISymType;
  public static SymTypeOfSIUnit _N_SISymType;
  public static SymTypeOfSIUnit _Pa_SISymType;
  public static SymTypeOfSIUnit _J_SISymType;
  public static SymTypeOfSIUnit _W_SISymType;
  public static SymTypeOfSIUnit _C_SISymType;
  public static SymTypeOfSIUnit _V_SISymType;
  public static SymTypeOfSIUnit _F_SISymType;
  public static SymTypeOfSIUnit _Ohm_SISymType;
  public static SymTypeOfSIUnit _Ω_SISymType;
  public static SymTypeOfSIUnit _S_SISymType;
  public static SymTypeOfSIUnit _Wb_SISymType;
  public static SymTypeOfSIUnit _T_SISymType;
  public static SymTypeOfSIUnit _H_SISymType;
  public static SymTypeOfSIUnit _lm_SISymType;
  public static SymTypeOfSIUnit _lx_SISymType;
  public static SymTypeOfSIUnit _Bq_SISymType;
  public static SymTypeOfSIUnit _Gy_SISymType;
  public static SymTypeOfSIUnit _Sv_SISymType;
  public static SymTypeOfSIUnit _kat_SISymType;
  public static SymTypeOfSIUnit _l_SISymType;
  public static SymTypeOfSIUnit _L_SISymType;
  public static SymTypeOfSIUnit _min_SISymType;
  public static SymTypeOfSIUnit _h_SISymType;
  public static SymTypeOfSIUnit _d_SISymType;
  public static SymTypeOfSIUnit _ha_SISymType;
  public static SymTypeOfSIUnit _t_SISymType;
  public static SymTypeOfSIUnit _au_SISymType;
  public static SymTypeOfSIUnit _eV_SISymType;
  public static SymTypeOfSIUnit _Da_SISymType;
  public static SymTypeOfSIUnit _u_SISymType;
  public static SymTypeOfSIUnit _ºC_SISymType;
  public static SymTypeOfSIUnit _ªF_SISymType;
  public static SymTypeOfSIUnit _Np_SISymType;
  public static SymTypeOfSIUnit _B_SISymType;
  public static SymTypeOfSIUnit _dB_SISymType;
  public static SymTypeOfSIUnit _degSym_SISymType;
  public static SymTypeOfSIUnit _deg_SISymType;
  public static SymTypeOfSIUnit _rad_SISymType;
  public static SymTypeOfSIUnit _sr_SISymType;

  public static void set_siUnitTypes() {
    _m_SISymType = createSIUnit(List.of(_m_SIUnitBasic), List.of());
    _g_SISymType = createSIUnit(List.of(_g_SIUnitBasic), List.of());
    _s_SISymType = createSIUnit(List.of(_s_SIUnitBasic), List.of());
    _A_SISymType = createSIUnit(List.of(_A_SIUnitBasic), List.of());
    _K_SISymType = createSIUnit(List.of(_K_SIUnitBasic), List.of());
    _mol_SISymType = createSIUnit(List.of(_mol_SIUnitBasic), List.of());
    _cd_SISymType = createSIUnit(List.of(_cd_SIUnitBasic), List.of());
    _kg_SISymType = createSIUnit(List.of(_kg_SIUnitBasic), List.of());

    _Hz_SISymType = createSIUnit(List.of(_Hz_SIUnitBasic), List.of());
    _N_SISymType = createSIUnit(List.of(_N_SIUnitBasic), List.of());
    _Pa_SISymType = createSIUnit(List.of(_Pa_SIUnitBasic), List.of());
    _J_SISymType = createSIUnit(List.of(_J_SIUnitBasic), List.of());
    _W_SISymType = createSIUnit(List.of(_W_SIUnitBasic), List.of());
    _C_SISymType = createSIUnit(List.of(_C_SIUnitBasic), List.of());
    _V_SISymType = createSIUnit(List.of(_V_SIUnitBasic), List.of());
    _F_SISymType = createSIUnit(List.of(_F_SIUnitBasic), List.of());
    _Ohm_SISymType = createSIUnit(List.of(_Ohm_SIUnitBasic), List.of());
    _Ω_SISymType = createSIUnit(List.of(_Ω_SIUnitBasic), List.of());
    _S_SISymType = createSIUnit(List.of(_S_SIUnitBasic), List.of());
    _Wb_SISymType = createSIUnit(List.of(_Wb_SIUnitBasic), List.of());
    _T_SISymType = createSIUnit(List.of(_T_SIUnitBasic), List.of());
    _H_SISymType = createSIUnit(List.of(_H_SIUnitBasic), List.of());
    _lm_SISymType = createSIUnit(List.of(_lm_SIUnitBasic), List.of());
    _lx_SISymType = createSIUnit(List.of(_lx_SIUnitBasic), List.of());
    _Bq_SISymType = createSIUnit(List.of(_Bq_SIUnitBasic), List.of());
    _Gy_SISymType = createSIUnit(List.of(_Gy_SIUnitBasic), List.of());
    _Sv_SISymType = createSIUnit(List.of(_Sv_SIUnitBasic), List.of());
    _kat_SISymType = createSIUnit(List.of(_kat_SIUnitBasic), List.of());
    _l_SISymType = createSIUnit(List.of(_l_SIUnitBasic), List.of());
    _L_SISymType = createSIUnit(List.of(_L_SIUnitBasic), List.of());
    _min_SISymType = createSIUnit(List.of(_min_SIUnitBasic), List.of());
    _h_SISymType = createSIUnit(List.of(_h_SIUnitBasic), List.of());
    _d_SISymType = createSIUnit(List.of(_d_SIUnitBasic), List.of());
    _ha_SISymType = createSIUnit(List.of(_ha_SIUnitBasic), List.of());
    _t_SISymType = createSIUnit(List.of(_t_SIUnitBasic), List.of());
    _au_SISymType = createSIUnit(List.of(_au_SIUnitBasic), List.of());
    _eV_SISymType = createSIUnit(List.of(_eV_SIUnitBasic), List.of());
    _Da_SISymType = createSIUnit(List.of(_Da_SIUnitBasic), List.of());
    _u_SISymType = createSIUnit(List.of(_u_SIUnitBasic), List.of());
    _ºC_SISymType = createSIUnit(List.of(_ºC_SIUnitBasic), List.of());
    _ªF_SISymType = createSIUnit(List.of(_ªF_SIUnitBasic), List.of());
    _Np_SISymType = createSIUnit(List.of(_Np_SIUnitBasic), List.of());
    _B_SISymType = createSIUnit(List.of(_B_SIUnitBasic), List.of());
    _dB_SISymType = createSIUnit(List.of(_dB_SIUnitBasic), List.of());
    _degSym_SISymType = createSIUnit(List.of(_degSym_SIUnitBasic), List.of());
    _deg_SISymType = createSIUnit(List.of(_deg_SIUnitBasic), List.of());
    _rad_SISymType = createSIUnit(List.of(_rad_SIUnitBasic), List.of());
    _sr_SISymType = createSIUnit(List.of(_sr_SIUnitBasic), List.of());
  }

}
