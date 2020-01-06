// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

public interface IDerivePrettyPrinter {

  String prettyprint(ASTExpression node);

  String prettyprint(ASTLiteral node);

}
