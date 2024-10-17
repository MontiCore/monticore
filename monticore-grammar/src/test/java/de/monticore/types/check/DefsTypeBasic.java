/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;

/**
 * DefsTypeBasic offers one Symbol-Infrastructure
 * including Scopes etc. that is used to provide relevant Symbols.
 *
 * This infrastructure can be used for testing
 */

public class DefsTypeBasic {

  // Original initialization
  static {
    setup();
  }
  
  /**
   * Initialization of the structure (can be called again to reinitialize).
   * Setup comes in two phases:
   * a) Build the new objects
   * b) add the linkage (and that is only necessary with complex objects
   */
  public static void setup() {
    // Phase A ----- create objects
    set_Void();  // only A
    set_Null();  // only A
    set_thePrimitives();   // only A
    set_BoxedPrimitives();
    set_array();
    set_String();
    set_Object();
    // Phase B ---- links
    link_array();
    link_String();
    link_Object();
   }
  
  
  /*********************************************************************/
  
  /**
   * Helpers that efficiently create Symbols
   * (which by the way can also later be extended)
   */
  public static OOTypeSymbol type(String name) {
    return type(name,name);
  }
  
  /** 
   * create TypeSymbols (some defaults apply)
   */
  public static OOTypeSymbol type(String name, String fullName) {
    return OOSymbolsMill.oOTypeSymbolBuilder()
            .setSpannedScope(OOSymbolsMill.scope())
            .setName(name)
            .setFullName(fullName)
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .build();
  }

  public static OOTypeSymbol type(String name, List<SymTypeExpression> superTypes){
    return OOSymbolsMill.oOTypeSymbolBuilder()
            .setSpannedScope(OOSymbolsMill.scope())
            .setName(name)
            .setFullName(name)
            .setSuperTypesList(superTypes)
            .build();
  }

  public static OOTypeSymbol type(String name, List<SymTypeExpression> superTypes, List<TypeVarSymbol> typeArguments){
    IOOSymbolsScope spannedScope = OOSymbolsMill.scope();
    OOTypeSymbol ts = OOSymbolsMill.oOTypeSymbolBuilder()
            .setSpannedScope(spannedScope)
            .setName(name)
            .setFullName(name)
            .setSuperTypesList(superTypes)
            .build();
    typeArguments.forEach(a -> ts.addTypeVarSymbol(a));
    return ts;
  }

  public static OOTypeSymbol type(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList,
                                  List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList){
    OOTypeSymbol ts = OOSymbolsMill.oOTypeSymbolBuilder()
          .setSpannedScope(OOSymbolsMill.scope())
          .setName(name)
          .setFullName(name)
          .setSuperTypesList(superTypeList)
          .build();
    methodList.forEach(m -> ts.addMethodSymbol(m));
    fieldList.forEach(f -> ts.addFieldSymbol(f));
    typeVariableList.forEach(a -> ts.addTypeVarSymbol(a));
    return ts;
  }

  public static OOTypeSymbol type(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList,
                                  List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList,
                                  IOOSymbolsScope enclosingScope){
    OOTypeSymbol t = OOSymbolsMill.oOTypeSymbolBuilder()
        .setEnclosingScope(enclosingScope)
        .setSpannedScope(OOSymbolsMill.scope())
        .setName(name)
        .setFullName(name)
        .setSuperTypesList(superTypeList)
        .build();
    methodList.forEach(m -> t.addMethodSymbol(m));
    fieldList.forEach(f -> t.addFieldSymbol(f));
    typeVariableList.forEach(a -> t.addTypeVarSymbol(a));

    t.getSpannedScope().setEnclosingScope(enclosingScope);

    for(MethodSymbol method: t.getMethodList()){
      method.getSpannedScope().setEnclosingScope(t.getSpannedScope());
    }
    return t;
  }

  /**
   * create TypeVariableSymbols (some defaults apply)
   */
  public static TypeVarSymbol typeVariable(String name){
    return OOSymbolsMill.typeVarSymbolBuilder()
        .setName(name)
        .setFullName(name)
            .setSpannedScope(OOSymbolsMill.scope())
        .build();
  }
  
  public static OOTypeSymbol add(OOTypeSymbol t, FieldSymbol f) {
    t.addFieldSymbol(f);
    return t;
  }
  
  public static OOTypeSymbol add(OOTypeSymbol t, MethodSymbol m) {
    t.addMethodSymbol(m);
    return t;
  }

  /** 
   * create FunctionSymbols (some defaults apply)
   */
  public static FunctionSymbol function(String name, SymTypeExpression returnType) {
    FunctionSymbol f = BasicSymbolsMill.functionSymbolBuilder()
        .setSpannedScope(BasicSymbolsMill.scope())
        .setName(name)
        .setFullName(name)  // can later be adapted, when fullname of Type is known
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setType(returnType)
        .build();
    f.setSpannedScope(BasicSymbolsMill.scope());
    return f;
  }
  
