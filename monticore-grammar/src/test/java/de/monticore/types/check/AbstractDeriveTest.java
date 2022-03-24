/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.abstracttypechecktest.AbstractTypeCheckTestMill;
import de.monticore.expressions.abstracttypechecktest._parser.AbstractTypeCheckTestParser;
import de.monticore.expressions.abstracttypechecktest._symboltable.IAbstractTypeCheckTestScope;
import de.monticore.expressions.abstracttypechecktest._visitor.AbstractTypeCheckTestTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractDeriveTest {

  private IAbstractTypeCheckTestScope scope;
  private FlatExpressionScopeSetter flatExpressionScopeSetter;
  private AbstractTypeCheckTestTraverser traverser;

  // Parser used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  AbstractTypeCheckTestParser p = new AbstractTypeCheckTestParser();

  // This is an auxiliary
  FullDeriveFromCombineExpressionsWithLiteralsAbstract derLit = new FullDeriveFromCombineExpressionsWithLiteralsAbstract();

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCalculator tc = new TypeCalculator(null, derLit);


  @BeforeClass
  public static void initLog(){
    Log.init();
    AbstractTypeCheckTestMill.reset();
    AbstractTypeCheckTestMill.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setup(){
    scope = AbstractTypeCheckTestMill.scope();
    scope.setEnclosingScope(null);
    scope.setExportingSymbols(true);
    scope.setAstNode(null);
    scope.setEnclosingScope(AbstractTypeCheckTestMill.globalScope());
    AbstractTypeCheckTestMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();

    TypeSymbol person = AbstractTypeCheckTestMill.typeSymbolBuilder()
        .setName("Person")
        .setSpannedScope(AbstractTypeCheckTestMill.scope())
        .setEnclosingScope(scope)
        .build();
    person.setSpannedScope(AbstractTypeCheckTestMill.scope());
    add2scope(scope, person);
    TypeSymbol student = AbstractTypeCheckTestMill.typeSymbolBuilder()
        .setName("Student")
        .setSpannedScope(AbstractTypeCheckTestMill.scope())
        .setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person",scope)))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, student);
    student.setSpannedScope(AbstractTypeCheckTestMill.scope());
    TypeSymbol firstsemesterstudent = AbstractTypeCheckTestMill.typeSymbolBuilder()
        .setName("FirstSemesterStudent")
        .setSpannedScope(AbstractTypeCheckTestMill.scope())
        .setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student",scope)))
        .setEnclosingScope(scope)
        .build();
    firstsemesterstudent.setSpannedScope(AbstractTypeCheckTestMill.scope());
    TypeSymbol address = AbstractTypeCheckTestMill.typeSymbolBuilder()
        .setName("Address")
        .setSpannedScope(AbstractTypeCheckTestMill.scope())
        .setSuperTypesList(Lists.newArrayList())
        .setEnclosingScope(person.getSpannedScope())
        .build();
    add2scope(person.getSpannedScope(), address);
    add2scope(person.getSpannedScope(), method("foo", SymTypeExpressionFactory.createTypeVoid()));
    add2scope(person.getSpannedScope(), field("bar", SymTypeExpressionFactory.createTypeConstant("int")));
    add2scope(scope, firstsemesterstudent);
    add2scope(scope, field("person1", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("firstsemester", SymTypeExpressionFactory.
        createTypeObject("FirstSemesterStudent", scope)));
    tc = new TypeCalculator(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);
    traverser = getTraverser(flatExpressionScopeSetter);
  }

  public void add2scope(IBasicSymbolsScope scope, TypeSymbol type){
    type.setEnclosingScope(scope);
    scope.add(type);
  }

  public void add2scope(IBasicSymbolsScope scope, VariableSymbol variable){
    variable.setEnclosingScope(scope);
    scope.add(variable);
  }

  public void add2scope(IBasicSymbolsScope scope, FunctionSymbol function){
    function.setEnclosingScope(scope);
    scope.add(function);
  }

  public AbstractTypeCheckTestTraverser getTraverser(FlatExpressionScopeSetter flatExpressionScopeSetter){
    AbstractTypeCheckTestTraverser traverser = AbstractTypeCheckTestMill.traverser();
    traverser.add4CommonExpressions(flatExpressionScopeSetter);
    traverser.add4ExpressionsBasis(flatExpressionScopeSetter);
    return traverser;
  }

  @Test
  public void testFieldAccessInnerVariables() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("person1.bar");
    assertTrue(expr.isPresent());
    expr.get().accept(traverser);

    assertEquals("int", tc.typeOf(expr.get()).print());
  }

  @Test
  public void testFieldAccessInnerTypes() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("person1.Address");
    assertTrue(expr.isPresent());
    expr.get().accept(traverser);

    assertEquals("Address", tc.typeOf(expr.get()).print());
  }

  @Test
  public void testCallInnerMethods() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("person1.foo()");
    assertTrue(expr.isPresent());
    expr.get().accept(traverser);

    assertEquals("void", tc.typeOf(expr.get()).print());
  }

  @Test
  public void testInheritanceVariables() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("firstsemester.bar");
    assertTrue(expr.isPresent());
    expr.get().accept(traverser);

    assertEquals("int", tc.typeOf(expr.get()).print());
  }

  @Test
  public void testInheritanceMethods() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("firstsemester.foo()");
    assertTrue(expr.isPresent());
    expr.get().accept(traverser);

    assertEquals("void", tc.typeOf(expr.get()).print());
  }


}
