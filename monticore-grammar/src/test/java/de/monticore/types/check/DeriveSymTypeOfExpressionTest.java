/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfExpressionTest extends DeriveSymTypeAbstractTest {
  
  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */
  
  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    // Setting up a Scope Infrastructure (without a global Scope)
    ICombineExpressionsWithLiteralsScope scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setEnclosingScope(null);       // No enclosing Scope: Search ending here
    scope.setExportingSymbols(true);
    scope.setAstNode(null);
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)

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
        .setSpannedScope(OOSymbolsMill.scope())
        .setEnclosingScope(scope)
        .setName("GenSuper")
        .build();
    genSuperType.addTypeVarSymbol(genArgs);
    SymTypeExpression genArg = SymTypeExpressionFactory.createTypeVariable("GenArg",scope);
    SymTypeExpression genSuper = SymTypeExpressionFactory.createGenerics("GenSuper",scope,genArg);
    OOTypeSymbol genSubType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("GenSub")
        .setSuperTypesList(Lists.newArrayList(genSuper))
        .setEnclosingScope(scope)
        .build();
    genSubType.addTypeVarSymbol(genArgs);
    SymTypeExpression genSub = SymTypeExpressionFactory.createGenerics("GenSub", scope, genArg);
    FieldSymbol genSubField = field("genericSub",genSub);
    FieldSymbol genSuperField = field("genericSuper",genSuper);
    add2scope(scope,genSuperType);
    add2scope(scope,genSubType);
    add2scope(scope,genSubField);
    add2scope(scope,genSuperField);

    setFlatExpressionScopeSetter(scope);
  }

  @Override
  protected void setupTypeCheck() {
    // This is an auxiliary
    FullDeriveFromCombineExpressionsWithLiterals derLit = new FullDeriveFromCombineExpressionsWithLiterals();

    // other arguments not used (and therefore deliberately null)
    // This is the TypeChecker under Test:
    setTypeCheck(new TypeCalculator(null, derLit));
  }

  // Parser used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
  @Override
  protected Optional<ASTExpression> parseStringExpression(String expression) throws IOException {
    return p.parse_StringExpression(expression);
  }

  @Override
  protected ExpressionsBasisTraverser getUsedLanguageTraverser() {
    return CombineExpressionsWithLiteralsMill.traverser();
  }
  
  // ------------------------------------------------------  Tests for Function 2


  @Test
  public void deriveTFromASTNameExpression() throws IOException {
    check("foo", "int");
  }

  @Test
  public void deriveTFromASTNameExpression2() throws IOException {
    check("bar2", "boolean");
  }

  @Test
  public void deriveTFromASTNameExpression3() throws IOException{
    check("person1", "Person");
  }

  @Test
  public void deriveTFromASTNameExpression4() throws IOException{
    check("student1", "Student");
  }

  @Test
  public void deriveTFromASTNameExpression5() throws IOException{
    check("firstsemester", "FirstSemesterStudent");
  }

   @Test
  public void deriveTFromLiteral() throws IOException {
    check("42", "int");
  }

  @Test
  public void deriveTFromLiteralString() throws IOException {
    check("\"aStringi\"", "String");
  }

  @Test
  public void genericsTest() throws IOException {
    check("genericSuper = genericSub", "GenSuper<GenArg>");
  }

}