  /**
   * create MethodSymbols (some defaults apply)
   */
  public static MethodSymbol method(String name, SymTypeExpression returnType) {
    MethodSymbol m = OOSymbolsMill.methodSymbolBuilder()
            .setSpannedScope(OOSymbolsMill.scope())
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setType(returnType)
            .build();
    m.setSpannedScope(OOSymbolsMill.scope());
    return m;
  }
  
  public static MethodSymbol add(MethodSymbol m, FieldSymbol f) {
    m.getSpannedScope().add(f);
    return m;
  }

  /**
   * create VariableSymbols (some defaults apply)
   */
  public static VariableSymbol variable(String name, SymTypeExpression type) {
    return BasicSymbolsMill.variableSymbolBuilder()
        .setName(name)
        .setFullName(name)  // can later be adapted, when fullname of Type is known
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setType(type)
        .build();
  }

  /** 
   * create FieldSymbols (some defaults apply)
   */
  public static FieldSymbol field(String name, SymTypeExpression type) {
    return OOSymbolsMill.fieldSymbolBuilder()
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setType(type)
            .build();
  }

  /**
   * add a Type to a Scope (bidirectional)
   */
  public static void add2scope(IOOSymbolsScope p, TypeSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }
  
  /** 
   * add an OOType to a Scope (bidirectional)
   */
  public static void add2scope(IOOSymbolsScope p, OOTypeSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
    p.add((TypeSymbol) s);
  }

  /**
   * add a Variable to a Scope (bidirectional)
   */
  public static void add2scope(IOOSymbolsScope p, VariableSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }

  /**
   * add a Field (e.g. a Variable) to a Scope (bidirectional)
   */
  public static void add2scope(IOOSymbolsScope p, FieldSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }

  /**
   * add a Method to a Scope (bidirectional)
   */
  public static void add2scope(IOOSymbolsScope p, MethodSymbol s){
    s.setEnclosingScope(p);
    p.add(s);
    p.add((FunctionSymbol) s);
  }

  /** 
   * add a Function to a Scope (bidirectional)
   */
  public static void add2scope(IOOSymbolsScope p, FunctionSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }

  /**
   * add a TypeVariable to a Scope (bidirectional)
   */
  public static void add2scope(IOOSymbolsScope p, TypeVarSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }


  /**
   * It is tedious to allways add name and fullNamee individually:
   * So this functions does that for Types,Methods,Fields afterwards
   * (only the Type needs a full name, the rest is added)
   */
  public static void completeFullnames(OOTypeSymbol s) {
    // in the class Fullname must already set
    String prefix = s.getPackageName();
    for (MethodSymbol m : s.getMethodList()) {
      completeFullnames(m, prefix);
    }
    for (FieldSymbol f : s.getFieldList()) {
      completeFullnames(f, prefix);
    }
  }
  public static void completeFullnames(MethodSymbol s, String prefix) {
    if(s.getFullName() == null || !s.getFullName().contains(".")) {
      s.setFullName(prefix + "." + s.getName());
    }
  }
  public static void completeFullnames(FieldSymbol s, String prefix) {
    if(s.getFullName() == null || !s.getFullName().contains(".")) {
      s.setFullName(prefix + "." + s.getName());
    }
  }
  /*********************************************************************/

  /*********************************************************************/
  
  /**
   * This is a predefined Symbol describing the
   * externally accessible Fields, Methods, etc. of an      array
   *
   * We deliberately choose Java 10 here and take methods from:
   * https://docs.oracle.com/javase/10/docs/api/java/util/Arrays.html
   *
   */
  public static OOTypeSymbol _array;
  // SymTypeExpression _arraySymType  cannot be defined, because Arrays are generics and have varying arguments
  
  public static void set_array() {
    _array = type("ArrayType");
  }
  
  public static void link_array() {
    MethodSymbol m; FieldSymbol f;

    // toString()
    add(_array, method("toString", _StringSymType))
            .setFullName("java.lang.Object.toString");

    completeFullnames(_array);

  }
  
  /*********************************************************************/
  
  /**
   * This is a predefined Symbol describing the
   * externally accessible Fields, Methods, etc. of a String
   *
   * We deliberately choose Java 10 here and take methods from:
   * https://docs.oracle.com/javase/10/docs/api/java/lang/String.html
   */
  public static OOTypeSymbol _String;
  public static SymTypeOfObject _StringSymType;
  
