package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfCommonExpressionTest {

  private ExpressionsBasisScope scope;

  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }

  // Parser used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressions derLit = new DeriveSymTypeOfCombineExpressions(ExpressionsBasisSymTabMill
      .expressionsBasisScopeBuilder()
      .build());

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test correctness of addition
   */
  @Test
  public void deriveFromPlusExpression() throws IOException {
    // example with two ints
    String s = "3+4";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    // example with double and int
    s = "4.9+12";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());

    // example with String
    s = "3 + \"Hallo\"";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());
  }

  /**
   * test correctness of subtraction
   */
  @Test
  public void deriveFromMinusExpression() throws IOException{
    // example with two ints
    String s = "7-2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with float and long
    s = "7.9f-3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float",tc.typeOf(astex).print());
  }

  /**
   * test correctness of multiplication
   */
  @Test
  public void deriveFromMultExpression() throws IOException{
    //example with two ints
    String s = "2*19";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long and char
    s = "\'a\'*3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
  }

  /**
   * test correctness of division
   */
  @Test
  public void deriveFromDivideExpression() throws IOException{
    //example with two ints
    String s = "7/12";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with float and double
    s = "5.4f/3.9";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
  }

  /**
   * tests correctness of modulo
   */
  @Test
  public void deriveFromModuloExpression() throws IOException{
    //example with two ints
    String s = "3%1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long and double
    s = "0.8%3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
  }

  /**
   * test LessEqualExpression
   */
  @Test
  public void deriveFromLessEqualExpression() throws IOException{
    //example with two ints
    String s = "4<=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.4f<=3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test GreaterEqualExpression
   */
  @Test
  public void deriveFromGreaterEqualExpression() throws IOException{
    //example with two ints
    String s = "7>=2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.5>=\'d\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test LessThanExpression
   */
  @Test
  public void deriveFromLessThanExpression() throws IOException{
    //example with two ints
    String s = "4<9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.4f<3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test GreaterThanExpression
   */
  @Test
  public void deriveFromGreaterThanExpression() throws IOException{
    //example with two ints
    String s = "7>2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.5>\'d\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * initialize basic scope and a few symbols for testing
   */
  public void init_basic(){
    // No enclosing Scope: Search ending here
    scope = scope(null,true,null,"Phantasy2");

    TypeSymbol person = DefsTypeBasic.type("Person");
    TypeSymbol student = DefsTypeBasic.type("Student",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person",person))
    );
    TypeSymbol firstsemesterstudent = DefsTypeBasic.type("FirstSemesterStudent",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student",student))
    );
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1", SymTypeExpressionFactory.createTypeObject("Person",person)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",person)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",student)));
    add2scope(scope, field("student2",SymTypeExpressionFactory.createTypeObject("Student",student)));
    add2scope(scope, field("firstsemester",SymTypeExpressionFactory.
        createTypeObject("FirstSemesterStudent",firstsemesterstudent)));
    add2scope(scope, method("isInt",_booleanSymType));
    add2scope(scope,add(method("isInt",_booleanSymType),field("maxLength",_intSymType)));

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);
  }

  /**
   * test EqualsExpression
   */
  @Test
  public void deriveFromEqualsExpression() throws IOException{
    //initialize symbol table
    init_basic();

    //example with two primitives
    String s = "7==9.5f";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects of the same class
    s = "student1==student2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects in sub-supertype relation
    s = "student1==person1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test NotEqualsExpression
   */
  @Test
  public void deriveFromNotEqualsExpression() throws IOException{
    //initialize symbol table
    init_basic();

    //example with two primitives
    String s = "true!=false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects of the same class
    s = "person1!=person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects in sub-supertype relation
    s = "student2!=person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test BooleanAndOpExpression
   */
  @Test
  public void deriveFromBooleanAndOpExpression() throws IOException{
    //only possible with two booleans
    String s = "true&&true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "(3<=4&&5>6)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  /**
   * test BooleanOrOpExpression
   */
  @Test
  public void deriveFromBooleanOrOpExpression() throws IOException{
    //only possible with two booleans
    String s = "true||false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "(3<=4.5f||5.3>6)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  /**
   * test LogicalNotExpression
   */
  @Test
  public void deriveFromLogicalNotExpression() throws IOException{
    //only possible with boolean as inner expression
    String s = "!true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "!(2.5>=0.3)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  /**
   * test BracketExpression
   */
  @Test
  public void deriveFromBracketExpression() throws IOException{
    //initialize symbol table
    init_basic();

    //test with only a literal in the inner expression
    String s = "(3)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test with a more complex inner expression
    s = "(3+4*(18-7.5))";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());

    //test without primitive types in inner expression
    s = "(person1)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());
  }

  /**
   * test ConditionalExpression
   */
  @Test
  public void deriveFromConditionalExpression() throws IOException{
    //initialize symbol table
    init_basic();

    //test with two ints as true and false expression
    String s = "3<4?9:10";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test with float and long
    s = "3>4?4.5f:10L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float",tc.typeOf(astex).print());

    //test without primitive types as true and false expression
    s = "3<9?person1:person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());

    //test with two objects in a sub-supertype relation
    s = "3<9?student1:person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());
  }

  /**
   * test BooleanNotExpression
   */
  @Test
  public void deriveFromBooleanNotExpression() throws IOException{
    //test with a int
    String s = "~3";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //test with a char
    s = "~\'a\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * initialize symboltable including global scope, artifact scopes and scopes with symbols for
   * testing (mostly used for FieldAccessExpressions)
   */
  public void init_advanced(){
    ExpressionsBasisGlobalScope globalScope = globalScope(new ExpressionsBasisLanguage(), new ModelPath());
    ExpressionsBasisArtifactScope artifactScope1 = artifactScope(globalScope,Lists.newArrayList(),"");
    ExpressionsBasisArtifactScope artifactScope2 = artifactScope(globalScope,Lists.newArrayList(),"");
    ExpressionsBasisArtifactScope artifactScope3 = artifactScope(globalScope,Lists.newArrayList(),"types2");
    ExpressionsBasisArtifactScope artifactScope4 = artifactScope(artifactScope3,Lists.newArrayList(),"types3");
    scope = scope(artifactScope1,true,null,"Phantasy2"); // No enclosing Scope: Search ending here
    ExpressionsBasisScope scope2 = scope(artifactScope2,"types");
    ExpressionsBasisScope scope3 = scope(artifactScope4,"types2");
    scope3.setEnclosingScope(artifactScope4);

    // some FieldSymbols (ie. Variables, Attributes)
    TypeSymbol person = DefsTypeBasic.type("Person");
    TypeSymbol student = DefsTypeBasic.type("Student",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person",person))
    );
    TypeSymbol firstsemesterstudent = DefsTypeBasic.type("FirstSemesterStudent",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student",student))
    );
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("Person",person)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",person)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",student)));
    add2scope(scope, field("student2",SymTypeExpressionFactory.createTypeObject("Student",student)));
    add2scope(scope, field("firstsemester",
        SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent",firstsemesterstudent))
    );
    add2scope(scope, method("isInt",_booleanSymType));
    add2scope(scope,add(method("isInt",_booleanSymType),field("maxLength",_intSymType)));
    FieldSymbol fs = field("variable",_intSymType);
    MethodSymbol ms = method("store",_doubleSymType);
    MethodSymbol ms1 = add(method("pay",_voidSymType),
        TypeSymbolsSymTabMill.fieldSymbolBuilder().setName("cost").setType(_intSymType).build()
    );
    ExpressionsBasisScope testSpannedScope = scope(scope2,"");
    testSpannedScope.add(fs);
    testSpannedScope.add(ms);
    testSpannedScope.add(ms1);
    TypeSymbol testType = DefsTypeBasic.type("Test");
    testType = add(add(add(testType,fs),ms),ms1);
    testType.setSpannedScope(testSpannedScope);
    add2scope(scope2,testType);
    add2scope(scope3,testType);
    add2scope(scope,testType);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);
  }

  /**
   * test FieldAccessExpression
   */
  @Test
  public void deriveFromFieldAccessExpression() throws IOException{
    //initialize symbol table
    init_advanced();

    //test for type with only one package
    String s = "types.Test";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("Test",tc.typeOf(astex).print());

    //test for variable of a type with one package
    s = "types.Test.variable";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test for type with more than one package
    s = "types2.types3.types2.Test";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Test",tc.typeOf(astex).print());

    //test for variable of type with more than one package
    s = "types2.types3.types2.Test.variable";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    s="Test";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Test",tc.typeOf(astex).print());
  }

  /**
   * test CallExpression
   */
  @Test
  public void deriveFromCallExpression() throws IOException{
    //initialize symbol table
    init_advanced();

    //test for method with unqualified name without parameters
    String s = "isInt()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //test for method with unqualified name with parameters
    s = "isInt(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //test for method with qualified name without parameters
    s = "types.Test.store()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());

    //test for method with qualified name with parameters
    s = "types.Test.pay(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void",tc.typeOf(astex).print());

    //test for String method
    s = "\"test\".hashCode()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test for multiple CallExpressions in a row
    s = "\"test\".toString().charAt(1)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());
  }

  /**
   * initialize the symbol table for a basic inheritance example
   * we only have one scope and the symbols are all in this scope or in subscopes
   */
  public void init_inheritance(){
    // No enclosing Scope: Search ending here
    scope = scope(null,true,null,"Phantasy2");

    //inheritance example
    //super
    MethodSymbol add = add(method("add",_voidSymType),field("element",_StringSymType));
    FieldSymbol field = field("field",_booleanSymType);
    TypeSymbol superclass = type("AList",Lists.newArrayList(add),Lists.newArrayList(field),
        Lists.newArrayList(),Lists.newArrayList()
    );
    SymTypeExpression supclass = SymTypeExpressionFactory.createTypeObject("AList",superclass);
    add2scope(scope,superclass);

    //sub
    TypeSymbol subclass = type("MyList",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(supclass),Lists.newArrayList()
    );
    SymTypeExpression sub = SymTypeExpressionFactory.createTypeObject("MyList",subclass);
    FieldSymbol myList = field("myList",sub);
    add2scope(scope,subclass);
    add2scope(scope,myList);

    //subsub
    TypeSymbol subsubclass = type("MyList",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(supclass),Lists.newArrayList()
    );
    SymTypeExpression subsub = SymTypeExpressionFactory.createTypeObject("MySubList",subsubclass);
    FieldSymbol mySubList = field("mySubList",subsub);
    add2scope(scope,subsubclass);
    add2scope(scope,mySubList);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);
  }

  /**
   * test if the methods and fields of superclasses can be used by subclasses
   */
  @Test
  public void testInheritance() throws IOException{
    //initialize symbol table
    init_inheritance();

    //methods
    //test normal inheritance
    String s = "myList.add(\"Hello\")";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("void",tc.typeOf(astex).print());

    //test inheritance over two levels
    s = "mySubList.add(\"World\")";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void",tc.typeOf(astex).print());

    //fields
    s = "myList.field";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "mySubList.field";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * initialize an empty scope
   */
  public void init_scope(){
    // No enclosing Scope: Search ending here
    scope = scope(null,true,null,"Phantasy2");
  }

  /**
   * test the inheritance of a generic type with one type variable
   */
  @Test
  public void testListAndArrayListInheritance() throws IOException{
    //initialize symboltable
    init_scope();

    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol addMethod = add(method("add",_booleanSymType),
        field("x",SymTypeExpressionFactory.createTypeVariable("T"))
    );
    FieldSymbol nextField = field("next",SymTypeExpressionFactory.createTypeVariable("T"));
    TypeSymbol sym = type("List",Lists.newArrayList(addMethod),Lists.newArrayList(nextField),
        Lists.newArrayList(),Lists.newArrayList(t)
    );
    SymTypeExpression listIntSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List",Lists.newArrayList(_intSymType),sym);
    listIntSymTypeExp.setTypeInfo(sym);
    FieldSymbol listVar = field("listVar",listIntSymTypeExp);
    add2scope(scope,listVar);
    add2scope(scope,sym);

    //one generic parameter, subtype ArrayList<T>
    TypeVarSymbol arrayListT = typeVariable("T");
    SymTypeExpression listTSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List",
            Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T")),sym
        );
    listTSymTypeExp.setTypeInfo(sym);
    TypeSymbol subsym = type("ArrayList",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(listTSymTypeExp),Lists.newArrayList(arrayListT)
    );
    SymTypeExpression subsymexp = SymTypeExpressionFactory.
        createGenerics("ArrayList",Lists.newArrayList(_intSymType),subsym);
    subsymexp.setTypeInfo(subsym);
    FieldSymbol arraylistVar = field("arraylistVar",subsymexp);
    add2scope(scope,arraylistVar);
    add2scope(scope,subsym);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    //test methods and fields of the supertype
    String s = "listVar.add(2)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "listVar.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test inherited methods and fields of the subtype
    s = "arraylistVar.add(3)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "arraylistVar.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * test the inheritance of generic types with two type variables
   */
  @Test
  public void testGenericInheritanceTwoTypeVariables() throws IOException{
    //initialize symboltable
    init_scope();

    //two generic parameters, supertype GenSup<S,V>, create SymType GenSup<String,int>
    TypeVarSymbol t1 = typeVariable("S");
    TypeVarSymbol t2 = typeVariable("V");
    MethodSymbol load = add(method("load",
        SymTypeExpressionFactory.createTypeVariable("S")),
        field("x",SymTypeExpressionFactory.createTypeVariable("V"))
    );
    FieldSymbol f1 = field("f1",SymTypeExpressionFactory.createTypeVariable("S"));
    FieldSymbol f2 = field("f2",SymTypeExpressionFactory.createTypeVariable("V"));
    TypeSymbol genSup = type("GenSup",Lists.newArrayList(load),Lists.newArrayList(f1,f2),
        Lists.newArrayList(),Lists.newArrayList(t1,t2)
    );
    SymTypeExpression genSupType = SymTypeExpressionFactory.
        createGenerics("GenSup",Lists.newArrayList(_StringSymType,_intSymType),genSup);
    genSupType.setTypeInfo(genSup);
    FieldSymbol genSupVar = field("genSupVar",genSupType);
    add2scope(scope,genSup);
    add2scope(scope,genSupVar);

    //two generic parameters, subtype GenSub<S,V>, create SymType GenSub<String,int>
    t1 = typeVariable("S");
    t2 = typeVariable("V");
    SymTypeExpression genTypeSV = SymTypeExpressionFactory.
        createGenerics("GenSup",Lists.newArrayList(SymTypeExpressionFactory.
            createTypeVariable("S"),SymTypeExpressionFactory.createTypeVariable("V")),genSup);
    genTypeSV.setTypeInfo(genSup);
    TypeSymbol genSub = type("GenSub",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(genTypeSV.deepClone()),Lists.newArrayList(t1,t2)
    );
    SymTypeExpression genSubType = SymTypeExpressionFactory.
        createGenerics("GenSub",Lists.newArrayList(_StringSymType,_intSymType),genSub);
    genSubType.setTypeInfo(genSub);
    FieldSymbol genSubVar = field("genSubVar",genSubType);
    add2scope(scope,genSub);
    add2scope(scope,genSubVar);

    //two generic parameters, subsubtype GenSubSub<V,S>, create GenSubSub<String,int>
    t1 = typeVariable("S");
    t2 = typeVariable("V");
    SymTypeExpression genSubTypeSV = SymTypeExpressionFactory.
        createGenerics("GenSub",Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("S"),
            SymTypeExpressionFactory.createTypeVariable("V")),genSub
        );
    genSubTypeSV.setTypeInfo(genSub);
    TypeSymbol genSubSub = type("GenSubSub",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(genSubTypeSV.deepClone()),Lists.newArrayList(t2,t1)
    );
    SymTypeExpression genSubSubType = SymTypeExpressionFactory.
        createGenerics("GenSubSub",Lists.newArrayList(_StringSymType,_intSymType),genSubSub);
    genSubSubType.setTypeInfo(genSubSub);
    FieldSymbol genSubSubVar = field("genSubSubVar",genSubSubType);
    add2scope(scope,genSubSub);
    add2scope(scope,genSubSubVar);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    //supertype: test methods and fields
    String s = "genSupVar.load(3)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());

    s = "genSupVar.f1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());

    s = "genSupVar.f2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //subtype: test inherited methods and fields
    s = "genSubVar.load(3)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());

    s = "genSubVar.f1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());

    s = "genSubVar.f2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //subsubtype: test inherited methods and fields
    s="genSubSubVar.load(\"Hello\")";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    s = "genSubSubVar.f1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    s = "genSubSubVar.f2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());
  }

  /**
   * test if methods and a field from a fixed subtype(generic type, but instead of type variable concrete type)
   * are inherited correctly
   */
  @Test
  public void testSubVarSupFix() throws IOException{
    //initialize symboltable
    init_scope();

    //subtype with variable generic parameter, supertype with fixed generic parameter
    //supertype with fixed generic parameter FixGen<A> and SymType FixGen<int>
    TypeVarSymbol a = typeVariable("A");
    MethodSymbol add2 = add(method("add",_booleanSymType),
        field("a",SymTypeExpressionFactory.createTypeVariable("A"))
    );
    FieldSymbol next2 = field("next",SymTypeExpressionFactory.createTypeVariable("A"));
    TypeSymbol fixGen = type("FixGen",Lists.newArrayList(add2),Lists.newArrayList(next2),
        Lists.newArrayList(),Lists.newArrayList(a)
    );
    SymTypeExpression fixGenType = SymTypeExpressionFactory.createGenerics("FixGen",
        Lists.newArrayList(_intSymType),fixGen
    );
    fixGenType.setTypeInfo(fixGen);
    FieldSymbol fixGenVar = field("fixGenVar",fixGenType);
    add2scope(scope,fixGen);
    add2scope(scope,fixGenVar);

    //subtype with variable generic parameter VarGen<N> which extends FixGen<int>, SymType VarGen<String>
    TypeVarSymbol n = typeVariable("N");
    MethodSymbol calculate = method("calculate",SymTypeExpressionFactory.createTypeVariable("N"));
    TypeSymbol varGenType = type("VarGen",Lists.newArrayList(calculate),Lists.newArrayList(),
        Lists.newArrayList(fixGenType),Lists.newArrayList(n)
    );
    SymTypeExpression varGenSym = SymTypeExpressionFactory.
        createGenerics("VarGen",Lists.newArrayList(_StringSymType),varGenType);
    varGenSym.setTypeInfo(varGenType);
    FieldSymbol varGen = field("varGen",varGenSym);
    add2scope(scope,varGenType);
    add2scope(scope,varGen);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    //test own methods first
    String s = "varGen.calculate()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());

    //test inherited methods and fields
    s="varGen.add(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s="varGen.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * Test-Case: SubType has more generic parameters than its supertype
   */
  @Test
  public void testSubTypeWithMoreGenericParameters() throws IOException{
    //initialize symboltable
    init_scope();

    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol addMethod = add(method("add",_booleanSymType),
        field("x",SymTypeExpressionFactory.createTypeVariable("T"))
    );
    FieldSymbol nextField = field("next",SymTypeExpressionFactory.createTypeVariable("T"));
    TypeSymbol sym = type("List",Lists.newArrayList(addMethod),Lists.newArrayList(nextField),
        Lists.newArrayList(),Lists.newArrayList(t)
    );
    SymTypeExpression listIntSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List",Lists.newArrayList(_intSymType),sym);
    listIntSymTypeExp.setTypeInfo(sym);
    FieldSymbol listVar = field("listVar",listIntSymTypeExp);
    add2scope(scope,listVar);
    add2scope(scope,sym);

    //two generic parameters, subtype MoreGen<T,F>
    t = typeVariable("T");
    TypeVarSymbol moreType1 = typeVariable("F");
    SymTypeExpression listTSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List",Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T")),sym);
    listTSymTypeExp.setTypeInfo(sym);
    MethodSymbol insert = add(
        method("insert",SymTypeExpressionFactory.createTypeVariable("T")),
        field("x",SymTypeExpressionFactory.createTypeVariable("F"))
    );
    TypeSymbol moreGenType = type("MoreGen",Lists.newArrayList(insert),Lists.newArrayList(),
        Lists.newArrayList(listTSymTypeExp),Lists.newArrayList(t,moreType1)
    );
    SymTypeExpression moreGenSym = SymTypeExpressionFactory.
        createGenerics("MoreGen",Lists.newArrayList(_intSymType,_longSymType),moreGenType);
    moreGenSym.setTypeInfo(moreGenType);
    FieldSymbol moreGen = field("moreGen",moreGenSym);
    add2scope(scope,moreGenType);
    add2scope(scope,moreGen);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    //test own method
    String s = "moreGen.insert(12L)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test inherited methods and fields
    s="moreGen.add(12)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s="moreGen.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * Test-Case: SubType is a normal object type and extends a fixed generic type
   */
  @Test
  public void testSubTypeWithoutGenericParameter() throws IOException{
    //initialize symboltable
    init_scope();

    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol addMethod = add(method("add",_booleanSymType),
        field("x",SymTypeExpressionFactory.createTypeVariable("T"))
    );
    FieldSymbol nextField = field("next",SymTypeExpressionFactory.createTypeVariable("T"));
    TypeSymbol sym = type("List",Lists.newArrayList(addMethod),Lists.newArrayList(nextField),
        Lists.newArrayList(),Lists.newArrayList(t)
    );
    SymTypeExpression listIntSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List",Lists.newArrayList(_intSymType),sym);
    listIntSymTypeExp.setTypeInfo(sym);
    FieldSymbol listVar = field("listVar",listIntSymTypeExp);
    add2scope(scope,listVar);
    add2scope(scope,sym);

    //subtype without generic parameter NotGen extends List<int>
    TypeSymbol notgeneric = type("NotGen",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(listIntSymTypeExp),Lists.newArrayList()
    );
    SymTypeExpression notgenericType = SymTypeExpressionFactory.createTypeObject("NotGen",notgeneric);
    FieldSymbol ng = field("notGen",notgenericType);
    add2scope(scope,ng);
    add2scope(scope,notgeneric);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    //test inherited methods and fields
    String s="notGen.add(14)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s="notGen.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * every type in the example has exactly one type variable
   */
  @Test
  public void testMultiInheritance() throws IOException{
    //initialize symboltable
    init_scope();

    //supertype SupA<T>
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol testA = method("testA",SymTypeExpressionFactory.createTypeVariable("T"));
    FieldSymbol currentA = field("currentA",SymTypeExpressionFactory.createTypeVariable("T"));
    TypeSymbol supA = type("SupA",Lists.newArrayList(testA),Lists.newArrayList(currentA),
        Lists.newArrayList(),Lists.newArrayList(t)
    );
    SymTypeExpression supATExpr = SymTypeExpressionFactory
        .createGenerics("SupA",Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T")),supA);
    supATExpr.setTypeInfo(supA);

    //supertype SupB<T>
    t = typeVariable("T");
    MethodSymbol testB = method("testB",SymTypeExpressionFactory.createTypeVariable("T"));
    FieldSymbol currentB = field("currentB",SymTypeExpressionFactory.createTypeVariable("T"));
    TypeSymbol supB = type("SupA",Lists.newArrayList(testB),Lists.newArrayList(currentB),
        Lists.newArrayList(),Lists.newArrayList(t)
    );
    SymTypeExpression supBTExpr = SymTypeExpressionFactory.
        createGenerics("SupB",Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T")),supB);
    supBTExpr.setTypeInfo(supB);

    //subType SubA<T>
    t = typeVariable("T");
    TypeSymbol subA = type("SubA",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(supATExpr,supBTExpr),Lists.newArrayList(t)
    );
    SymTypeExpression subATExpr = SymTypeExpressionFactory
        .createGenerics("SubA",Lists.newArrayList(_charSymType),subA);
    subATExpr.setTypeInfo(subA);
    FieldSymbol sub = field("sub",subATExpr);
    add2scope(scope,sub);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    String s = "sub.testA()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());

    s = "sub.currentA";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());

    s = "sub.testB()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());

    s = "sub.currentB";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * the supertypes have one type variable and the subtype has two type variables
   */
  @Test
  public void testMultiInheritanceSubTypeMoreGen() throws IOException{
    //initialize symboltable
    init_scope();

    //supertype SupA<T>
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol testA = method("testA",SymTypeExpressionFactory.createTypeVariable("T"));
    FieldSymbol currentA = field("currentA",SymTypeExpressionFactory.createTypeVariable("T"));
    TypeSymbol supA = type("SupA",Lists.newArrayList(testA),Lists.newArrayList(currentA),
        Lists.newArrayList(),Lists.newArrayList(t)
    );
    SymTypeExpression supATExpr = SymTypeExpressionFactory
        .createGenerics("SupA",Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T")),supA);
    supATExpr.setTypeInfo(supA);

    //supertype SupB<T>
    TypeVarSymbol s = typeVariable("S");
    MethodSymbol testB = method("testB",SymTypeExpressionFactory.createTypeVariable("S"));
    FieldSymbol currentB = field("currentB",SymTypeExpressionFactory.createTypeVariable("S"));
    TypeSymbol supB = type("SupA",Lists.newArrayList(testB),Lists.newArrayList(currentB),
        Lists.newArrayList(),Lists.newArrayList(s)
    );
    SymTypeExpression supBTExpr = SymTypeExpressionFactory
        .createGenerics("SupB",Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("S")),supB);
    supBTExpr.setTypeInfo(supB);

    //subType SubA<T>
    t = typeVariable("T");
    s = typeVariable("S");
    TypeSymbol subA = type("SubA",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(supATExpr,supBTExpr),Lists.newArrayList(s,t)
    );
    SymTypeExpression subATExpr = SymTypeExpressionFactory
        .createGenerics("SubA",Lists.newArrayList(_charSymType,_booleanSymType),subA);
    subATExpr.setTypeInfo(subA);
    FieldSymbol sub = field("sub",subATExpr);
    add2scope(scope,sub);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    String s1 = "sub.testA()";
    ASTExpression astex = p.parse_StringExpression(s1).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s1 = "sub.currentA";
    astex = p.parse_StringExpression(s1).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s1 = "sub.testB()";
    astex = p.parse_StringExpression(s1).get();
    assertEquals("char",tc.typeOf(astex).print());

    s1 = "sub.currentB";
    astex = p.parse_StringExpression(s1).get();
    assertEquals("char",tc.typeOf(astex).print());
  }

  /**
   * test if you can use methods, types and fields of the type or its supertypes in its method scopes
   */
  @Test
  public void testMethodScope() throws IOException{
    init_scope();

    //super
    MethodSymbol add = add(method("add",_voidSymType),field("element",_StringSymType));
    FieldSymbol field = field("field",_booleanSymType);
    TypeSymbol superclass = type("AList",Lists.newArrayList(add),Lists.newArrayList(field),
        Lists.newArrayList(),Lists.newArrayList()
    );
    SymTypeExpression supclass = SymTypeExpressionFactory.createTypeObject("AList",superclass);
    add2scope(scope,superclass);

    //sub
    TypeSymbol subclass = type("MyList",Lists.newArrayList(),Lists.newArrayList(),
        Lists.newArrayList(supclass),Lists.newArrayList()
    );
    SymTypeExpression sub = SymTypeExpressionFactory.createTypeObject("MyList",subclass);
    FieldSymbol myList = field("myList",sub);
    add2scope(scope,subclass);
    add2scope(scope,myList);

    //subsub
    FieldSymbol myNext = field("myNext",_StringSymType);
    MethodSymbol myAdd = method("myAdd",_voidSymType);
    ExpressionsBasisScope methodSpannedScope = scope();
    myAdd.setSpannedScope(methodSpannedScope);
    TypeSymbol subsubclass = type("MyList",Lists.newArrayList(myAdd),Lists.newArrayList(myNext),
        Lists.newArrayList(supclass),Lists.newArrayList(),scope
    );
    SymTypeExpression subsub = SymTypeExpressionFactory.createTypeObject("MySubList",subsubclass);
    FieldSymbol mySubList = field("mySubList",subsub);
    add2scope(scope,subsubclass);
    add2scope(scope,mySubList);

    //set scope of method myAdd as standard resolving scope
    derLit.setScope(methodSpannedScope);
    tc = new TypeCheck(null,derLit);

    String s = "mySubList";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("MySubList",tc.typeOf(astex).print());

    s = "myAdd()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void",tc.typeOf(astex).print());

    s = "myNext";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());

    s = "add(\"Hello\")";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void",tc.typeOf(astex).print());

    s = "field";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * create a scope (some defaults apply)
   */
  public static ExpressionsBasisScope scope(){
    return ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
  }

  public static ExpressionsBasisScope scope(IExpressionsBasisScope enclosingScope, boolean exportingSymbols, ASTNode astnode, String name){
    return ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
        .setEnclosingScope(enclosingScope)
        .setExportingSymbols(exportingSymbols)
        .setAstNode(astnode)
        .setName(name)
        .build();
  }

  public static ExpressionsBasisScope scope(IExpressionsBasisScope enclosingScope, String name){
    return ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
        .setEnclosingScope(enclosingScope)
        .setName(name)
        .build();
  }

  /**
   * create a global scope (some defaults apply)
   */
  public static ExpressionsBasisGlobalScope globalScope(ExpressionsBasisLanguage expressionsBasisLanguage, ModelPath modelPath){
    return ExpressionsBasisSymTabMill.expressionsBasisGlobalScopeBuilder()
        .setExpressionsBasisLanguage(expressionsBasisLanguage)
        .setModelPath(modelPath)
        .build();
  }

  /**
   * create an artifact scope (some defaults apply)
   */
  public static ExpressionsBasisArtifactScope artifactScope(IExpressionsBasisScope enclosingScope, List<ImportStatement> importList, String packageName){
    return ExpressionsBasisSymTabMill.expressionsBasisArtifactScopeBuilder()
        .setEnclosingScope(enclosingScope)
        .setImportList(importList)
        .setPackageName(packageName)
        .build();
  }
}
