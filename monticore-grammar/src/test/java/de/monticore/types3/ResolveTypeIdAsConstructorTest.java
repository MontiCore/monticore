/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.mcbasics._symboltable.IMCBasicsScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import static de.monticore.types3.util.DefsTypesForTests._floatSymType;
import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.method;
import static de.monticore.types3.util.DefsTypesForTests.oOtype;

/**
 * tests whether we can resolve correctly constructors
 * based solely on their Type identifiers,
 * e.g., Foo(1) -> based on Constructor Foo::Foo(int)
 * It mostly tests {@link de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeIdAsConstructorTypeVisitor}
 * and {@link de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeIdAsConstructorTypeVisitor}
 */
public class ResolveTypeIdAsConstructorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void before() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    // replace the typeMapTraverser with an OO-aware variant
    new CombineExpressionsWithLiteralsTypeTraverserFactory()
        .initTypeCheck3ForOOWithConstructors();
  }

  // class t {
  //   public static int t() {}
  //   public t() {}
  //   public t(t) {}
  //   => test to resolve function here
  // }
  // => test to resolve constructor here
  @Test
  public void test1() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    SymTypeExpression oOTypeSymType =
        SymTypeExpressionFactory.createTypeObject(oOType);
    inScope(gs, oOType);

    MethodSymbol method = method("t", _intSymType);
    method.setIsStatic(true);
    inScope(oOType.getSpannedScope(), method);

    MethodSymbol constructor = method("t", oOTypeSymType);
    constructor.setIsConstructor(true);
    inScope(oOType.getSpannedScope(), constructor);

    MethodSymbol constructor2 = method("t", oOTypeSymType, oOTypeSymType);
    constructor2.setIsConstructor(true);
    inScope(oOType.getSpannedScope(), constructor2);

    // do not find the constructor if we find a function
    SymTypeExpression type =
        calculateTypeWithinScope("t", oOType.getSpannedScope());
    Assertions.assertEquals("() -> int", type.printFullName());
    Assertions.assertSame(method, ((SymTypeOfFunction) type).getSymbol());

    // do not find the constructor if we find a function
    type = calculateTypeWithinScope("t.t", gs);
    Assertions.assertEquals("() -> int", type.printFullName());
    Assertions.assertSame(method, ((SymTypeOfFunction) type).getSymbol());

    // find the constructor based on the type id
    type = calculateTypeWithinScope("t", gs);
    Assertions.assertTrue(type.isIntersectionType());
    Collection<SymTypeExpression> functions =
        ((SymTypeOfIntersection) type).getIntersectedTypeSet();
    Assertions.assertTrue(functions.stream().allMatch(f -> f.isFunctionType()));
    Collection<SymTypeOfFunction> constructors = functions.stream()
        .map(f -> (SymTypeOfFunction) f)
        .collect(Collectors.toSet());
    Assertions.assertEquals(2, constructors.size());
    Assertions.assertTrue(constructors.stream().anyMatch(c -> c.getSymbol() == constructor));
    Assertions.assertTrue(constructors.stream().anyMatch(c -> c.getSymbol() == constructor2));
    Assertions.assertTrue(constructors.stream()
        .map(SymTypeOfFunction::printFullName)
        .anyMatch(p -> p.equals("() -> t")));
    Assertions.assertTrue(constructors.stream()
        .map(SymTypeOfFunction::printFullName)
        .anyMatch(p -> p.equals("t -> t")));

    assertNoFindings();
  }

  // class t {
  //   class t {
  //     private t(float) {}
  //     public t(int) {}
  //     => test to resolve constructor here
  //   }
  //   public t() {}
  //   => test to resolve constructor here
  // }
  // => test to resolve constructor here
  @Test
  public void test2() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    SymTypeExpression oOTypeSymType =
        SymTypeExpressionFactory.createTypeObject(oOType);
    inScope(gs, oOType);

    MethodSymbol constructor = method("t", oOTypeSymType);
    constructor.setIsConstructor(true);
    inScope(oOType.getSpannedScope(), constructor);

    OOTypeSymbol innerOOType = oOtype("t");
    SymTypeExpression innerOOTypeSymType =
        SymTypeExpressionFactory.createTypeObject(innerOOType);
    inScope(oOType.getSpannedScope(), innerOOType);

    MethodSymbol innerConstructor1 =
        method("t", innerOOTypeSymType, _floatSymType);
    innerConstructor1.setIsConstructor(true);
    innerConstructor1.setIsPublic(false);
    innerConstructor1.setIsPrivate(true);
    inScope(innerOOType.getSpannedScope(), innerConstructor1);

    MethodSymbol innerConstructor2 =
        method("t", innerOOTypeSymType, _intSymType);
    innerConstructor2.setIsConstructor(true);
    inScope(innerOOType.getSpannedScope(), innerConstructor2);

    // find the constructor based on the type id
    SymTypeExpression type = calculateTypeWithinScope("t", gs);
    Assertions.assertTrue(type.isFunctionType());
    Assertions.assertSame(constructor, ((SymTypeOfFunction) type).getSymbol());

    // find the constructor of the inner type,
    // as the constructor of the outer type is filtered out
    type = calculateTypeWithinScope("t.t", gs);
    Assertions.assertTrue(type.isFunctionType());
    Assertions.assertSame(innerConstructor2, ((SymTypeOfFunction) type).getSymbol());

    // find the constructor of the inner type,
    // as the constructor of the outer type is filtered out
    type = calculateTypeWithinScope("t", oOType.getSpannedScope());
    Assertions.assertTrue(type.isFunctionType());
    Assertions.assertSame(innerConstructor2, ((SymTypeOfFunction) type).getSymbol());

    type = calculateTypeWithinScope("t", innerOOType.getSpannedScope());
    Assertions.assertEquals("(float -> t.t) & (int -> t.t)", type.printFullName());

    assertNoFindings();
  }

  // Helper

  /**
   * calculates the type of the (simple) expression within the scope
   * s.a. getExpressionScopeSetter
   */
  protected SymTypeExpression calculateTypeWithinScope(
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

}
