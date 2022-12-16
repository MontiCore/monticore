/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types.check.DefsTypeBasic.*;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Class for {@link TypeCalculator}
 */
public class TypeCalculatorTest {

  private ICombineExpressionsWithLiteralsScope scope;
  private TypeCalculator tc = new TypeCalculator(
      new FullSynthesizeFromCombineExpressionsWithLiterals(),
      new FullDeriveFromCombineExpressionsWithLiterals(),
      new TypeRelations()
  );
  private CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
  private FlatExpressionScopeSetter flatExpressionScopeSetter;

  @Before
  public void setupForEach() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    
    // Setting up a Scope Infrastructure (without a global Scope)
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    DefsTypeBasic.setup();
    scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setEnclosingScope(null);       // No enclosing Scope: Search ending here
    scope.setExportingSymbols(true);
    scope.setAstNode(null);     // hopefully unused
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)

    add2scope(scope, DefsTypeBasic._array);
    add2scope(scope, DefsTypeBasic._Object);
    add2scope(scope, DefsTypeBasic._String);

    // some FieldSymbols (ie. Variables, Attributes)
    OOTypeSymbol p = new OOTypeSymbol("Person");
    p.setEnclosingScope(scope);
    add2scope(scope, p);
    OOTypeSymbol s = new OOTypeSymbol("Student");
    s.setEnclosingScope(scope);
    add2scope(scope,s);
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    f.setEnclosingScope(scope);
    add2scope(scope,f);
    f.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("vardouble", _doubleSymType));
    add2scope(scope, field("varchar", _charSymType));
    add2scope(scope, field("varfloat", _floatSymType));
    add2scope(scope, field("varlong", _longSymType));
    add2scope(scope, field("varint", _intSymType));
    add2scope(scope, field("varString", SymTypeExpressionFactory.createTypeObject("String", scope)));
    add2scope(scope, field("person1", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("person2", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("student1", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("student2", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("firstsemester", SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent", scope)));
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);
    LogStub.init();
  }

  @Test
  public void testIsOfTypeForAssign() throws IOException {
    //primitives
    CombineExpressionsWithLiteralsTraverser traverser = getTraverser(flatExpressionScopeSetter);
    ASTExpression bool1 = p.parse_StringExpression("true").get();
    bool1.accept(traverser);
    ASTExpression bool2 = p.parse_StringExpression("false").get();
    bool2.accept(traverser);
    ASTExpression float1 = p.parse_StringExpression("3.4f").get();
    float1.accept(traverser);
    ASTExpression int1 = p.parse_StringExpression("3").get();
    int1.accept(traverser);
    ASTExpression double1 = p.parse_StringExpression("3.46").get();
    double1.accept(traverser);
    ASTExpression long1 = p.parse_StringExpression("5L").get();
    long1.accept(traverser);
    ASTExpression char1 = p.parse_StringExpression("\'a\'").get();
    char1.accept(traverser);

    assertTrue(tc.isOfTypeForAssign(tc.typeOf(bool1), bool2));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(double1), int1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(bool1), int1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(float1), int1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(long1), int1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(char1), char1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(char1), int1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(double1), bool1));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(long1), float1));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(float1), int1));

    //non-primitives
    ASTExpression pers = p.parse_StringExpression("Person").get();
    pers.accept(traverser);
    ASTExpression stud = p.parse_StringExpression("Student").get();
    stud.accept(traverser);
    ASTExpression fstud = p.parse_StringExpression("FirstSemesterStudent").get();
    fstud.accept(traverser);

    assertTrue(tc.isOfTypeForAssign(tc.typeOf(pers), stud));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(pers), fstud));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(stud), fstud));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(stud), pers));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(fstud), pers));
    assertFalse(tc.isOfTypeForAssign(tc.typeOf(fstud), stud));
    assertTrue(tc.isOfTypeForAssign(tc.typeOf(pers), pers));

    assertFalse(tc.isOfTypeForAssign(tc.typeOf(int1), pers));
  }

  @Test
  public void testIsSubtype() throws IOException {
    //primitives
    CombineExpressionsWithLiteralsTraverser traverser = getTraverser(flatExpressionScopeSetter);
    ASTExpression bool1 = p.parse_StringExpression("true").get();
    bool1.accept(traverser);
    ASTExpression bool2 = p.parse_StringExpression("false").get();
    bool2.accept(traverser);
    ASTExpression float1 = p.parse_StringExpression("3.4f").get();
    float1.accept(traverser);
    ASTExpression int1 = p.parse_StringExpression("3").get();
    int1.accept(traverser);
    ASTExpression double1 = p.parse_StringExpression("3.46").get();
    double1.accept(traverser);
    ASTExpression long1 = p.parse_StringExpression("5L").get();
    long1.accept(traverser);
    ASTExpression char1 = p.parse_StringExpression("\'a\'").get();
    char1.accept(traverser);


    assertTrue(tc.isSubtypeOf(tc.typeOf(bool1), tc.typeOf(bool2)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(int1), tc.typeOf(double1)));
    assertFalse(tc.isSubtypeOf(tc.typeOf(int1), tc.typeOf(bool1)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(int1), tc.typeOf(float1)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(int1), tc.typeOf(long1)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(char1), tc.typeOf(char1)));
    assertFalse(tc.isSubtypeOf(tc.typeOf(int1), tc.typeOf(char1)));
    assertFalse(tc.isSubtypeOf(tc.typeOf(bool1), tc.typeOf(double1)));
    assertFalse(tc.isSubtypeOf(tc.typeOf(float1), tc.typeOf(long1)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(int1), tc.typeOf(float1)));

    //non-primitives
    ASTExpression pers = p.parse_StringExpression("Person").get();
    pers.accept(traverser);
    ASTExpression stud = p.parse_StringExpression("Student").get();
    stud.accept(traverser);
    ASTExpression fstud = p.parse_StringExpression("FirstSemesterStudent").get();
    fstud.accept(traverser);

    assertTrue(tc.isSubtypeOf(tc.typeOf(stud), tc.typeOf(pers)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(fstud), tc.typeOf(pers)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(fstud), tc.typeOf(stud)));
    assertFalse(tc.isSubtypeOf(tc.typeOf(pers), tc.typeOf(stud)));
    assertFalse(tc.isSubtypeOf(tc.typeOf(pers), tc.typeOf(fstud)));
    assertFalse(tc.isSubtypeOf(tc.typeOf(stud), tc.typeOf(fstud)));
    assertTrue(tc.isSubtypeOf(tc.typeOf(pers), tc.typeOf(pers)));

    assertFalse(tc.isSubtypeOf(tc.typeOf(int1), tc.typeOf(pers)));
  }

  @Test
  public void testCompatibilityForPrimitives() {
    SymTypeExpression booleanT = createPrimitive(BasicSymbolsMill.BOOLEAN);
    SymTypeExpression byteT = createPrimitive(BasicSymbolsMill.BYTE);
    SymTypeExpression shortT = createPrimitive(BasicSymbolsMill.SHORT);
    SymTypeExpression charT = createPrimitive(BasicSymbolsMill.CHAR);
    SymTypeExpression intT = createPrimitive(BasicSymbolsMill.INT);
    SymTypeExpression longT = createPrimitive(BasicSymbolsMill.LONG);
    SymTypeExpression floatT = createPrimitive(BasicSymbolsMill.FLOAT);
    SymTypeExpression doubleT = createPrimitive(BasicSymbolsMill.DOUBLE);

    assertTrue(tc.compatible(booleanT, booleanT));
    assertTrue(tc.compatible(byteT, byteT));
    assertTrue(tc.compatible(shortT, byteT));
    assertTrue(tc.compatible(shortT, shortT));
    assertTrue(tc.compatible(charT, charT));
    assertTrue(tc.compatible(intT, byteT));
    assertTrue(tc.compatible(intT, shortT));
    assertTrue(tc.compatible(intT, charT));
    assertTrue(tc.compatible(intT, intT));
    assertTrue(tc.compatible(longT, byteT));
    assertTrue(tc.compatible(longT, shortT));
    assertTrue(tc.compatible(longT, charT));
    assertTrue(tc.compatible(longT, intT));
    assertTrue(tc.compatible(longT, longT));
    assertTrue(tc.compatible(floatT, byteT));
    assertTrue(tc.compatible(floatT, shortT));
    assertTrue(tc.compatible(floatT, charT));
    assertTrue(tc.compatible(floatT, intT));
    assertTrue(tc.compatible(floatT, longT));
    assertTrue(tc.compatible(floatT, floatT));
    assertTrue(tc.compatible(doubleT, byteT));
    assertTrue(tc.compatible(doubleT, shortT));
    assertTrue(tc.compatible(doubleT, charT));
    assertTrue(tc.compatible(doubleT, intT));
    assertTrue(tc.compatible(doubleT, longT));
    assertTrue(tc.compatible(doubleT, floatT));
    assertTrue(tc.compatible(doubleT, doubleT));
  }

  @Test
  public void testIncompatibilityForPrimitives() {
    SymTypeExpression booleanT = createPrimitive(BasicSymbolsMill.BOOLEAN);
    SymTypeExpression byteT = createPrimitive(BasicSymbolsMill.BYTE);
    SymTypeExpression shortT = createPrimitive(BasicSymbolsMill.SHORT);
    SymTypeExpression charT = createPrimitive(BasicSymbolsMill.CHAR);
    SymTypeExpression intT = createPrimitive(BasicSymbolsMill.INT);
    SymTypeExpression longT = createPrimitive(BasicSymbolsMill.LONG);
    SymTypeExpression floatT = createPrimitive(BasicSymbolsMill.FLOAT);
    SymTypeExpression doubleT = createPrimitive(BasicSymbolsMill.DOUBLE);

    assertFalse(tc.compatible(booleanT, byteT));
    assertFalse(tc.compatible(booleanT, shortT));
    assertFalse(tc.compatible(booleanT, charT));
    assertFalse(tc.compatible(booleanT, intT));
    assertFalse(tc.compatible(booleanT, longT));
    assertFalse(tc.compatible(booleanT, floatT));
    assertFalse(tc.compatible(booleanT, doubleT));
    assertFalse(tc.compatible(byteT, booleanT));
    assertFalse(tc.compatible(byteT, shortT));
    assertFalse(tc.compatible(byteT, charT));
    assertFalse(tc.compatible(byteT, intT));
    assertFalse(tc.compatible(byteT, longT));
    assertFalse(tc.compatible(byteT, floatT));
    assertFalse(tc.compatible(byteT, doubleT));
    assertFalse(tc.compatible(shortT, booleanT));
    assertFalse(tc.compatible(shortT, charT));
    assertFalse(tc.compatible(shortT, intT));
    assertFalse(tc.compatible(shortT, longT));
    assertFalse(tc.compatible(shortT, floatT));
    assertFalse(tc.compatible(shortT, doubleT));
    assertFalse(tc.compatible(charT, booleanT));
    assertFalse(tc.compatible(charT, byteT));
    assertFalse(tc.compatible(charT, shortT));
    assertFalse(tc.compatible(charT, intT));
    assertFalse(tc.compatible(charT, longT));
    assertFalse(tc.compatible(charT, floatT));
    assertFalse(tc.compatible(charT, doubleT));
    assertFalse(tc.compatible(intT, booleanT));
    assertFalse(tc.compatible(intT, longT));
    assertFalse(tc.compatible(intT, floatT));
    assertFalse(tc.compatible(intT, doubleT));
    assertFalse(tc.compatible(longT, booleanT));
    assertFalse(tc.compatible(longT, floatT));
    assertFalse(tc.compatible(longT, doubleT));
    assertFalse(tc.compatible(floatT, booleanT));
    assertFalse(tc.compatible(floatT, doubleT));
    assertFalse(tc.compatible(doubleT, booleanT));
  }

  @Test
  public void testCompatibilityForGenerics() {
    // Given
    // Building ootype List<T>; Instantiating List<Person>
    OOTypeSymbol listSym = provideGeneric("List", "T");
    OOSymbolsMill.globalScope().add(listSym);
    OOSymbolsMill.globalScope().addSubScope(listSym.getSpannedScope());
    SymTypeExpression personExpr = SymTypeExpressionFactory.createTypeObject(scope.resolveOOType("Person").get());
    SymTypeExpression listOfPersonExpr = SymTypeExpressionFactory.createGenerics(listSym, personExpr);

    // Building ootype PersonList extends List<Person>; Instantiating PersonList
    OOTypeSymbol personListSym = provideOOType("PersonList");
    personListSym.addSuperTypes(listOfPersonExpr);
    OOSymbolsMill.globalScope().add(personListSym);
    OOSymbolsMill.globalScope().addSubScope(personListSym.getSpannedScope());
    SymTypeExpression personListExpr = SymTypeExpressionFactory.createTypeObject(personListSym);

    // Building ootype LinkedList<U> extends List<U>; Instantiating LinkedList<Person>
    OOTypeSymbol linkedListSym = provideGeneric("LinkedList", "U");
    OOSymbolsMill.globalScope().add(linkedListSym);
    OOSymbolsMill.globalScope().addSubScope(linkedListSym.getSpannedScope());
    SymTypeVariable linkedlistTypeVar = SymTypeExpressionFactory.createTypeVariable(linkedListSym.getTypeParameterList().get(0));
    SymTypeExpression linkedListParExpr = SymTypeExpressionFactory.createGenerics(listSym, linkedlistTypeVar);
    linkedListSym.addSuperTypes(linkedListParExpr);
    SymTypeExpression linkedlistOfPersonExpr = SymTypeExpressionFactory.createGenerics(linkedListSym, personExpr);

    // When & Then
    Assert.assertTrue(tc.compatible(listOfPersonExpr, listOfPersonExpr));
    Assert.assertTrue(tc.compatible(listOfPersonExpr, personListExpr));
    Assert.assertTrue(tc.compatible(listOfPersonExpr, linkedlistOfPersonExpr));
  }

  @Test
  public void testIncompatibilityForGenerics() {
    // Given
    // Building ootype List<T>; Instantiating List<Person>, List<int>, List<boolean>
    OOTypeSymbol listSym = provideGeneric("List", "T");
    OOSymbolsMill.globalScope().add(listSym);
    OOSymbolsMill.globalScope().addSubScope(listSym.getSpannedScope());
    SymTypeExpression personExpr = SymTypeExpressionFactory.createTypeObject(scope.resolveOOType("Person").get());
    SymTypeExpression listOfPersonExpr = SymTypeExpressionFactory.createGenerics(listSym, personExpr);
    SymTypeExpression listOfIntExpr = SymTypeExpressionFactory.createGenerics(listSym, _intSymType);
    SymTypeExpression listOfBoolExpr = SymTypeExpressionFactory.createGenerics(listSym, _booleanSymType);

    // Building ootype PersonList extends List<Person>; Instantiating PersonList
    OOTypeSymbol personListSym = provideOOType("PersonList");
    personListSym.addSuperTypes(listOfPersonExpr);
    OOSymbolsMill.globalScope().add(personListSym);
    OOSymbolsMill.globalScope().addSubScope(personListSym.getSpannedScope());
    SymTypeExpression personListExpr = SymTypeExpressionFactory.createTypeObject(personListSym);

    // When & Then
    Assert.assertFalse(tc.compatible(listOfIntExpr, _intSymType));
    Assert.assertFalse(tc.compatible(listOfIntExpr, listOfBoolExpr));
    Assert.assertFalse(tc.compatible(listOfBoolExpr, listOfIntExpr));
    Assert.assertFalse(tc.compatible(listOfBoolExpr, listOfPersonExpr));
    Assert.assertFalse(tc.compatible(listOfPersonExpr, listOfBoolExpr));
    Assert.assertFalse(tc.compatible(listOfBoolExpr, personListExpr));
    Assert.assertFalse(tc.compatible(personListExpr, listOfBoolExpr));
  }

  public CombineExpressionsWithLiteralsTraverser getTraverser(FlatExpressionScopeSetter flatExpressionScopeSetter){
    CombineExpressionsWithLiteralsTraverser traverser = CombineExpressionsWithLiteralsMill.traverser();
    traverser.add4AssignmentExpressions(flatExpressionScopeSetter);
    traverser.add4BitExpressions(flatExpressionScopeSetter);
    traverser.add4CommonExpressions(flatExpressionScopeSetter);
    traverser.add4ExpressionsBasis(flatExpressionScopeSetter);
    traverser.add4JavaClassExpressions(flatExpressionScopeSetter);
    traverser.add4MCBasicTypes(flatExpressionScopeSetter);
    traverser.add4MCCommonLiterals(flatExpressionScopeSetter);
    return traverser;
  }


  protected static OOTypeSymbol provideOOType(String name) {
    return DefsTypeBasic.type(name);
  }

  protected static OOTypeSymbol provideGeneric(String rawName, String... typeVarNames) {
    List<TypeVarSymbol> typeVars = Arrays.stream(typeVarNames)
      .map(tVarName -> OOSymbolsMill.typeVarSymbolBuilder()
        .setName(tVarName)
        .setSpannedScope(OOSymbolsMill.scope())
        .build()
      ).collect(Collectors.toList());

    return DefsTypeBasic.type(rawName, new ArrayList<>(), typeVars);
  }
}
