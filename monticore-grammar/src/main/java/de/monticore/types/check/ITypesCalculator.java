/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;

import java.util.Optional;

public interface ITypesCalculator extends ExpressionsBasisVisitor {

  Optional<SymTypeExpression> calculateType(ASTExpression ex);

  Optional<SymTypeExpression> calculateType(ASTLiteral lit);

  void init();
}
