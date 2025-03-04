/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.types3.util.TypeContextCalculator;

import java.util.List;
import java.util.Optional;

/**
 * This visitor allows the use of type identifiers "as" the types constructors,
 * e.g., pack.age.Foo(1) is accepted if the constructor
 * pack.age.Foo::Foo(int) exists.
 */
public class CommonExpressionsTypeIdAsConstructorTypeVisitor extends
    CommonExpressionsTypeVisitor {

  @Override
  protected Optional<SymTypeExpression> calculateTypeIdFieldAccess(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional) {
    Optional<SymTypeExpression> exprType =
        super.calculateTypeIdFieldAccess(expr, resultsAreOptional);
    if (exprType.isEmpty() && !resultsAreOptional) {
      Optional<SymTypeExpression> typeIdType =
          calculateInnerTypeIdFieldAccess(expr);
      if (typeIdType.isPresent()) {
        exprType =
            constructorsOfTypeId(typeIdType.get(), expr.getEnclosingScope());
      }
    }
    return exprType;
  }

  @Override
  protected Optional<SymTypeExpression> calculateExprQName(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional) {
    Optional<SymTypeExpression> exprType = super.calculateExprQName(expr, resultsAreOptional);
    if (exprType.isEmpty() && !resultsAreOptional) {
      Optional<SymTypeExpression> typeIdType =
          calculateTypeIdQName(expr);
      if (typeIdType.isPresent()) {
        exprType =
            constructorsOfTypeId(typeIdType.get(), expr.getEnclosingScope());
      }
    }
    return exprType;
  }

  // Helper

  protected Optional<SymTypeExpression> constructorsOfTypeId(
      SymTypeExpression typeIdType,
      IScope enclosingScope) {
    AccessModifier accessModifier = TypeContextCalculator.getAccessModifier(
        typeIdType.getTypeInfo(),
        enclosingScope,
        true
    );
    List<SymTypeOfFunction> constructors =
        OOWithinTypeBasicSymbolsResolver.resolveConstructors(
            typeIdType,
            accessModifier,
            c -> true
        );
    if (constructors.isEmpty()) {
      return Optional.empty();
    }
    else if (constructors.size() == 1) {
      return Optional.of(constructors.get(0));
    }
    else {
      return Optional.of(
          SymTypeExpressionFactory.createIntersection(constructors)
      );
    }
  }

}
