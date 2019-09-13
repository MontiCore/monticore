/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.*;

import java.util.ArrayList;

import static de.monticore.types2.SymTypeExpressionFactory.createTypeObject;

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
  
  public static TypeSymbol add(TypeSymbol t, FieldSymbol f) {
    t.getFieldList().add(f);
    return t;
  }
  
  public static TypeSymbol add(TypeSymbol t, MethodSymbol m) {
    t.getMethodList().add(m);
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
  public static SymTypeExpression _StringSymType;
  
  public static void set_String() {
    _String = type("StringType");
    _StringSymType = createTypeObject("String", _String);
  }
  
  public static void link_String() {
    MethodSymbol m; FieldSymbol f;
    
    // hashCode()
    add(_String, method("hashCode", _intSymType));
    
    // equals(Object)
    m = method("equals", _booleanSymType);
    m.setFullName("java.lang.Object.equals");
    add(m, field("o", _ObjectSymType));
    add(_String, m);
  
    // indexOf(String str, int fromIndex)
    m = method("indexOf", _intSymType);
    add(m, field("str", _StringSymType));
    add(m, field("fromIndex", _intSymType));
    add(_String, m);
    
    // TODO RE: this function is very incomplete; ersetzen oder komplettieren
    
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
  public static SymTypeExpression _ObjectSymType;
  
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
    _ObjectSymType = createTypeObject("Object", _Object);
  }
  
  
  /*********************************************************************/
  
  /**
   * This is the predefined Symbol for al Primitives, such as "int"
   * which has empty Fields and Methods
   */
  public static TypeSymbol _int;
  public static SymTypeExpression _intSymType;
  public static TypeSymbol _char;
  public static SymTypeExpression _charSymType;
  public static TypeSymbol _boolean;
  public static SymTypeExpression _booleanSymType;
  public static TypeSymbol _double;
  public static SymTypeExpression _doubleSymType;
  public static TypeSymbol _float;
  public static SymTypeExpression _floatSymType;
  public static TypeSymbol _long;
  public static SymTypeExpression _longSymType;
  
  
  public static void set_thePrimitives() {
    _int = type("int");
    _intSymType = SymTypeExpressionFactory.createTypeConstant("int");
    _boolean = type("boolean");
    _booleanSymType = SymTypeExpressionFactory.createTypeConstant("boolean");
    _char = type("char");
    _charSymType = SymTypeExpressionFactory.createTypeConstant("char");
    _double = type("double");
    _doubleSymType = SymTypeExpressionFactory.createTypeConstant("double");
    _float = type("float");
    _floatSymType = SymTypeExpressionFactory.createTypeConstant("float");
    _long = type("long");
    _longSymType = SymTypeExpressionFactory.createTypeConstant("long");
  }
  
  /*********************************************************************/

  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "void" with no Fields, no Methods, etc.
   * It is used for internal derivations, but of course not for real type results
   *
   */
  public static TypeSymbol _void;
  public static SymTypeExpression _voidSymType;
  
  public static void set_Void() {
    _void = type("voidType");           // the name shouldn't be unused
    _voidSymType = SymTypeExpressionFactory.createTypeVoid();
  }
  
  
  /*********************************************************************/

  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "null" with no Fields, no Methods, etc.
   */
  public static TypeSymbol _null;
  public static SymTypeExpression _nullSymType;
  
  public static void set_Null() {
    _null = type("nullType");    // and the name shouldn't be used anyway
    _nullSymType = SymTypeExpressionFactory.createTypeOfNull();
  }
  
  // TODO: diese Klasse etwas testen
  
  
}
