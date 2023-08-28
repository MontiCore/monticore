/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.types3.util.TypeContextCalculator;

import java.util.List;
import java.util.Optional;

/**
 * s. {@link de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeIdAsConstructorTypeVisitor}
 */
public class ExpressionBasisTypeIdAsConstructorTypeVisitor
    extends ExpressionBasisTypeVisitor {

  OOWithinTypeBasicSymbolsResolver oOWithinTypeResolver;

  TypeContextCalculator typeContextCalculator;

  public ExpressionBasisTypeIdAsConstructorTypeVisitor() {
    // default values
    oOWithinTypeResolver = new OOWithinTypeBasicSymbolsResolver();
    withinScopeResolver = new OOWithinScopeBasicSymbolsResolver();
    typeContextCalculator = new TypeContextCalculator();
  }

  public void setOOWithinTypeResolver(
      OOWithinTypeBasicSymbolsResolver oOWithinTypeResolver) {
    this.oOWithinTypeResolver = oOWithinTypeResolver;
  }

  protected OOWithinTypeBasicSymbolsResolver getOOWithinTypeResolver() {
    return oOWithinTypeResolver;
  }

  protected TypeContextCalculator getTypeContextCalculator() {
    return typeContextCalculator;
  }

  /**
   * adds support for the type id to be used as if it were the constructors of the type,
   * e.g., Foo(1) based on Constructor Foo::Foo(int)
   */
  @Override
  protected Optional<SymTypeExpression> calculateNameExpression(
      ASTNameExpression expr) {
    Optional<SymTypeExpression> exprType = super.calculateNameExpression(expr);
    // add constructors only if no expression has been found
    if (exprType.isEmpty()) {
      IBasicSymbolsScope enclosingScope =
          getAsBasicSymbolsScope(expr.getEnclosingScope());
      Optional<SymTypeExpression> typeIdType = getWithinScopeResolver()
          .resolveType(enclosingScope, expr.getName());
      if (typeIdType.isPresent()) {
        AccessModifier accessModifier = getTypeContextCalculator()
            .getAccessModifier(typeIdType.get().getTypeInfo(), enclosingScope);
        List<SymTypeOfFunction> constructors =
            getOOWithinTypeResolver().resolveConstructors(
                typeIdType.get(),
                accessModifier,
                c -> true
            );
        if (constructors.size() == 1) {
          exprType = Optional.of(constructors.get(0));
        }
        else if (constructors.size() > 1) {
          exprType = Optional.of(SymTypeExpressionFactory.createIntersection(constructors));
        }
      }
    }
    return exprType;
  }

}