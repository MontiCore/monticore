/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfExpressionTest {
  
  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */
  private ICombineExpressionsWithLiteralsScope scope;
  private FlatExpressionScopeSetter flatExpressionScopeSetter;
  
  @BeforeClass
  public static void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }
  
  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
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
    add2scope(scope,p);
    OOTypeSymbol s = new OOTypeSymbol("Student");
    add2scope(scope,s);
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    add2scope(scope,f);
    f.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("Person",scope)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",scope)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",scope)));
    add2scope(scope,field("student2",SymTypeExpressionFactory.createTypeObject("Student",scope)));
    add2scope(scope,field("firstsemester",SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent",scope)));

    //testing for generics
    TypeVarSymbol genArgs = typeVariable("GenArg");
    OOTypeSymbol genSuperType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setEnclosingScope(scope)
        .setName("GenSuper")
        .build();
    genSuperType.addTypeVarSymbol(genArgs);
    SymTypeExpression genArg = SymTypeExpressionFactory.createTypeVariable("GenArg",scope);
    SymTypeExpression genSuper = SymTypeExpressionFactory.createGenerics("GenSuper",scope,genArg);
    OOTypeSymbol genSubType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("GenSub").setSuperTypesList(Lists.newArrayList(genSuper))
        .setEnclosingScope(scope)
        .build();
    genSubType.addTypeVarSymbol(genArgs);
    SymTypeExpression genSub = SymTypeExpressionFactory.createGenerics("GenSub",scope,genArg);
    FieldSymbol genSubField = field("genericSub",genSub);
    FieldSymbol genSuperField = field("genericSuper",genSuper);
    add2scope(scope,genSuperType);
    add2scope(scope,genSubType);
    add2scope(scope,genSubField);
    add2scope(scope,genSuperField);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);
  }
  
  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
  
  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);
  
  // ------------------------------------------------------  Tests for Function 2


  @Test
  public void deriveTFromASTNameExpression() throws IOException {
    ASTExpression astex = p.parse_StringExpression("foo").get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression2() throws IOException {
    String s = "bar2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression3() throws IOException{
    String s = "person1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("Person", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression4() throws IOException{
    String s = "student1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("Student",tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression5() throws IOException{
    String s = "firstsemester";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("FirstSemesterStudent",tc.typeOf(astex).print());
  }

   @Test
  public void deriveTFromLiteral() throws IOException {
    ASTExpression astex = p.parse_StringExpression("42").get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromLiteralString() throws IOException {
    ASTExpression astex = p.parse_StringExpression("\"aStringi\"").get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("String", tc.typeOf(astex).print());
  }

  @Test
  public void genericsTest() throws IOException {
    ASTExpression astex = p.parse_StringExpression("genericSuper = genericSub").get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("GenSuper<GenArg>",tc.typeOf(astex).print());
  }

}
