package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfJavaClassExpressionsTest {

  private ExpressionsBasisScope scope;

  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
    scope =
        ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
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
    TypeSymbol p = new TypeSymbol("Person");
    scope.add(p);
    TypeSymbol s = new TypeSymbol("Student");
    scope.add(s);
    s.setSuperTypeList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    TypeSymbol f = new TypeSymbol("FirstSemesterStudent");
    scope.add(f);
    f.setSuperTypeList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
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
    derLit.setScope(scope);

    LogStub.init();
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressions derLit = new DeriveSymTypeOfCombineExpressions(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build());

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/


  @Test
  public void deriveFromPrimarySuperExpression() throws IOException {
    TypeSymbol supType = type("A",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    SymTypeExpression sup = SymTypeExpressionFactory.createTypeObject("A",scope);
    supType.setClass(true);

    MethodSymbol get = method("get",_voidSymType);
    TypeSymbol p = type("AB",Lists.newArrayList(get),Lists.newArrayList(),Lists.newArrayList(sup),Lists.newArrayList(),scope);
    add2scope(scope,p);

    //use the spannedscope of the type
    derLit.setScope((ExpressionsBasisScope)p.getSpannedScope());
    tc = new TypeCheck(null, derLit);
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> a = parser.parse_StringExpression("super");
    assertTrue(a.isPresent());
    assertEquals("A",tc.typeOf(a.get()).print());

    //use the spannedscope of the method
    derLit.setScope((ExpressionsBasisScope)get.getSpannedScope());
    tc = new TypeCheck(null, derLit);

    assertEquals("A",tc.typeOf(a.get()).print());
  }

  @Test
  public void deriveFromPrimaryThisExpression() throws IOException {
    MethodSymbol get = method("get",_voidSymType);
    TypeSymbol p = type("AB",Lists.newArrayList(get),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,p);

    //use the spannedscope of the type
    derLit.setScope((ExpressionsBasisScope)p.getSpannedScope());
    tc = new TypeCheck(null, derLit);
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> a = parser.parse_StringExpression("this");
    assertTrue(a.isPresent());
    assertEquals("AB",tc.typeOf(a.get()).print());

    //use the spannedscope of the method
    derLit.setScope((ExpressionsBasisScope)get.getSpannedScope());
    tc = new TypeCheck(null, derLit);

    assertEquals("AB",tc.typeOf(a.get()).print());
  }

}
