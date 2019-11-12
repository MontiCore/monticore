/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;

/**
 * DefsTypeBasic offers one Symbol-Infrastructure
 * including Scopes etc. that is used to provide relevant Symbols.
 *
 * This infrastructure cn also be used for testing, but is basically meant for
 * shipping along with the appropriate Types grammar:
 *    types/ MCBasicTypes.mc4
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
    set_array();
    set_String();
    set_Object();
    // Phase B ---- links
    link_array();
    link_String();
    link_Object();
    
    // TODO: A SymbolTable Structure that correctly reflects
    // the above objects could be helpful?
  }
  
  
  /*********************************************************************/
  
  /**
   * Helpers that efficiently create Symbols
   * (which by the way can also later be extended)
   */
  public static TypeSymbol type(String name) {
    return type(name,name);
  }
  
  /** create TypeSymbols (some defaults apply)
   */
  public static TypeSymbol type(String name, String fullName) {
    return TypeSymbolsSymTabMill.typeSymbolBuilder()
            .setName(name)
            .setFullName(fullName)
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setTypeParameterList(new ArrayList<>())
            .setFieldList(new ArrayList<>())
            .setMethodList(new ArrayList<>())
            .build();
  }

  public static TypeSymbol type(String name, List<SymTypeExpression> superTypes){
    return TypeSymbolsSymTabMill.typeSymbolBuilder()
            .setName(name)
            .setFullName(name)
            .setSuperTypeList(superTypes)
            .build();
  }

  public static TypeSymbol type(String name, List<SymTypeExpression> superTypes, List<TypeVarSymbol> typeArguments){
    return TypeSymbolsSymTabMill.typeSymbolBuilder()
            .setName(name)
            .setFullName(name)
            .setSuperTypeList(superTypes)
            .setTypeParameterList(typeArguments)
            .build();
  }

  public static TypeSymbol type(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList,
                                List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList){
    TypeSymbol t = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setName(name)
        .setFullName(name)
        .setTypeParameterList(typeVariableList)
        .setSuperTypeList(superTypeList)
        .setMethodList(methodList)
        .setFieldList(fieldList)
        .build();
    ExpressionsBasisScope spannedScope = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
    for(MethodSymbol method: methodList){
      add2scope(spannedScope,method);
      if(method.getSpannedScope()!=null){
        method.getSpannedScope().setEnclosingScope(spannedScope);
      }
    }
    for(FieldSymbol field: fieldList){
      add2scope(spannedScope,field);
    }
    t.setSpannedScope(spannedScope);
    return t;
  }

  public static TypeSymbol type(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList,
                                List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList,
                                ExpressionsBasisScope enclosingScope){
    TypeSymbol t = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setName(name)
        .setFullName(name)
        .setTypeParameterList(typeVariableList)
        .setSuperTypeList(superTypeList)
        .setMethodList(methodList)
        .setFieldList(fieldList)
        .setEnclosingScope(enclosingScope)
        .build();
    ExpressionsBasisScope spannedScope = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
    spannedScope.setEnclosingScope(enclosingScope);
    for(MethodSymbol method: methodList){
      add2scope(spannedScope,method);
      if(method.getSpannedScope()!=null){
        method.getSpannedScope().setEnclosingScope(spannedScope);
      }
    }
    for(FieldSymbol field: fieldList){
      add2scope(spannedScope,field);
    }
    t.setSpannedScope(spannedScope);
    return t;
  }

  /**
   * create TypeVariableSymbols (some defaults apply)
   */
  public static TypeVarSymbol typeVariable(String name){
    return TypeSymbolsSymTabMill.typeVarSymbolBuilder()
        .setName(name)
        .setFullName(name)
        .build();
  }
  
  public static TypeSymbol add(TypeSymbol t, FieldSymbol f) {
    List<FieldSymbol> fieldList = t.getFieldList();
    fieldList.add(f);
    t.setFieldList(fieldList);
    return t;
  }
  
  public static TypeSymbol add(TypeSymbol t, MethodSymbol m) {
    List<MethodSymbol> methodList = t.getMethodList();
    methodList.add(m);
    t.setMethodList(methodList);
    return t;
  }
  
  /** create MethodSymbols (some defaults apply)
   */
  public static MethodSymbol method(String name, SymTypeExpression returnType) {
    return TypeSymbolsSymTabMill.methodSymbolBuilder()
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setParameterList(new ArrayList<>())
            .setReturnType(returnType)
            .build();
  }
  
  public static MethodSymbol add(MethodSymbol m, FieldSymbol f) {
    m.getParameterList().add(f);
    return m;
  }
  
  /** create FieldSymbols (some defaults apply)
   */
  public static FieldSymbol field(String name, SymTypeExpression type) {
    return TypeSymbolsSymTabMill.fieldSymbolBuilder()
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setType(type)
            .build();
  }
  
  /** add a Type to a Scope (bidirectional)
   */
  public static void add2scope(ITypeSymbolsScope p, TypeSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }
  
  /** add a Filed (e.g. a Variable) to a Scope (bidirectional)
   */
  public static void add2scope(ITypeSymbolsScope p, FieldSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }

  /** add a Method to a Scope (bidirectional)
   */
  public static void add2scope(ITypeSymbolsScope p, MethodSymbol s){
    s.setEnclosingScope(p);
    p.add(s);
  }

  /**
   * It is tedious to allways add name and fullNamee individually:
   * So this functions does that for Types,Methods,Fields afterwards
   * (only the Type needs a full name, the rest is added)
   */
  public static void completeFullnames(TypeSymbol s) {
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
   * TODO RE, ggf. mit ND zusammen
   * TODO RE,AB: 1) Besseres Verfahren überlegen, wie man Vordefinierte Signaturen einliest
   *          (zB durch handgeschriebene Symtabs, die man der RTE beilegt,
   *          oder auch doch durch Java Code wie den hier?)
   *          Das untenstehende beispiel ist grausam schlecht)
   *
   * TODO RE: 2) Die Liste ist unvollständig --> irgendwie vervollstänndigen
   *       wenige übersetzte beispiele:
   *           int i[]; i.length; i.toString(); i.equals(Object i); i.wait(long 3,int 3);
   *
   * TODO RE,AB: (mittelfristig) 2) Konfigurierbarkeit ist zB notwendig
   *        mit der Target-Java-Version (denn die Checks
   *        müssen nicht nach Java 10 gehen, sondern evtl. eine frühere Version ...)
   *
   */
  public static TypeSymbol _array;
  // SymTypeExpression _arraySymType  cannot be defined, because Arrays are generics and have varying arguments
  
  public static void set_array() {
    _array = type("ArrayType");
  }
  
  public static void link_array() {
     // TODO: Offen, Array ist eigentlich generisch?
     // zur Zeit aber, leerer Typparameter: .setTypeParameter(new ArrayList<>());
     // die aktuell gelisteten Methoden haben auch keine generischen argumente
  
    MethodSymbol m; FieldSymbol f;
  
    // toString()
    add(_array, method("toString", _StringSymType))
            .setFullName("java.lang.Object.toString");
  
    // equals(Object)
    m = method("equals", _booleanSymType);
    m.setFullName("java.lang.Object.equals");
    add(m, field("o", _ObjectSymType));
    add(_array, m);
  
    // length field
    add(_array, field("length", _intSymType));
  
    // TODO RE: this function is very incomplete; ersetzen oder komplettieren
  
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
  public static TypeSymbol _String;
  public static SymTypeOfObject _StringSymType;
  
  public static void set_String() {
    _String = type("StringType");
    _StringSymType = new SymTypeOfObject("String", _String);
  }
  
  public static void link_String() {
    MethodSymbol m; FieldSymbol f;
    ExpressionsBasisScope scope = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
    
    // hashCode()
    add(_String, method("hashCode", _intSymType));
    scope.add(method("hashCode",_intSymType));

    // equals(Object)
    m = method("equals", _booleanSymType);
    m.setFullName("java.lang.Object.equals");
    add(m, field("o", _ObjectSymType));
    add(_String, m);
    scope.add(m);
  
    // indexOf(String str, int fromIndex)
    m = method("indexOf", _intSymType);
    add(m, field("str", _StringSymType));
    add(m, field("fromIndex", _intSymType));
    add(_String, m);
    scope.add(m);

    // toString()
    m = method("toString",_StringSymType);
    add(_String, m);
    scope.add(m);

    // charAt(int index)
    m = method("charAt",_charSymType);
    add(m,field("index",_intSymType));
    add(_String, m);
    scope.add(m);
    
    // TODO RE: this function is very incomplete (wegen der fehlenden Signatur); ersetzen oder komplettieren
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
  public static TypeSymbol _Object;
  public static SymTypeOfObject _ObjectSymType;
  
  public static void set_Object() {
    _Object = type("ObjectType");
  }
  
  public static void link_Object() {
    MethodSymbol m; FieldSymbol f;
    
    // equals(Object)
    m = method("equals", _booleanSymType);
    m.setFullName("equals");
    add(m, field("o", _ObjectSymType));
    add(_Object, m);
    
    // TODO RE: this function is very incomplete; ersetzen oder komplettieren
    
    completeFullnames(_Object);
    _ObjectSymType = new SymTypeOfObject("Object", _Object);
  }
  
  
  /*********************************************************************/
  
  /**
   * This is the predefined Symbol for all Primitives, such as "int"
   * which has empty Fields and Methods
   */
  public static TypeSymbol _int;
  public static SymTypeConstant _intSymType;
  public static TypeSymbol _char;
  public static SymTypeConstant _charSymType;
  public static TypeSymbol _boolean;
  public static SymTypeConstant _booleanSymType;
  public static TypeSymbol _double;
  public static SymTypeConstant _doubleSymType;
  public static TypeSymbol _float;
  public static SymTypeConstant _floatSymType;
  public static TypeSymbol _long;
  public static SymTypeConstant _longSymType;
  public static TypeSymbol _byte;
  public static SymTypeConstant _byteSymType;
  public static TypeSymbol _short;
  public static SymTypeConstant _shortSymType;
  
  
  public static Map<String,SymTypeConstant> typeConstants;
  
  public static void set_thePrimitives() {
    typeConstants = new HashMap<>();
    _int = type("int");
    _intSymType = new SymTypeConstant("int", _int);
    typeConstants.put("int", _intSymType);
    _boolean = type("boolean");
    _booleanSymType = new SymTypeConstant("boolean", _boolean);
    typeConstants.put("boolean", _booleanSymType);
    _char = type("char");
    _charSymType = new SymTypeConstant("char", _char);
    typeConstants.put("char", _charSymType);
    _double = type("double");
    _doubleSymType = new SymTypeConstant("double", _double);
    typeConstants.put("double", _doubleSymType);
    _float = type("float");
    _floatSymType = new SymTypeConstant("float", _float);
    typeConstants.put("float", _floatSymType);
    _long = type("long");
    _longSymType = new SymTypeConstant("long", _long);
    typeConstants.put("long", _longSymType);
    _byte = type("byte");
    _byteSymType = new SymTypeConstant("byte", _byte);
    typeConstants.put("byte", _byteSymType);
    _short = type("short");
    _shortSymType = new SymTypeConstant("short", _short);
    typeConstants.put("short", _shortSymType);
  }
  
  /*********************************************************************/

  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "void" with no Fields, no Methods, etc.
   * It is used for internal derivations, but of course not for real type results
   *
   */
  public static TypeSymbol _void;
  public static SymTypeVoid _voidSymType;
  public static String _voidTypeString = "voidType";
  
  public static void set_Void() {
    _void = type(_voidTypeString);           // the name shouldn't be used
    _voidSymType = new SymTypeVoid();
  }
  
  
  /*********************************************************************/

  
  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "null" with no Fields, no Methods, etc.
   */
  public static TypeSymbol _null;
  public static SymTypeOfNull _nullSymType;
  public static String _nullTypeString = "nullType";
  
  public static void set_Null() {
    _null = type(_nullTypeString);    // and the name shouldn't be used anyway, but it is at DeSer
    _nullSymType = new SymTypeOfNull();
  }
  
  /*********************************************************************/
  
  // TODO: diese Klasse etwas testen

  // TODO: diese Objekte realisieren
  
  public static TypeSymbol _list;
  public static SymTypeExpression _listSymType;

  public static TypeSymbol _set;
  public static SymTypeExpression _setTypeSymbol;

  public static TypeSymbol _map;
  public static SymTypeExpression _mapTypeSymbol;

  public static TypeSymbol _optional;
  public static SymTypeExpression _optionalTypeSymbol;
  
}
