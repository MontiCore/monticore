/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.expressions.expressionsbasis._ast.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScopeBuilder;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.mcbasictypes._symboltable.MCBasicTypesGlobalScope;
import de.monticore.types.mcbasictypes._symboltable.MCBasicTypesLanguage;
import de.monticore.types.typesymbols._symboltable.*;

import java.nio.file.Paths;
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
    set_int();   // only A
    set_boolean();   // only A
    set_array();
    set_String();
    set_Object();
    // Phase B ---- links
    link_array();
    link_String();
    link_Object();
    
    
    // Phase 3: setting Scopes and attaching all Symbols to Scopes
    
    
    // TODO: Ab hier: Dies ist ein experimentelles Setting, das muss noch
    // rausfaktorisiert werden (damit es auch andere Arten von Glocal Scopes geben kann
    // die mit diesen Symbolen gefüttert werden können
    // Oder man verschiebt das in die tests, denn die folgenden zeilen sind evtl. nicht
    // nicht ganz allgemeingültig?
    
    // Setting up a Scope Infrastructure (without a global Scope)
  
    ExpressionsBasisScope scope =
            ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
            .setEnclosingScope(null)       // No enclosing Scope: Search ending here
            .setExportsSymbols(true)
            .setAstNode(null)
            .setName("Phantasy2").build();     // hopefully unused
    
     // scope.add(ETypeSymbol ...);
   
    // YYY weiter
    
    
  }
  
  
  /*********************************************************************/
  
  /**
   * Helpers that efficiently create Symbols
   * (which by the way can also later be extended)
   */
  public static TypeSymbol type(String name) {
    return type(name,name);
  }
  
  public static TypeSymbol type(String name, String fullName) {
    return TypeSymbolsSymTabMill.typeSymbolBuilder()
            .setName(name)
            .setFullName(fullName)
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setTypeParameter(new ArrayList<>())
            .setFields(new ArrayList<>())
            .setMethods(new ArrayList<>())
            .build();
  }
  
  public static TypeSymbol add(TypeSymbol t, FieldSymbol f) {
    t.getFields().add(f);
    return t;
  }
  
  public static TypeSymbol add(TypeSymbol t, MethodSymbol m) {
    t.getMethods().add(m);
    return t;
  }
  
  public static MethodSymbol method(String name, SymTypeExpression returnType) {
    return TypeSymbolsSymTabMill.methodSymbolBuilder()
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setParameter(new ArrayList<>())
            .setReturnType(returnType)
            .build();
  }
  
  public static MethodSymbol add(MethodSymbol m, FieldSymbol f) {
    m.getParameter().add(f);
    return m;
  }
  
  public static FieldSymbol field(String name, SymTypeExpression type) {
    return TypeSymbolsSymTabMill.fieldSymbolBuilder()
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setType(type)
            .build();
  }
  
  public static void checkFullnames(TypeSymbol s) {
    // in the class Fullname must already set
    String prefix = s.getPackageName();
    for (MethodSymbol m : s.getMethods()) {
      checkFullnames(m, prefix);
    }
    for (FieldSymbol f : s.getFields()) {
      checkFullnames(f, prefix);
    }
  }
  public static void checkFullnames(MethodSymbol s, String prefix) {
    if(s.getFullName() == null || !s.getFullName().contains(".")) {
      s.setFullName(prefix + "." + s.getName());
    }
  }
  public static void checkFullnames(FieldSymbol s, String prefix) {
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
  
    checkFullnames(_array);
    
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
    
    checkFullnames(_String);
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
    
    checkFullnames(_Object);
    _ObjectSymType = createTypeObject("Object", _Object);
  }
  
  
  /*********************************************************************/
  
  /**
   * This is the predefined Symbol for "boolean"
   * which has empty Fields and Methods
   */
  public static TypeSymbol _boolean;
  public static SymTypeExpression _booleanSymType;
  
  
  public static void set_boolean() {
    _boolean = type("boolean");
    _booleanSymType = SymTypeExpressionFactory.createTypeConstant("boolean");
  }

  // TODO RE: hier fehlen die anderen primitive Types noch-- nach selbem Muster
  
  /*********************************************************************/
  
  /**
   * This is the predefined Symbol for "int"
   * which has empty Fields and Methods
   */
  public static TypeSymbol _int;
  public static SymTypeExpression _intSymType;
  
  
  public static void set_int() {
    _int = type("int");
    _intSymType = SymTypeExpressionFactory.createTypeConstant("int");
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
