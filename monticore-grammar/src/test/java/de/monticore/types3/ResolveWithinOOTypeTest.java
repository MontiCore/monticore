/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.mcbasics._symboltable.IMCBasicsScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolTOP;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.method;
import static de.monticore.types3.util.DefsTypesForTests.oOtype;

/**
 * tests whether we can resolve correctly constructors within a type.
 * It mostly tests {@link OOWithinTypeBasicSymbolsResolver}
 */
public class ResolveWithinOOTypeTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void before() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    // replace the typeMapTraverser with an OO-aware variant
    new CombineExpressionsWithLiteralsTypeTraverserFactory()
        .initTypeCheck3ForOO();
  }

  // class t {
  //   public int t() {}
  //   public t() {}
  //   public t(t) {}
  //   => test to resolve method here
  //   => test to resolve constructor
  // }
  @Test
  public void test1() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    SymTypeExpression oOTypeSymType =
        SymTypeExpressionFactory.createTypeObject(oOType);
    inScope(gs, oOType);

    MethodSymbol method = method("t", _intSymType);
    inScope(oOType.getSpannedScope(), method);

    MethodSymbol constructor = method("t", oOTypeSymType);
    constructor.setIsConstructor(true);
    inScope(oOType.getSpannedScope(), constructor);

    MethodSymbol constructor2 = method("t", oOTypeSymType, oOTypeSymType);
    constructor2.setIsConstructor(true);
    inScope(oOType.getSpannedScope(), constructor2);

    SymTypeExpression type =
        calculateTypeWithinScope("t", oOType.getSpannedScope());
    Assertions.assertEquals("() -> int", type.printFullName());
    Assertions.assertSame(method, ((SymTypeOfFunction) type).getSymbol());

    List<MethodSymbol> constructors = calculateConstructorWithinScope(
        oOType.getSpannedScope(), "t", BasicAccessModifier.PRIVATE
    );
    Assertions.assertEquals(2, constructors.size());
    Assertions.assertTrue(constructors.contains(constructor));
    Assertions.assertTrue(constructors.contains(constructor2));
  }

  // class t {
  //   private t() {}
  //   public t(t) {}
  //   public s() {} // some constructor with a different name
  //                 // that should not be resolved
  //   => test to resolve constructor using different AccessModifiers
  // }
  @Test
  public void test2() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    OOTypeSymbol oOType = oOtype("t");
    SymTypeExpression oOTypeSymType =
        SymTypeExpressionFactory.createTypeObject(oOType);
    inScope(gs, oOType);

    MethodSymbol constructor = method("t", oOTypeSymType);
    constructor.setIsConstructor(true);
    constructor.setIsPublic(false);
    constructor.setIsPrivate(true);
    inScope(oOType.getSpannedScope(), constructor);

    MethodSymbol constructor2 = method("t", oOTypeSymType, oOTypeSymType);
    constructor2.setIsConstructor(true);
    inScope(oOType.getSpannedScope(), constructor2);

    MethodSymbol constructor3 = method("s", oOTypeSymType);
    constructor3.setIsConstructor(true);
    inScope(oOType.getSpannedScope(), constructor3);

    List<MethodSymbol> constructors = calculateConstructorWithinScope(
        oOType.getSpannedScope(), "t", BasicAccessModifier.PRIVATE
    );
    Assertions.assertEquals(2, constructors.size());
    Assertions.assertTrue(constructors.contains(constructor));
    Assertions.assertTrue(constructors.contains(constructor2));

    constructors = calculateConstructorWithinScope(
        oOType.getSpannedScope(), "t", BasicAccessModifier.PUBLIC
    );
    Assertions.assertEquals(1, constructors.size());
    Assertions.assertTrue(constructors.contains(constructor2));
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
   * resolves the construtors given the accessmodifier
   */
  protected List<MethodSymbol> calculateConstructorWithinScope(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier
  ) {
    List<FunctionSymbol> functions =
        new OOWithinTypeBasicSymbolsResolver().resolveConstructorLocally(
            scope, name, accessModifier, c -> true
        );
    assertNoFindings();
    Assertions.assertTrue(functions.stream().allMatch(f -> f instanceof MethodSymbol));
    List<MethodSymbol> constructors = functions.stream()
        .map(f -> (MethodSymbol) f)
        .collect(Collectors.toList());
    Assertions.assertTrue(constructors.stream().allMatch(MethodSymbolTOP::isIsConstructor));
    return constructors;
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
