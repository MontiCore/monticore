/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.SymTypeNormalizeVisitor;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommonExpressionTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Before
  public void init() {
    setupValues();
  }

  @Test
  public void deriveFromIncSuffixExpression() throws IOException {

    //example with byte
    checkExpr("varbyte++", "byte");

    //example with short
    checkExpr("varshort++", "short");

    //example with char
    checkExpr("varchar++", "char");

    //example with int
    checkExpr("varint++", "int");

    //example with float
    checkExpr("varfloat++", "float");

    //example with double
    checkExpr("vardouble++", "double");
  }

  @Test
  public void deriveFromPlusExpression() throws IOException {
    // example with two ints
    checkExpr("3+4", "int");

    // example with double and int
    checkExpr("4.9+12", "double");

    // example with Integer
    checkExpr("varint+varint", "int");

    // example with String
    checkExpr("3 + \"Hallo\"", "String");
  }

  @Test
  public void testInvalidPlusExpression() throws IOException {
    checkErrorExpr("3+true", "0xB0163");
  }

  @Test
  public void deriveFromMinusExpression() throws IOException {
    // example with two ints
    checkExpr("7-2", "int");

    //example with float and long
    checkExpr("7.9f-3L", "float");
  }

  @Test
  public void testInvalidMinusExpression() throws IOException {
    checkErrorExpr("3-true", "0xB0163");
  }

  @Test
  public void deriveFromMultExpression() throws IOException {
    //example with two ints
    checkExpr("2*19", "int");

    //example with long and char
    checkExpr("'a'*3L", "long");
  }

  @Test
  public void testInvalidMultExpression() throws IOException {
    checkErrorExpr("3*true", "0xB0163");
  }

  @Test
  public void deriveFromDivideExpression() throws IOException {
    //example with two ints
    checkExpr("7/12", "int");

    //example with float and double
    checkExpr("5.4f/3.9", "double");
  }

  @Test
  public void testInvalidDivideExpression() throws IOException {
    checkErrorExpr("3/true", "0xB0163");
  }

  @Test
  public void deriveFromModuloExpression() throws IOException {
    //example with two ints
    checkExpr("3%1", "int");

    //example with long and double
    checkExpr("0.8%3L", "double");
  }

  @Test
  public void testInvalidModuloExpression() throws IOException {
    checkErrorExpr("3%true", "0xB0163");
  }

  @Test
  public void deriveFromLessEqualExpression() throws IOException {
    //example with two ints
    checkExpr("4<=9", "boolean");

    //example with two other numeric types
    checkExpr("2.4f<=3L", "boolean");
  }

  @Test
  public void testInvalidLessEqualExpression() throws IOException {
    checkErrorExpr("3<=true", "0xB0167");
  }

  @Test
  public void deriveFromGreaterEqualExpression() throws IOException {
    //example with two ints
    checkExpr("7>=2", "boolean");

    //example with two other numeric types
    checkExpr("2.5>='d'", "boolean");
  }

  @Test
  public void testInvalidGreaterEqualExpression() throws IOException {
    checkErrorExpr("3>=true", "0xB0167");
  }

  @Test
  public void deriveFromLessThanExpression() throws IOException {
    //example with two ints
    checkExpr("4<9", "boolean");

    //example with two other numeric types
    checkExpr("2.4f<3L", "boolean");
  }

  @Test
  public void testInvalidLessThanExpression() throws IOException {
    checkErrorExpr("3<true", "0xB0167");
  }

  @Test
  public void deriveFromGreaterThanExpression() throws IOException {
    //example with two ints
    checkExpr("7>2", "boolean");

    //example with two other numeric types
    checkExpr("2.5>'d'", "boolean");
  }

  @Test
  public void testInvalidGreaterThanExpression() throws IOException {
    checkErrorExpr("3>true", "0xB0167");
  }

  @Test
  public void deriveFromEqualsExpression() throws IOException {
    //example with two primitives
    checkExpr("7==9.5f", "boolean");

    //example with two objects of the same class
    checkExpr("student1==student2", "boolean");

    //example with two objects in sub-supertype relation
    checkExpr("person1==student1", "boolean");
  }

  @Test
  public void testInvalidEqualsExpression() throws IOException {
    checkErrorExpr("3==true", "0xB0166");
  }

  @Test
  public void testInvalidEqualsExpression2() throws IOException {
    //person1 has the type Person, foo is a boolean
    checkErrorExpr("person1==foo", "0xB0166");
  }

  @Test
  public void deriveFromNotEqualsExpression() throws IOException {
    //example with two primitives
    checkExpr("true!=false", "boolean");

    //example with two objects of the same class
    checkExpr("person1!=person2", "boolean");

    //example with two objects in sub-supertype relation
    checkExpr("student2!=person2", "boolean");
  }

  @Test
  public void testInvalidNotEqualsExpression() throws IOException {
    checkErrorExpr("3!=true", "0xB0166");
  }

  @Test
  public void testInvalidNotEqualsExpression2() throws IOException {
    //person1 is a Person, foo is a boolean
    checkErrorExpr("person1!=foo", "0xB0166");
  }

  @Test
  public void deriveFromBooleanAndOpExpression() throws IOException {
    //only possible with two booleans
    checkExpr("true&&true", "boolean");

    checkExpr("(3<=4&&5>6)", "boolean");
  }

  @Test
  public void testInvalidAndOpExpression() throws IOException {
    //only possible with two booleans
    checkErrorExpr("3&&true", "0xB0113");
  }

  @Test
  public void deriveFromBooleanOrOpExpression() throws IOException {
    //only possible with two booleans
    checkExpr("true||false", "boolean");

    checkExpr("(3<=4.5f||5.3>6)", "boolean");
  }

  @Test
  public void testInvalidOrOpExpression() throws IOException {
    //only possible with two booleans
    checkErrorExpr("3||true", "0xB0113");
  }

  @Test
  public void deriveFromLogicalNotExpression() throws IOException {
    //only possible with boolean as inner expression
    checkExpr("!true", "boolean");

    checkExpr("!(2.5>=0.3)", "boolean");
  }

  @Test
  public void testInvalidLogicalNotExpression() throws IOException {
    //only possible with a boolean as inner expression
    checkErrorExpr("!4", "0xB0164");
  }

  @Test
  public void deriveFromBracketExpression() throws IOException {
    //test with only a literal in the inner expression
    checkExpr("(3)", "int");

    //test with a more complex inner expression
    checkExpr("(3+4*(18-7.5))", "double");

    //test without primitive types in inner expression
    checkExpr("(person1)", "Person");
  }

  @Test
  public void testInvalidBracketExpression() throws IOException {
    //a cannot be resolved -> a has no type
    checkErrorExpr("(a)", "0xFD118");
  }

  @Test
  public void deriveFromConditionalExpression() throws IOException {
    SymTypeRelations typeRel = new SymTypeRelations();

    //test with byte and short
    ASTExpression astExpr = parseExpr("varbool ? varbyte : varshort");
    generateScopes(astExpr);
    assertNoFindings();
    calculateTypes(astExpr);
    SymTypeExpression type = getType4Ast().getTypeOfExpression(astExpr);
    assertTrue(typeRel.isCompatible(_shortSymType, type));
    assertFalse(typeRel.isCompatible(_byteSymType, type));

    //test with two ints as true and false expression
    astExpr = parseExpr("3<4?9:10");
    generateScopes(astExpr);
    assertNoFindings();
    calculateTypes(astExpr);
    type = getType4Ast().getTypeOfExpression(astExpr);
    assertTrue(typeRel.isCompatible(_intSymType, type));

    // test with boolean and int
    astExpr = parseExpr("3<4?true:7");
    generateScopes(astExpr);
    assertNoFindings();
    calculateTypes(astExpr);
    type = getType4Ast().getTypeOfExpression(astExpr);
    assertFalse(typeRel.isCompatible(_booleanSymType, type));
    assertFalse(typeRel.isCompatible(_intSymType, type));

    //test with float and long
    astExpr = parseExpr("3>4?4.5f:10L");
    generateScopes(astExpr);
    assertNoFindings();
    calculateTypes(astExpr);
    type = getType4Ast().getTypeOfExpression(astExpr);
    assertTrue(typeRel.isCompatible(_floatSymType, type));
    assertFalse(typeRel.isCompatible(_longSymType, type));

    //test without primitive types as true and false expression
    astExpr = parseExpr("3<9?person1:person2");
    generateScopes(astExpr);
    assertNoFindings();
    calculateTypes(astExpr);
    type = getType4Ast().getTypeOfExpression(astExpr);
    assertTrue(typeRel.isCompatible(_personSymType, type));

    //test with two objects in a sub-supertype relation
    astExpr = parseExpr("3<9?student1:person2");
    generateScopes(astExpr);
    assertNoFindings();
    calculateTypes(astExpr);
    type = getType4Ast().getTypeOfExpression(astExpr);
    assertTrue(typeRel.isCompatible(_personSymType, type));
    assertFalse(typeRel.isCompatible(_studentSymType, type));
  }

  @Test
  public void testInvalidConditionalExpression() throws IOException {
    checkErrorExpr("3?true:false", "0xB0165");
  }

  @Test
  public void deriveFromBooleanNotExpression() throws IOException {
    //test with a int
    checkExpr("~3", "int");
    //test with a char
    checkExpr("~'a'", "int");
    //test with a long
    checkExpr("~varlong", "long");
  }

  @Test
  public void testInvalidBooleanNotExpression() throws IOException {
    //only possible with an integral type (int, long, char, short, byte)
    checkErrorExpr("~3.4", "0xB0175");
  }

  /**
   * initialize symboltable including
   * global scope, artifact scopes and scopes with symbols for testing
   * (mostly used for FieldAccessExpressions)
   */
  public void init_advanced() {
    ICombineExpressionsWithLiteralsGlobalScope globalScope =
        CombineExpressionsWithLiteralsMill.globalScope();

    ICombineExpressionsWithLiteralsArtifactScope artifactScope2 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope2.setEnclosingScope(globalScope);
    artifactScope2.setImportsList(Lists.newArrayList());
    artifactScope2.setName("types");
    artifactScope2.setPackageName("");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope3 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope3.setEnclosingScope(globalScope);
    artifactScope3.setImportsList(Lists.newArrayList());
    artifactScope3.setName("types2");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope4 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope4.setEnclosingScope(globalScope);
    artifactScope4.setImportsList(Lists.newArrayList());
    artifactScope4.setName("types3");
    artifactScope4.setPackageName("types3");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope5 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope5.setEnclosingScope(globalScope);
    artifactScope5.setImportsList(Lists.newArrayList());
    artifactScope5.setName("functions1");
    artifactScope5.setPackageName("functions1");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope6 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope6.setEnclosingScope(globalScope);
    artifactScope6.setImportsList(Lists.newArrayList());
    artifactScope6.setName("functions2");
    artifactScope6.setPackageName("functions2");

    //todo
    // No enclosing Scope: Search ending here

    ICombineExpressionsWithLiteralsScope scope3 =
        CombineExpressionsWithLiteralsMill.scope();
    scope3.setName("types2");
    artifactScope4.addSubScope(scope3);
    scope3.setEnclosingScope(artifactScope4);

    TypeSymbol selfReflectiveStudent = BasicSymbolsMill.typeSymbolBuilder()
        .setName("SelfReflectiveStudent")
        .setSpannedScope(OOSymbolsMill.scope())
        .setSuperTypesList(List.of(_studentSymType))
        .setEnclosingScope(globalScope)
        .build();
    inScope(selfReflectiveStudent.getSpannedScope(), method("self",
        createTypeObject(selfReflectiveStudent))
    );
    inScope(selfReflectiveStudent.getSpannedScope(),
        variable("selfVar", createTypeObject(selfReflectiveStudent))
    );
    inScope(globalScope, variable("selfReflectiveStudent",
        createTypeObject("SelfReflectiveStudent", globalScope))
    );

    inScope(artifactScope2, type("AClass"));
    inScope(scope3, type("AClass"));
    inScope(globalScope, type("AClass"));

    inScope(artifactScope2, type("BClass"));
    inScope(scope3, type("BClass"));

    inScope(artifactScope2, type("CClass"));
    inScope(scope3, type("CClass"));

    inScope(artifactScope2, selfReflectiveStudent);

    MethodSymbol ms2 = method("isInt", _booleanSymType);
    inScope(globalScope, ms2);
    inScope(globalScope, method("isInt", _booleanSymType, _intSymType));
    MethodSymbol ms0 = method("areInt", _booleanSymType, _intSymType);
    ms0.setIsElliptic(true);
    inScope(globalScope, ms0);
    inScope(globalScope, method("getIsInt", ms2.getFunctionType()));
    inScope(globalScope, method("getAreInt", ms0.getFunctionType()));

    OOTypeSymbol testType =
        inScope(globalScope, oOtype("Test",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(
                method("store", _doubleSymType),
                method("pay", _voidSymType, _intSymType)
            ),
            List.of(field("variable", _intSymType))
        ));
    testType.getMethodList().forEach(m -> m.setIsStatic(true));
    testType.getFieldList().forEach(f -> f.setIsStatic(true));

    OOTypeSymbol testType2 =
        inScope(artifactScope2, oOtype("Test",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(
                method("store", _doubleSymType),
                method("pay", _voidSymType, _intSymType)
            ),
            List.of(field("variable", _intSymType))
        ));
    testType2.getMethodList().forEach(m -> m.setIsStatic(true));
    testType2.getFieldList().forEach(f -> f.setIsStatic(true));

    OOTypeSymbol testType3 =
        oOtype("Test",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(
                method("store", _doubleSymType),
                method("pay", _voidSymType, _intSymType)
            ),
            List.of(field("variable", _intSymType))
        );
    testType3.getMethodList().forEach(m -> m.setIsStatic(true));
    testType3.getFieldList().forEach(f -> f.setIsStatic(true));

    IOOSymbolsScope testScope = testType3.getSpannedScope();

    FieldSymbol testVariable = field("testVariable", _shortSymType);
    testVariable.setIsStatic(true);
    OOTypeSymbol testInnerType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("TestInnerType")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setEnclosingScope(testScope)
        .build();
    testInnerType.addFieldSymbol(testVariable);
    testInnerType.setIsStatic(true);
    inScope(testScope, testInnerType);
    inScope(testInnerType.getSpannedScope(), testVariable);

    testType3.setSpannedScope(testScope);

    inScope(artifactScope2, testType2);
    inScope(scope3, testType3);
    inScope(globalScope, testType);

    inScope(artifactScope5, function("getPi", _floatSymType));
    inScope(artifactScope6, function("getPi", _floatSymType));

    // Creating types for legal access
    // on "types.DeepNesting.firstLayer.onlyMember",
    // where firstLayer and onlyMember are fields
    OOTypeSymbol oneFieldMember =
        inScope(globalScope, oOtype("OneFieldMember"));
    FieldSymbol onlyMember = field("onlyMember", _intSymType);
    inScope(oneFieldMember.getSpannedScope(), onlyMember);

    OOTypeSymbol deepNesting = oOtype("DeepNesting");
    inScope(artifactScope2, deepNesting);
    FieldSymbol firstLayer = field("firstLayer",
        SymTypeExpressionFactory.createTypeExpression(oneFieldMember));
    firstLayer.setIsStatic(true);
    inScope(deepNesting.getSpannedScope(), firstLayer);
  }

  @Test
  public void deriveFromFieldAccessExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for variable of a type with one package
    checkExpr("types.Test.variable", "int");

    //test for variable of type with more than one package
    checkExpr("types3.types2.Test.variable", "int");

    //test for variable in inner type
    checkExpr("types3.types2.Test.TestInnerType.testVariable", "short");

    // test for nested field access ("firstLayer" is a static member, "onlyMember" is an instance member)
    checkExpr("types.DeepNesting.firstLayer.onlyMember", "int");
  }

  @Test
  public void syntheziseFromFieldAccessExpression() throws IOException {
    init_advanced();

    checkType("Test", "Test");

    //test for type with only one package
    checkType("types.Test", "Test");

    //test for type with more than one package
    checkType("types3.types2.Test", "types3.types2.Test");
  }

  @Test
  public void deriveFromCallExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for method with unqualified name without parameters
    checkExpr("isInt()", "boolean");

    //test for method with unqualified name with parameters
    checkExpr("isInt(4)", "boolean");

    //test for method with varargs with no optional value
    checkExpr("areInt()", "boolean");

    //test for method with varargs with one optional value
    checkExpr("areInt(1)", "boolean");

    //test for method with varargs with multiple optional values
    checkExpr("areInt(1, 2, 3)", "boolean");

    //test for method with qualified name without parameters
    checkExpr("types.Test.store()", "double");

    //test for method with qualified name with parameters
    checkExpr("types.Test.pay(4)", "void");

    //test for function with that exists in another scope with
    //the same name but different qualified name
    checkExpr("functions1.functions1.getPi()", "float");

    // test method chaining
    checkExpr("selfReflectiveStudent.self().self()", "SelfReflectiveStudent");

    // test function chaining
    checkExpr("getIsInt()()", "boolean");
    checkExpr("(()->()->1)()()", "int");

    // test indirect function chaining
    checkExpr("(getIsInt())()", "boolean");
    checkExpr("((()->()->1)())()", "int");

    // test function chaining with varargs
    checkExpr("getAreInt()()", "boolean");
    checkExpr("getAreInt()(1,2)", "boolean");

    // test function chaining using fields
    checkExpr("selfReflectiveStudent.self().self()", "SelfReflectiveStudent");
  }

  @Test
  public void testInvalidCallExpression() throws IOException {
    //method isNot() is not in scope -> method cannot be resolved -> method has no return type
    init_advanced();
    checkErrorExpr("isNot()", "0xFD118");
  }

  @Test
  public void testInvalidCallExpressionWithMissingNameAndNotComposedOfCallback()
      throws IOException {
    // Expression (2 + 3)() and all other Expressions in front of brackets are parsable
    init_advanced();
    checkErrorExpr("(2 + 3)()", "0xFDABC");
  }

  @Test
  public void testInvalidCallExpressionWithInvalidQualifiedName() throws IOException {
    //method isInt() is not in the specified scope -> method cannot be resolved
    init_advanced();
    checkErrorExpr("notAScope.isInt()", "0xF777F");
  }

  @Test
  public void testInvalidCallExpressionWithFunctionChaining() throws IOException {
    //function isNot() is part of the return type of getIsInt() -> function cannot be resolved
    init_advanced();
    checkErrorExpr("getIsInt.isNot()", "0xFDB3A");
  }

  @Test
  public void testInvalidCallExpressionWithInvalidArgument() throws IOException {
    init_advanced();
    checkErrorExpr("isInt(\"foo\" / 2)", "0xB0163");
  }

  @Test
  public void testRegularAssignmentWithTwoMissingFields() throws IOException {
    checkErrorExpr("missingField = missingField2", "0xFD118");
  }

  @Test
  public void testMissingMethodWithMissingArgs() throws IOException {
    checkErrorExpr("missingMethod(missing1, missing2)", "0xFD118");
  }

  /**
   * initialize the symbol table for a basic inheritance example
   * we only have one scope and the symbols are all in this scope or in subscopes
   */
  public void init_inheritance() {
    //inheritance example
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    //super
    OOTypeSymbol aList = inScope(globalScope,
        oOtype("AList",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(method("add", _voidSymType,
                List.of(_boxedString))),
            List.of(field("name", _boxedString))
        )
    );

    //sub
    OOTypeSymbol myList = inScope(globalScope,
        oOtype("MyList", List.of(createTypeObject(aList)))
    );
    inScope(globalScope, field("myList", createTypeObject(myList)));

    //subsub
    OOTypeSymbol mySubList = inScope(globalScope,
        oOtype("MySubList", List.of(createTypeObject(myList)))
    );
    inScope(globalScope, field("mySubList", createTypeObject(myList)));
  }

  /**
   * test if the methods and fields of superclasses can be used by subclasses
   */
  @Test
  public void testInheritance() throws IOException {
    //initialize symbol table
    init_inheritance();

    //methods
    //test normal inheritance
    checkExpr("myList.add(\"Hello\")", "void");

    //test inheritance over two levels
    checkExpr("mySubList.add(\"World\")", "void");

    //fields
    checkExpr("myList.name", "java.lang.String");

    checkExpr("mySubList.name", "java.lang.String");
  }

  /**
   * test the inheritance of a generic type with one type variable
   */
  @Test
  public void testListAndArrayListInheritance() throws IOException {
    //one generic parameter, supertype List<T>
    IBasicSymbolsScope listScope =
        _boxedListSymType.getTypeInfo().getSpannedScope();
    TypeVarSymbol listTVar = listScope.getLocalTypeVarSymbols().get(0);
    listScope.add(function("add", _booleanSymType,
        List.of(createTypeVariable(listTVar))
    ));
    listScope.add(variable("next", createTypeVariable(listTVar)));

    //test methods and fields of the supertype
    checkExpr("intList.add(2)", "boolean");

    checkExpr("intList.next", "int");

    //test inherited methods and fields of the subtype
    checkExpr("intLinkedList.add(3)", "boolean");

    checkExpr("intLinkedList.next", "int");
  }

  /**
   * test the inheritance of generic types with two type variables
   */
  @Test
  public void testGenericInheritanceTwoTypeVariables() throws IOException {
    // two generic parameters, supertype GenSup<S,V>,
    // create SymType GenSup<String,int>
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    TypeVarSymbol tvS = typeVariable("S");
    TypeVarSymbol tvV = typeVariable("V");
    TypeSymbol genSup = inScope(gs,
        oOtype("GenSup",
            Collections.emptyList(),
            List.of(tvS, tvV),
            List.of(method("load",
                createTypeVariable(tvS),
                createTypeVariable(tvV))
            ),
            List.of(
                field("f1", createTypeVariable(tvS)),
                field("f2", createTypeVariable(tvV))
            )
        )
    );
    SymTypeExpression genSupType =
        createGenerics(genSup, _boxedString, _intSymType);
    inScope(gs, field("genSupVar", genSupType));

    // two generic parameters, subtype GenSub<S,V>,
    // create SymType GenSub<String,int>
    // same name of variables on purpose
    TypeVarSymbol tvS2 = typeVariable("S");
    TypeVarSymbol tvV2 = typeVariable("V");
    OOTypeSymbol genSub = inScope(gs,
        oOtype("GenSub",
            List.of(createGenerics(genSup,
                createTypeVariable(tvS2), createTypeVariable(tvV2))),
            List.of(tvS2, tvV2),
            // override f1
            Collections.emptyList(),
            List.of(field("f1", createTypeVariable(tvS2)))
        )
    );
    SymTypeExpression genSubType =
        createGenerics(genSub, _boxedString, _intSymType);
    inScope(gs, field("genSubVar", genSubType));

    //two generic parameters, subsubtype GenSubSub<V,S>, create GenSubSub<String,int>
    TypeVarSymbol tvS3 = typeVariable("S");
    TypeVarSymbol tvV3 = typeVariable("V");
    OOTypeSymbol genSubSub = inScope(gs,
        oOtype("GenSubSub",
            List.of(createGenerics(genSub,
                createTypeVariable(tvS3), createTypeVariable(tvV3))),
            List.of(tvV3, tvS3)
        )
    );
    SymTypeExpression genSubSubType =
        createGenerics(genSubSub, _boxedString, _intSymType);
    inScope(gs, field("genSubSubVar", genSubSubType));

    //supertype: test methods and fields
    checkExpr("genSupVar.load(3)", "java.lang.String");

    checkExpr("genSupVar.f1", "java.lang.String");

    checkExpr("genSupVar.f2", "int");

    //subtype: test inherited methods and fields
    checkExpr("genSubVar.load(3)", "java.lang.String");

    checkExpr("genSubVar.f1", "java.lang.String");

    checkExpr("genSubVar.f2", "int");

    //subsubtype: test inherited methods and fields
    checkExpr("genSubSubVar.load(\"Hello\")", "int");

    checkExpr("genSubSubVar.f1", "int");

    checkExpr("genSubSubVar.f2", "java.lang.String");
  }

  /**
   * test if methods and a field from a fixed subtype(generic type, but instead of type variable concrete type)
   * are inherited correctly
   */
  @Test
  public void testSubVarSupFix() throws IOException {
    //subtype with variable generic parameter, supertype with fixed generic parameter
    //supertype with fixed generic parameter FixGen<A> and SymType FixGen<int>
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();

    TypeVarSymbol tvA = typeVariable("A");
    OOTypeSymbol fixGen = inScope(gs,
        oOtype("FixGen",
            Collections.emptyList(),
            List.of(tvA),
            List.of(method("add", _booleanSymType, createTypeVariable(tvA))),
            List.of(field("next", createTypeVariable(tvA)))
        )
    );
    SymTypeExpression fixGenType = createGenerics(fixGen, _intSymType);
    inScope(gs, field("fixGenVar", fixGenType));

    // subtype with variable generic parameter VarGen<N>
    // which extends FixGen<int>,
    // SymType VarGen<String>
    TypeVarSymbol tvN = typeVariable("N");
    OOTypeSymbol varGen = inScope(gs,
        oOtype("VarGen",
            List.of(fixGenType),
            List.of(tvN),
            List.of(method("calculate", createTypeVariable(tvN))),
            Collections.emptyList()
        )
    );
    SymTypeExpression varGenType = createGenerics(varGen, _boxedString);
    inScope(gs, field("varGen", varGenType));

    //test own methods first
    checkExpr("varGen.calculate()", "java.lang.String");

    //test inherited methods and fields
    checkExpr("varGen.add(4)", "boolean");

    checkExpr("varGen.next", "int");
  }

  /**
   * Test-Case: SubType has more generic parameters than its supertype
   */
  @Test
  public void testSubTypeWithMoreGenericParameters() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //one generic parameter, supertype List<T>
    TypeSymbol list = _unboxedListSymType.getTypeInfo();
    TypeVarSymbol tvT = list.getTypeParameterList().get(0);
    inScope(list.getSpannedScope(),
        function("add", _booleanSymType, createTypeVariable(tvT))
    );
    inScope(list.getSpannedScope(), variable("next", createTypeVariable(tvT)));

    //two generic parameters, subtype MoreGen<T,F>
    TypeVarSymbol tvT2 = typeVariable("T");
    TypeVarSymbol tvF = typeVariable("F");
    TypeSymbol moreGenType = inScope(gs,
        type("MoreGen",
            List.of(createGenerics(list, createTypeVariable(tvT2))),
            List.of(tvT2, tvF),
            List.of(function("insert", createTypeVariable(tvT2), createTypeVariable(tvF))),
            Collections.emptyList()
        )
    );
    VariableSymbol moreGen = variable("moreGen",
        createGenerics(moreGenType, _intSymType, _longSymType)
    );
    inScope(gs, moreGen);

    //test own method
    checkExpr("moreGen.insert(12L)", "int");

    //test inherited methods and fields
    checkExpr("moreGen.add(12)", "boolean");

    checkExpr("moreGen.next", "int");
  }

  /**
   * Test-Case: SubType is a normal object type and extends a fixed generic type
   */
  @Test
  public void testSubTypeWithoutGenericParameter() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //one generic parameter, supertype List<T>
    TypeSymbol list = _unboxedListSymType.getTypeInfo();
    TypeVarSymbol tvT = list.getTypeParameterList().get(0);
    inScope(list.getSpannedScope(),
        function("add", _booleanSymType, createTypeVariable(tvT))
    );
    inScope(list.getSpannedScope(), variable("next", createTypeVariable(tvT)));

    //subtype without generic parameter NotGen extends List<int>
    OOTypeSymbol notGeneric = inScope(gs,
        oOtype("NotGen",
            List.of(createGenerics(list, _intSymType))
        )
    );
    inScope(gs, field("notGen", createTypeObject(notGeneric)));

    //test inherited methods and fields
    checkExpr("notGen.add(14)", "boolean");

    checkExpr("notGen.next", "int");
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * every type in the example has exactly one type variable
   */
  @Test
  public void testMultiInheritance() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //supertype SupA<T>
    TypeVarSymbol tvSupA = typeVariable("T");
    OOTypeSymbol supAType = inScope(gs,
        oOtype("SupA",
            Collections.emptyList(),
            List.of(tvSupA),
            List.of(method("testA", createTypeVariable(tvSupA))),
            List.of(field("currentA", createTypeVariable(tvSupA)))
        )
    );

    //supertype SupB<T>
    TypeVarSymbol tvSupB = typeVariable("T");
    OOTypeSymbol supBType = inScope(gs,
        oOtype("SupB",
            Collections.emptyList(),
            List.of(tvSupB),
            List.of(method("testB", createTypeVariable(tvSupB))),
            List.of(field("currentB", createTypeVariable(tvSupB)))
        )
    );

    //subType SubA<T>
    TypeVarSymbol tvSubA = typeVariable("T");
    OOTypeSymbol subAType = inScope(gs,
        oOtype("SubA",
            List.of(createGenerics(supAType, createTypeVariable(tvSubA)),
                createGenerics(supBType, createTypeVariable(tvSubA))),
            List.of(tvSubA)
        )
    );
    inScope(gs, field("sub", createGenerics(subAType, _charSymType)));

    checkExpr("sub.testA()", "char");

    checkExpr("sub.currentA", "char");

    checkExpr("sub.testB()", "char");

    checkExpr("sub.currentB", "char");
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * the supertypes have one type variable and the subtype has two type variables
   */
  @Test
  public void testMultiInheritanceSubTypeMoreGen() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //supertype SupA<T>
    TypeVarSymbol tvSupA = typeVariable("T");
    OOTypeSymbol supAType = inScope(gs,
        oOtype("SupA",
            Collections.emptyList(),
            List.of(tvSupA),
            List.of(method("testA", createTypeVariable(tvSupA))),
            List.of(field("currentA", createTypeVariable(tvSupA)))
        )
    );

    //supertype SupB<T>
    TypeVarSymbol tvSupB = typeVariable("T");
    OOTypeSymbol supBType = inScope(gs,
        oOtype("SupB",
            Collections.emptyList(),
            List.of(tvSupB),
            List.of(method("testB", createTypeVariable(tvSupB))),
            List.of(field("currentB", createTypeVariable(tvSupB)))
        )
    );

    //subType SubA<T,V>
    TypeVarSymbol tvSubAT = typeVariable("T");
    TypeVarSymbol tvSubAV = typeVariable("V");
    OOTypeSymbol subAType = inScope(gs,
        oOtype("SubA",
            List.of(createGenerics(supAType, createTypeVariable(tvSubAT)),
                createGenerics(supBType, createTypeVariable(tvSubAV))),
            List.of(tvSubAT, tvSubAV)
        )
    );
    inScope(gs, field("sub",
        createGenerics(subAType, _booleanSymType, _charSymType)
    ));

    checkExpr("sub.testA()", "boolean");

    checkExpr("sub.currentA", "boolean");

    checkExpr("sub.testB()", "char");

    checkExpr("sub.currentB", "char");
  }

  /**
   * test if you can use methods, types and fields of the type or its supertypes in its method scopes
   */
  @Test
  public void testMethodScope() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //one generic parameter, supertype List<T>
    TypeSymbol list = _boxedListSymType.getTypeInfo();
    TypeVarSymbol tvT = list.getTypeParameterList().get(0);
    inScope(list.getSpannedScope(),
        function("add", _voidSymType, createTypeVariable(tvT))
    );
    inScope(list.getSpannedScope(), field("name", _unboxedString));

    //sub
    TypeSymbol linkedList = _linkedListSymType.getTypeInfo();
    TypeVarSymbol tvT2 = linkedList.getTypeParameterList().get(0);
    inScope(linkedList.getSpannedScope(), field("next", createTypeVariable(tvT2)));

    //subsub
    TypeVarSymbol tvV = typeVariable("V");
    FunctionSymbol myAdd = function("myAdd", _voidSymType);
    VariableSymbol myAddParameter = variable("parameter", createTypeVariable(tvV));
    myAdd.getParameterList().add(myAddParameter);
    inScope(myAdd.getSpannedScope(), myAddParameter);
    TypeSymbol mySubListType = inScope(gs,
        type("MySubList",
            List.of(createGenerics(linkedList, createTypeVariable(tvV))),
            List.of(tvV),
            List.of(myAdd),
            List.of(field("myName", _unboxedString))
        )
    );
    mySubListType.getFullName();

    // all expressions are to be in the myAdd() scope,
    // we do not have subscopes in these expressions
    ExpressionsBasisTraverser scopeSetter =
        ExpressionsBasisMill.inheritanceTraverser();
    scopeSetter.add4ExpressionsBasis(
        new ExpressionsBasisVisitor2() {
          @Override public void visit(ASTExpression node) {
            node.setEnclosingScope(myAdd.getSpannedScope());
          }
        }
    );

    // we calculate subexpressions within the myAdd method
    ASTExpression astexpr = parseExpr("myAdd(null)");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    calculateTypes(astexpr);
    SymTypeExpression type = getType4Ast().getTypeOfExpression(astexpr);
    assertNoFindings();
    assertEquals("void", type.printFullName());

    astexpr = parseExpr("myName");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    calculateTypes(astexpr);
    type = getType4Ast().getTypeOfExpression(astexpr);
    assertNoFindings();
    assertEquals("String", type.printFullName());

    astexpr = parseExpr("next");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    calculateTypes(astexpr);
    type = getType4Ast().getTypeOfExpression(astexpr);
    assertNoFindings();
    assertEquals("MySubList.V", type.printFullName());

    astexpr = parseExpr("name");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    calculateTypes(astexpr);
    type = getType4Ast().getTypeOfExpression(astexpr);
    assertNoFindings();
    assertEquals("String", type.printFullName());

    astexpr = parseExpr("parameter");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    calculateTypes(astexpr);
    type = getType4Ast().getTypeOfExpression(astexpr);
    assertNoFindings();
    assertEquals("MySubList.V", type.printFullName());

    astexpr = parseExpr("add(parameter)");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    calculateTypes(astexpr);
    type = getType4Ast().getTypeOfExpression(astexpr);
    assertNoFindings();
    assertEquals("void", type.printFullName());
  }

  public void init_static_example() {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //types A and B
    MethodSymbol atest = method("test", _voidSymType);
    atest.setIsStatic(true);
    FieldSymbol afield = field("field", _intSymType);
    afield.setIsStatic(true);
    OOTypeSymbol a = inScope(gs,
        oOtype("A",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(atest),
            List.of(afield)
        )
    );
    //A has static inner type D
    FieldSymbol aDX = field("x", _intSymType);
    aDX.setIsStatic(true);
    OOTypeSymbol aD = inScope(a.getSpannedScope(), oOtype("D",
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        List.of(aDX)
    ));
    aD.setIsStatic(true);

    MethodSymbol btest = method("test", _voidSymType);
    FieldSymbol bfield = field("field", _intSymType);
    OOTypeSymbol b = inScope(gs,
        oOtype("B",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(btest),
            List.of(bfield)
        )
    );
    //B has non-static inner type D
    FieldSymbol bDX = field("x", _intSymType);
    OOTypeSymbol bD = inScope(b.getSpannedScope(), oOtype("D",
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        List.of(bDX)
    ));

    //A has static method test, static field field, static type D
    //B has normal method test, normal field field, normal type D
    //type C extends A and has no method, field or type
    inScope(gs, oOtype("C", List.of(createTypeObject(a))));
  }

  @Test
  public void testStaticType() throws IOException {
    init_static_example();

    checkType("A.D", "A.D");
    checkType("B.D", "B.D");
  }

  @Test
  public void testInvalidStaticType() throws IOException {
    init_static_example();

    checkErrorMCType("A.NotAType", "0xA0324");
  }

  @Test
  public void testStaticField() throws IOException {
    init_static_example();

    checkExpr("A.field", "int");
  }

  @Test
  public void testInvalidStaticField() throws IOException {
    init_static_example();

    checkErrorExpr("B.field", "0xF777F");
  }

  @Test
  public void testStaticMethod() throws IOException {
    init_static_example();

    checkExpr("A.test()", "void");
  }

  @Test
  public void testInvalidStaticMethod() throws IOException {
    init_static_example();

    checkErrorExpr("B.test()", "0xF777F");
  }

  @Test
  public void testMissingTypeQualified() throws IOException {
    checkErrorMCType("pac.kage.not.present.Type", "0xA0324");
  }

  @Test
  public void testMissingFieldQualified() throws IOException {
    init_static_example();

    checkErrorExpr("B.notPresentField", "0xF777F");
  }

  @Test
  public void testMissingFieldUnqualified() throws IOException {
    checkErrorExpr("notPresentField", "0xFD118");
  }

  @Test
  public void testMissingMethodQualified() throws IOException {
    checkErrorExpr("pac.kage.not.present.Type.method()", "0xF777F");
  }

  @Test
  public void testSubClassesKnowsStaticMethodsOfSuperClasses() throws IOException {
    init_static_example();

    checkExpr("C.test()", "void");
  }

  @Test
  public void testSubClassesKnowsStaticFieldsOfSuperClasses() throws IOException {
    init_static_example();

    checkExpr("C.field", "int");
  }

  @Test
  public void testSubClassesKnowsStaticTypesOfSuperClasses() throws IOException {
    init_static_example();

    checkType("C.D", "A.D");
    checkExpr("C.D.x", "int");
  }

  /**
   * test if we can use functions and variables
   * as e.g. imported by Class2MC
   */
  @Test
  public void testDoNotFilterBasicTypes() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    TypeSymbol A = inScope(gs,
        type("A",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(function("func", _voidSymType)),
            List.of(variable("var", _booleanSymType))
        )
    );
    inScope(gs, variable("a", createTypeObject(A)));

    // todo copied from typecheck 1 tests, but to be discussed
    // functions are available as if they were static
    checkExpr("A.func()", "void");
    //checkExpr("a.func()", "void");

    // variables are available as if they were non-static
    //checkErrorExpr("A.var", "0xA0241");
    //checkExpr("a.var", "boolean");
  }

  protected void init_method_test() {
    //see MC Ticket #3298 for this example, use test instead of bar because bar is a keyword in CombineExpressions
    //create types A, B and C, B extends A, C extends B
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    TypeSymbol a = inScope(globalScope, type("A"));
    SymTypeExpression aSym = createTypeObject(a);
    TypeSymbol b = inScope(globalScope, type("B", Lists.newArrayList(aSym)));
    SymTypeExpression bSym = createTypeObject(b);
    TypeSymbol c = inScope(globalScope, type("C", Lists.newArrayList(bSym)));
    SymTypeExpression cSym = createTypeObject(c);

    inScope(globalScope, method("foo", aSym, aSym));
    inScope(globalScope, method("foo", bSym, bSym));
    inScope(globalScope, method("foo", cSym, cSym));

    inScope(globalScope, method("foo", aSym, aSym, aSym));
    inScope(globalScope, method("foo", bSym, aSym, bSym));
    inScope(globalScope, method("foo", cSym, aSym, cSym));

    inScope(globalScope, method("foo2", aSym, aSym, bSym, cSym));
    inScope(globalScope, method("foo2", aSym, cSym, cSym, aSym));

    inScope(globalScope, method("foo2", bSym, aSym, bSym));
    inScope(globalScope, method("foo2", cSym, cSym, aSym));

    inScope(globalScope, field("a", aSym));
    inScope(globalScope, field("b", bSym));
    inScope(globalScope, field("c", cSym));
  }

  @Test
  public void testCorrectMethodChosen() throws IOException {
    init_method_test();

    /*
    available methods:
    A foo(A x)
    B foo(B x)
    C foo(C x)
    A foo(A x, A y)
    B foo(A x, B y)
    C foo(A x, C y)

    A foo2(A x, B y, C z)
    C foo2(C x, C y, A z)
    B foo2(A x, B y)
    C foo2(C x, A y)
    */

    checkExpr("foo(a)", "A");
    checkExpr("foo(b)", "B");
    checkExpr("foo(c)", "C");

    checkExpr("foo(a, a)", "A");
    checkExpr("foo(a, b)", "B");
    checkExpr("foo(a, c)", "C");
    checkExpr("foo(b, a)", "A");
    checkExpr("foo(b, b)", "B");
    checkExpr("foo(b, c)", "C");
    checkExpr("foo(c, a)", "A");
    checkExpr("foo(c, b)", "B");
    checkExpr("foo(c, c)", "C");

    checkErrorExpr("foo2(c, c, c)", "0xFDCBA");

    checkErrorExpr("foo2(a, a)", "0xFDABE");
    checkErrorExpr("foo2(b, a)", "0xFDABE");
    checkErrorExpr("foo2(c, b)", "0xFDCBA");
    checkErrorExpr("foo2(c, c)", "0xFDCBA");

    checkExpr("foo2(a, b)", "B");
    checkExpr("foo2(a, c)", "B");
    checkExpr("foo2(b, b)", "B");
    checkExpr("foo2(b, c)", "B");
    checkExpr("foo2(c, a)", "C");
  }
}
