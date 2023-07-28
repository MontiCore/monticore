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
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.check.SymTypeVoid;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createBottomType;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
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

}
