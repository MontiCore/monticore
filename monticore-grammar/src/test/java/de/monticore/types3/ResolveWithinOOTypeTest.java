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
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.jupiter.api.Assertions.*;

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
    CombineExpressionsWithLiteralsTypeTraverserFactory
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
    assertEquals("() -> int", type.printFullName());
    assertSame(method, ((SymTypeOfFunction) type).getSymbol());

    List<MethodSymbol> constructors = calculateConstructorWithinScope(
        oOType.getSpannedScope(), "t", BasicAccessModifier.PRIVATE
    );
    assertEquals(2, constructors.size());
    assertTrue(constructors.contains(constructor));
    assertTrue(constructors.contains(constructor2));
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
    assertEquals(2, constructors.size());
    assertTrue(constructors.contains(constructor));
    assertTrue(constructors.contains(constructor2));

    constructors = calculateConstructorWithinScope(
        oOType.getSpannedScope(), "t", BasicAccessModifier.PUBLIC
    );
    assertEquals(1, constructors.size());
    assertTrue(constructors.contains(constructor2));
  }

  // test if we get a list of all resolvable elements
  // class s {
  //   public class v {}
  //   public s s();
  //   public s s;
  // }
  // class t extends s {
  //  public class u {}
  //  public t();
  //  public t u();
  //  pulbic t u(t t);
  //  public t v;
  //  private t w;
  // }
  @Test
  public void resolveAllElementsTest() throws IOException {
    IOOSymbolsScope gs = OOSymbolsMill.globalScope();

    OOTypeSymbol sType = inScope(gs, oOtype("s"));
    SymTypeExpression sSymType =
        SymTypeExpressionFactory.createTypeObject(sType);

    inScope(sType.getSpannedScope(), oOtype("v"));
    inScope(sType.getSpannedScope(), method("s", sSymType));
    inScope(sType.getSpannedScope(), field("s", sSymType));

    OOTypeSymbol tType = inScope(gs, oOtype("t", List.of(sSymType)));
    SymTypeExpression tSymType =
        SymTypeExpressionFactory.createTypeObject(tType);

    inScope(tType.getSpannedScope(), oOtype("u"));
    MethodSymbol tConstructor = inScope(
        tType.getSpannedScope(),
        method("t", tSymType)
    );
    tConstructor.setIsConstructor(true);
    inScope(tType.getSpannedScope(), method("u", tSymType));
    inScope(tType.getSpannedScope(),
        method("u", tSymType, List.of(tSymType))
    );
    inScope(tType.getSpannedScope(), field("v", tSymType));
    FieldSymbol wField = inScope(
        tType.getSpannedScope(),
        field("w", tSymType)
    );
    wField.setIsPublic(false);
    wField.setIsPrivate(true);

    Map<String, SymTypeExpression> allTypes =
        OOWithinTypeBasicSymbolsResolver.getAllTypes(
            tSymType,
            BasicAccessModifier.PUBLIC,
            t -> true
        );
    assertEquals(Set.of("v", "u"), allTypes.keySet());

    Map<String, List<SymTypeOfFunction>> allFunctions =
        OOWithinTypeBasicSymbolsResolver.getAllFunctions(
            tSymType,
            BasicAccessModifier.PUBLIC,
            f -> true
        );
    // may not contain constructor
    assertEquals(Set.of("s", "u"), allFunctions.keySet());
    assertEquals(1, allFunctions.get("s").size());
    assertEquals(2, allFunctions.get("u").size());

    Map<String, SymTypeExpression> allFields =
        OOWithinTypeBasicSymbolsResolver.getAllVariables(
            tSymType,
            BasicAccessModifier.PUBLIC,
            v -> true
        );
    // may not contain the private w
    assertEquals(Set.of("s", "v"), allFields.keySet());
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
    assertTrue(functions.stream().allMatch(f -> f instanceof MethodSymbol));
    List<MethodSymbol> constructors = functions.stream()
        .map(f -> (MethodSymbol) f)
        .collect(Collectors.toList());
    assertTrue(constructors.stream().allMatch(MethodSymbolTOP::isIsConstructor));
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
