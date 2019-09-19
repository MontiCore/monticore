package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

import java.util.Optional;

public interface ITypesCalculator {

  Optional<SymTypeExpression> calculateType(ASTExpression ex);

  Optional<SymTypeExpression> calculateType(ASTLiteral lit);

  void init();
}
