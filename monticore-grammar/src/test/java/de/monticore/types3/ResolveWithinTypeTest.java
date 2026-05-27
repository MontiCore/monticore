/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.mcbasics._symboltable.IMCBasicsScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types3.util.DefsTypesForTests._booleanSymType;
import static de.monticore.types3.util.DefsTypesForTests.field;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.method;
import static de.monticore.types3.util.DefsTypesForTests.oOtype;
import static de.monticore.types3.util.DefsTypesForTests.typeVariable;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static org.junit.jupiter.api.Assertions.*;

/**
 * tests whether we can resolve correctly within a type.
 * E.g., the inner type of a super class.
 * It mostly tests {@link de.monticore.types3.util.WithinTypeBasicSymbolsResolver}
 */
public class ResolveWithinTypeTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void before() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  // class t {
  //   t t;
  //   t t() {
  //     => test expression "t" in this method scope
  //   }
  //   => test expressions "t()" and "t" in this class scope
  // }
  @Test
  public void test1() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    inScope(gs, oOType);

    FieldSymbol field = field("t",
        SymTypeExpressionFactory.createTypeObject(oOType));
    inScope(oOType.getSpannedScope(), field);

    MethodSymbol method = method("t",
        SymTypeExpressionFactory.createTypeObject(oOType));
    inScope(oOType.getSpannedScope(), method);

    SymTypeExpression type =
        calculateTypeWithinScope("t", oOType.getSpannedScope());
    assertEquals("(() -> t) & t", type.printFullName());
    assertTrue(type.isIntersectionType());
    assertTrue(((SymTypeOfIntersection) type).getIntersectedTypeSet()
        .stream()
        .anyMatch(t -> t.hasTypeInfo() && t.getTypeInfo() == oOType));

    type = calculateTypeWithinScope("t()", oOType.getSpannedScope());
    assertNoFindings();
    assertEquals("t", type.printFullName());
    assertSame(type.getTypeInfo(), oOType);

    type = calculateTypeWithinScope("t", method.getSpannedScope());
    assertEquals("(() -> t) & t", type.printFullName());
    assertTrue(type.isIntersectionType());
    assertTrue(((SymTypeOfIntersection) type).getIntersectedTypeSet()
        .stream()
        .anyMatch(t -> t.hasTypeInfo() && t.getTypeInfo() == oOType));
  }

  // class t<t> {
  //   t t;
  //   <t> t t() {
  //     t t;
  //     => test expression "t" in this method scope
  //   }
  //   => cannot test expressions "t" in this class scope,
  //      as target type is missing
  // }
  @Test
  public void test2() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    inScope(gs, oOType);
    TypeVarSymbol oOTypeVar =
        inScope(oOType.getSpannedScope(), typeVariable("t"));

    FieldSymbol field = field("t",
        SymTypeExpressionFactory.createTypeObject(oOType));
    inScope(oOType.getSpannedScope(), field);

    TypeVarSymbol methodTypeVar = typeVariable("t");
    MethodSymbol method = method("t",
        SymTypeExpressionFactory.createTypeVariable(methodTypeVar));
    inScope(method.getSpannedScope(), methodTypeVar);
    inScope(oOType.getSpannedScope(), method);

    IBasicSymbolsScope methodScope = inScope(method.getSpannedScope(), BasicSymbolsMill.scope());
    VariableSymbol variable = variable("t",
        SymTypeExpressionFactory.createTypeVariable(oOTypeVar));
    inScope(methodScope, variable);

    // cannot resolve t, as no target type exists.
  }

  // class s<t> {
  //   class t {}
  //   t t;
  //   <t> t t() {
  //     t t;
  //     => test expression "t" in this method scope
  //   }
  // }
  @Test
  public void test3() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("s");
    inScope(gs, oOType);
    TypeVarSymbol oOTypeVar =
        inScope(oOType.getSpannedScope(), typeVariable("t"));

    OOTypeSymbol oOType1 = oOtype("t");
    inScope(oOType.getSpannedScope(), oOType1);

    FieldSymbol field = field("t",
        SymTypeExpressionFactory.createTypeObject(oOType1));
    inScope(oOType.getSpannedScope(), field);

    TypeVarSymbol methodTypeVar = typeVariable("t");
    MethodSymbol method = method("t",
        SymTypeExpressionFactory.createTypeVariable(methodTypeVar)
    );
    inScope(oOType.getSpannedScope(), method);
    inScope(method.getSpannedScope(), methodTypeVar);

    IBasicSymbolsScope methodScope = inScope(method.getSpannedScope(), BasicSymbolsMill.scope());
    VariableSymbol variable = variable("t",
        SymTypeExpressionFactory.createTypeVariable(oOTypeVar));
    inScope(methodScope, variable);

    // cannot resolve t in method scope as target type for method is not present
  }

  // class s {
  //   class t {}
  //   t t;
  //   t t() {
  //     => test expression "t" in this method scope
  //   }
  //   => test expressions "t()" and "t" in this class scope
  // }
  @Test
  public void test4() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("s");
    inScope(gs, oOType);

    OOTypeSymbol oOType1 = oOtype("t");
    inScope(oOType.getSpannedScope(), oOType1);

    FieldSymbol field = field("t",
        SymTypeExpressionFactory.createTypeObject(oOType1));
    inScope(oOType.getSpannedScope(), field);

    MethodSymbol method = method("t",
        SymTypeExpressionFactory.createTypeObject(oOType1));
    inScope(oOType.getSpannedScope(), method);

    SymTypeExpression type =
        calculateTypeWithinScope("t", oOType.getSpannedScope());
    assertEquals("(() -> s.t) & s.t", type.printFullName());
    assertTrue(type.isIntersectionType());
    assertTrue(((SymTypeOfIntersection) type).getIntersectedTypeSet()
        .stream()
        .anyMatch(t -> t.hasTypeInfo() && t.getTypeInfo() == oOType1));

    type = calculateTypeWithinScope("t()", oOType.getSpannedScope());
    assertEquals("s.t", type.printFullName());
    assertSame(type.getTypeInfo(), oOType1);

    type = calculateTypeWithinScope("t", method.getSpannedScope());
    assertEquals("(() -> s.t) & s.t", type.printFullName());
    assertTrue(type.isIntersectionType());
    assertTrue(((SymTypeOfIntersection) type).getIntersectedTypeSet()
        .stream()
        .anyMatch(t -> t.hasTypeInfo() && t.getTypeInfo() == oOType1));
  }

  // class t<t> {}
  // class s extends t<boolean> {
  //   t<s> t;
  //   => test expression "t" in this class scope
  // }
  @Test
  public void test5() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    inScope(gs, oOType);
    inScope(oOType.getSpannedScope(), typeVariable("t"));

    OOTypeSymbol oOType1 = oOtype("s", List.of(
        SymTypeExpressionFactory.createGenerics(oOType, _booleanSymType)
    ));
    inScope(gs, oOType1);

    FieldSymbol field = field("t",
        SymTypeExpressionFactory.createGenerics(oOType,
            SymTypeExpressionFactory.createTypeObject(oOType1)
        )
    );
    inScope(oOType1.getSpannedScope(), field);

    SymTypeExpression type =
        calculateTypeWithinScope("t", oOType1.getSpannedScope());
    assertEquals("t<s>", type.printFullName());
    assertSame(oOType, type.getTypeInfo());
  }

  // class t {}
  // class s<t> {}
  // class u extends s<t> {
  //   t t;
  //   => test expression "t" in this class scope
  // }
  @Test
  public void test6() throws IOException {
    ICombineExpressionsWithLiteralsGlobalScope gs =
        CombineExpressionsWithLiteralsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    inScope(gs, oOType);

    OOTypeSymbol oOType1 = oOtype("s");
    inScope(gs, oOType1);
    inScope(oOType1.getSpannedScope(), typeVariable("t"));

    OOTypeSymbol oOType2 = oOtype("u", List.of(
        SymTypeExpressionFactory.createGenerics(oOType1,
            SymTypeExpressionFactory.createTypeObject(oOType)
        )
    ));
    inScope(gs, oOType2);

    FieldSymbol fieldSymbol = field("t",
        SymTypeExpressionFactory.createTypeObject(oOType));
    inScope(oOType2.getSpannedScope(), fieldSymbol);

    SymTypeExpression type =
        calculateTypeWithinScope("t", oOType2.getSpannedScope());
    assertEquals("t", type.printFullName());
    assertSame(type.getTypeInfo(), oOType);
  }

  // class t<t> {
  //   => test type "t.t" in this scope, ought to fail
  // }
  // => test type "t.t" in this scope, ought to fail
  @Test
  public void testError1() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType1 = oOtype("t");
    inScope(gs, oOType1);

    inScope(oOType1.getSpannedScope(), typeVariable("t"));

    calculateTypeIDWithinScopeError("t.t", oOType1.getSpannedScope(), "0xFDAE3");
    // ignore legacy error codes explicitly
    Log.clearFindings();
    calculateTypeIDWithinScopeError("t.t", oOType1.getEnclosingScope(), "0xFDAE3");
    Log.clearFindings();
  }

  // Helper

  /**
   * calculates the type of the (simple) expression within the scope
   * s.a. getExpressionScopeSetter
   */
  SymTypeExpression calculateTypeWithinScope(
      String exprStr,
      IMCBasicsScope scope
  ) throws IOException {
    ASTExpression expr = parseExpr(exprStr);
    generateScopes(expr);
    expr.accept(getExpressionScopeSetter(scope));
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    assertNoFindings();
    return type;
  }

  /**
   * Sets every (sub-)expression to the given scope.
   * This can be used to test the expression in specific contexts.
   * This only works for expressions, in which no own scope is spanned,
   * e.g., lambdas shall not be used with this
   */
  protected ExpressionsBasisTraverser getExpressionScopeSetter(
      IMCBasicsScope scope) {
    ExpressionsBasisTraverser scopeSetter =
        ExpressionsBasisMill.inheritanceTraverser();
    scopeSetter.add4ExpressionsBasis(
        new ExpressionsBasisVisitor2() {
          @Override
          public void visit(ASTExpression node) {
            node.setEnclosingScope(scope);
          }
        }
    );
    return scopeSetter;
  }

  /**
   * tries to calculate the type of the type identifier within the scope,
   * but should fail and checks for the error Code.
   * If getting the type ID does not fail,
   * a variable / function is used to check the type, in this case,
   * use {@link #calculateTypeWithinScope(String, IMCBasicsScope)} instead.
   */
  SymTypeExpression calculateTypeIDWithinScopeError(
      String typeStr,
      IMCBasicsScope scope,
      String errorCode
  ) throws IOException {
    ASTMCType mcType = parseMCType(typeStr);
    generateScopes(mcType);
    mcType.accept(getMCTypeScopeSetter(scope));
    SymTypeExpression type = TypeCheck3.symTypeFromAST(mcType);
    assertHasErrorCode(errorCode);
    return type;
  }

  /**
   * Sets every (sub-)MCType to the given scope.
   * This can be used to test the MCType in specific contexts.
   */
  protected MCBasicTypesTraverser getMCTypeScopeSetter(
      IMCBasicsScope scope) {
    MCBasicTypesTraverser scopeSetter =
        MCBasicTypesMill.inheritanceTraverser();
    scopeSetter.add4MCBasicTypes(
        new MCBasicTypesVisitor2() {
          @Override
          public void visit(ASTMCType node) {
            node.setEnclosingScope(scope);
          }
        }
    );
    return scopeSetter;
  }
}
