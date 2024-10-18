/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mcjavaliterals.types3;

import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;

public class MCJavaLiteralsTypeVisitor extends AbstractTypeVisitor
    implements MCJavaLiteralsVisitor2 {

  public void endVisit(de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral node) {
    getType4Ast().setTypeOfExpression(
        node,
        SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT)
    );
  }

  public void endVisit(de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral node) {
    getType4Ast().setTypeOfExpression(
        node,
        SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG)
    );
  }

  public void endVisit(de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral node) {
    getType4Ast().setTypeOfExpression(
        node,
        SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT)
    );
  }

  public void endVisit(de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral node) {
    getType4Ast().setTypeOfExpression(
        node,
        SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE)
    );
  }
}
