/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.literals.mccommonliterals._ast.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.typesymbols._ast.TypeSymbolsMill;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;
import de.monticore.typescalculator.CombineExpressionsWithLiteralsTypesCalculator;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types2.DefsTypeBasic.*;
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
                    .setExportsSymbols(true)
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
    TypeSymbol s = new TypeSymbol("Student");
    s.setSuperTypes(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person",p)));
    TypeSymbol f = new TypeSymbol("FirstSemesterStudent");
    f.setSuperTypes(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("Person",p)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",p)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope,field("student2",SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope,field("firstsemester",SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent",f)));
    derLit.setScope(scope);
  }
  
  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
  
  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  CombineExpressionsWithLiteralsTypesCalculator derLit = new CombineExpressionsWithLiteralsTypesCalculator(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build());
  
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


/*
  // This one is for the CommonExpressions (to be moved)
  @Test
  public void deriveTFromASTNameExpression2() throws IOException {
    ASTExpression astex = p.parse_StringExpression("!bar").get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void deriveTFromNot() throws IOException {
    String s = "!(3+aBoolean)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
    // does work, but should issue a CoCo violation (TODO later)
  }
*/

}