  public static void set_String() {
    _String = type("String");
    OOTypeSymbol loader = new OOTypeSymbolSurrogate("String");
    loader.setEnclosingScope(createScopeWithString());
    _StringSymType = new SymTypeOfObject(loader);
  }

  public static IOOSymbolsScope createScopeWithString() {
    OOSymbolsScope typeSymbolsScope = new OOSymbolsScope();
    add2scope(typeSymbolsScope, _String);
    return typeSymbolsScope;
  }

  public static void link_String() {
    MethodSymbol m; FieldSymbol f;
    IOOSymbolsScope scope = OOSymbolsMill.scope();

    _String.setSpannedScope(scope);
    completeFullnames(_String);
  }
  
  /*********************************************************************/
  
  /**
   * This is a predefined Symbol describing the
   * externally accessible Fields, Methods, etc. of a Object
   *
   * We deliberately choose Java 10 here and take methods from:
   * https://docs.oracle.com/javase/10/docs/api/java/lang/Object.html
   */
  public static OOTypeSymbol _Object;
  public static SymTypeOfObject _ObjectSymType;
  
  public static void set_Object() {
    _Object = type("ObjectType");
  }
  
  public static void link_Object() {
    MethodSymbol m; FieldSymbol f;
    
    completeFullnames(_Object);
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Object");
    loader.setEnclosingScope(createScopeWithObject());
    _ObjectSymType = new SymTypeOfObject(loader);
  }

  public static IOOSymbolsScope createScopeWithObject() {
    OOSymbolsScope typeSymbolsScope = new OOSymbolsScope();
    typeSymbolsScope.add(_Object);
    return typeSymbolsScope;
  }



  /*********************************************************************/
  
  /**
   * This is the predefined Symbol for all Primitives, such as "int"
   * which has empty Fields and Methods
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
    OOTypeSymbol loader = new OOTypeSymbolSurrogate("int");
    loader.setEnclosingScope(typeSymbolsScope);
    _intSymType = new SymTypePrimitive(loader);

    loader = new OOTypeSymbolSurrogate("boolean");
    loader.setEnclosingScope(typeSymbolsScope);
    _booleanSymType = new SymTypePrimitive(loader);

    loader = new OOTypeSymbolSurrogate("char");
    loader.setEnclosingScope(typeSymbolsScope);
    _charSymType = new SymTypePrimitive(loader);

    loader = new OOTypeSymbolSurrogate("double");
    loader.setEnclosingScope(typeSymbolsScope);
    _doubleSymType = new SymTypePrimitive(loader);

    loader = new OOTypeSymbolSurrogate("float");
    loader.setEnclosingScope(typeSymbolsScope);
    _floatSymType = new SymTypePrimitive(loader);

    loader = new OOTypeSymbolSurrogate("long");
    loader.setEnclosingScope(typeSymbolsScope);
    _longSymType = new SymTypePrimitive(loader);

    loader = new OOTypeSymbolSurrogate("short");
    loader.setEnclosingScope(typeSymbolsScope);
    _shortSymType = new SymTypePrimitive(loader);

    loader = new OOTypeSymbolSurrogate("byte");
    loader.setEnclosingScope(typeSymbolsScope);
    _byteSymType = new SymTypePrimitive(loader);


  }

  /*********************************************************************/

  /**
   * This is the predefined Symbol for boxed Primitives, such as "Integer"
   */
  public static SymTypeOfObject _IntegerSymType;

  public static void set_BoxedPrimitives() {
    IBasicSymbolsGlobalScope typeSymbolsScope = BasicSymbolsMill.globalScope();
    OOTypeSymbol loader = new OOTypeSymbolSurrogate("Integer");
    loader.setEnclosingScope(typeSymbolsScope);
    _IntegerSymType = new SymTypeOfObject(loader);
  }

  
  /*********************************************************************/

  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "void" with no Fields, no Methods, etc.
   * It is used for internal derivations, but of course not for real type results
   *
   */
  public static OOTypeSymbol _void;
  public static SymTypeVoid _voidSymType;
  public static final String _voidTypeString = "voidType";
  
  public static void set_Void() {
    _void = type(_voidTypeString);           // the name shouldn't be used
    _void.setEnclosingScope(new OOSymbolsScope());
    _voidSymType = new SymTypeVoid();
  }
  
  
  /*********************************************************************/

  
  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "null" with no Fields, no Methods, etc.
   */
  public static OOTypeSymbol _null;
  public static SymTypeOfNull _nullSymType;
  public static final String _nullTypeString = "nullType";
  
  public static void set_Null() {
    _null = type(_nullTypeString);    // and the name shouldn't be used anyway, but it is at DeSer
    _null.setEnclosingScope(new OOSymbolsScope());
    _nullSymType = new SymTypeOfNull();
  }
  
  /*********************************************************************/

  
}
