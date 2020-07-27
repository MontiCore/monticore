// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static de.monticore.types.check.TypeCheck.isSubtypeOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Class for {@link TypeCheck}
 */
public class TypeCheckTest {

  private ICombineExpressionsWithLiteralsScope scope;
  private TypeCheck tc = new TypeCheck(null, new DeriveSymTypeOfCombineExpressionsDelegator());
  private CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
  private FlatExpressionScopeSetter flatExpressionScopeSetter;

  @BeforeClass
  public static void setup() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
    scope =
        CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder()
            .setEnclosingScope(null)       // No enclosing Scope: Search ending here
            .setExportingSymbols(true)
            .setAstNode(null)
            .setName("Phantasy2").build();     // hopefully unused
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)
    add2scope(scope, DefsTypeBasic._int);
    add2scope(scope, DefsTypeBasic._char);
    add2scope(scope, DefsTypeBasic._boolean);
    add2scope(scope, DefsTypeBasic._double);
    add2scope(scope, DefsTypeBasic._float);
    add2scope(scope, DefsTypeBasic._long);

    add2scope(scope, DefsTypeBasic._array);
    add2scope(scope, DefsTypeBasic._Object);
    add2scope(scope, DefsTypeBasic._String);

    // some FieldSymbols (ie. Variables, Attributes)
    OOTypeSymbol p = new OOTypeSymbol("Person");
    p.setEnclosingScope(scope);
    scope.add(p);
    OOTypeSymbol s = new OOTypeSymbol("Student");
    s.setEnclosingScope(scope);
    scope.add(s);
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    f.setEnclosingScope(scope);
    scope.add(f);
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
    ASTExpression bool1 = p.parse_StringExpression("true").get();
    bool1.accept(flatExpressionScopeSetter);
    ASTExpression bool2 = p.parse_StringExpression("false").get();
    bool2.accept(flatExpressionScopeSetter);
    ASTExpression float1 = p.parse_StringExpression("3.4f").get();
    float1.accept(flatExpressionScopeSetter);
    ASTExpression int1 = p.parse_StringExpression("3").get();
    int1.accept(flatExpressionScopeSetter);
    ASTExpression double1 = p.parse_StringExpression("3.46").get();
    double1.accept(flatExpressionScopeSetter);
    ASTExpression long1 = p.parse_StringExpression("5L").get();
    long1.accept(flatExpressionScopeSetter);
    ASTExpression char1 = p.parse_StringExpression("\'a\'").get();
    char1.accept(flatExpressionScopeSetter);

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
    pers.accept(flatExpressionScopeSetter);
    ASTExpression stud = p.parse_StringExpression("Student").get();
    stud.accept(flatExpressionScopeSetter);
    ASTExpression fstud = p.parse_StringExpression("FirstSemesterStudent").get();
    fstud.accept(flatExpressionScopeSetter);

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
    ASTExpression bool1 = p.parse_StringExpression("true").get();
    bool1.accept(flatExpressionScopeSetter);
    ASTExpression bool2 = p.parse_StringExpression("false").get();
    bool2.accept(flatExpressionScopeSetter);
    ASTExpression float1 = p.parse_StringExpression("3.4f").get();
    float1.accept(flatExpressionScopeSetter);
    ASTExpression int1 = p.parse_StringExpression("3").get();
    int1.accept(flatExpressionScopeSetter);
    ASTExpression double1 = p.parse_StringExpression("3.46").get();
    double1.accept(flatExpressionScopeSetter);
    ASTExpression long1 = p.parse_StringExpression("5L").get();
    long1.accept(flatExpressionScopeSetter);
    ASTExpression char1 = p.parse_StringExpression("\'a\'").get();
    char1.accept(flatExpressionScopeSetter);


    assertFalse(isSubtypeOf(tc.typeOf(bool1), tc.typeOf(bool2)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(double1)));
    assertFalse(isSubtypeOf(tc.typeOf(int1), tc.typeOf(bool1)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(float1)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(long1)));
    assertFalse(isSubtypeOf(tc.typeOf(char1), tc.typeOf(char1)));
    assertFalse(isSubtypeOf(tc.typeOf(int1), tc.typeOf(char1)));
    assertFalse(isSubtypeOf(tc.typeOf(bool1), tc.typeOf(double1)));
    assertFalse(isSubtypeOf(tc.typeOf(float1), tc.typeOf(long1)));
    assertTrue(isSubtypeOf(tc.typeOf(int1), tc.typeOf(float1)));

    //non-primitives
    ASTExpression pers = p.parse_StringExpression("Person").get();
    pers.accept(flatExpressionScopeSetter);
    ASTExpression stud = p.parse_StringExpression("Student").get();
    stud.accept(flatExpressionScopeSetter);
    ASTExpression fstud = p.parse_StringExpression("FirstSemesterStudent").get();
    fstud.accept(flatExpressionScopeSetter);

    assertTrue(isSubtypeOf(tc.typeOf(stud), tc.typeOf(pers)));
    assertTrue(isSubtypeOf(tc.typeOf(fstud), tc.typeOf(pers)));
    assertTrue(isSubtypeOf(tc.typeOf(fstud), tc.typeOf(stud)));
    assertFalse(isSubtypeOf(tc.typeOf(pers), tc.typeOf(stud)));
    assertFalse(isSubtypeOf(tc.typeOf(pers), tc.typeOf(fstud)));
    assertFalse(isSubtypeOf(tc.typeOf(stud), tc.typeOf(fstud)));
    assertFalse(isSubtypeOf(tc.typeOf(pers), tc.typeOf(pers)));

    assertFalse(isSubtypeOf(tc.typeOf(int1), tc.typeOf(pers)));
  }
}
