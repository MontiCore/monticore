/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfExpressionTest {
  
  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */
  private ExpressionsBasisScope scope;
  
  @BeforeClass
  public static void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }
  
  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
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
    add2scope(scope,p);
    TypeSymbol s = new TypeSymbol("Student");
    add2scope(scope,s);
    s.setSuperTypeList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    TypeSymbol f = new TypeSymbol("FirstSemesterStudent");
    add2scope(scope,f);
    f.setSuperTypeList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("Person",scope)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",scope)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",scope)));
    add2scope(scope,field("student2",SymTypeExpressionFactory.createTypeObject("Student",scope)));
    add2scope(scope,field("firstsemester",SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent",scope)));

    //testing for generics
    TypeVarSymbol genArgs = typeVariable("GenArg");
    TypeSymbol genSuperType = type("GenSuper",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(genArgs),scope);
    SymTypeExpression genArg = SymTypeExpressionFactory.createTypeVariable(new TypeSymbolLoader("GenArg",scope));
    SymTypeExpression genSuper = SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader("GenSuper",scope),genArg);
    TypeSymbol genSubType = type("GenSub",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(genSuper),Lists.newArrayList(genArgs),scope);
    SymTypeExpression genSub = SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader("GenSub",scope),genArg);
    FieldSymbol genSubField = field("genericSub",genSub);
    FieldSymbol genSuperField = field("genericSuper",genSuper);
    add2scope(scope,genSuperType);
    add2scope(scope,genSubType);
    add2scope(scope,genSubField);
    add2scope(scope,genSuperField);
    derLit.setScope(scope);
  }
  
  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
  
  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build(), new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);
  
  // ------------------------------------------------------  Tests for Function 2


  @Test
  public void deriveTFromASTNameExpression() throws IOException {
    ASTExpression astex = p.parse_StringExpression("foo").get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression2() throws IOException {
    String s = "bar2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression3() throws IOException{
    String s = "person1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("Person", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression4() throws IOException{
    String s = "student1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("Student",tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromASTNameExpression5() throws IOException{
    String s = "firstsemester";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("FirstSemesterStudent",tc.typeOf(astex).print());
  }

   @Test
  public void deriveTFromLiteral() throws IOException {
    ASTExpression astex = p.parse_StringExpression("42").get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromLiteralString() throws IOException {
    ASTExpression astex = p.parse_StringExpression("\"aStringi\"").get();
    assertEquals("String", tc.typeOf(astex).print());
  }

  @Test
  public void genericsTest() throws IOException {
    ASTExpression astex = p.parse_StringExpression("genericSuper = genericSub").get();
    assertEquals("GenSuper<GenArg>",tc.typeOf(astex).print());
  }

}
