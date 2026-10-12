// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mcreturnstatements.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsInheritanceHandler;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.MapBasedTypeCheck3;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Provides Java code generations for MCReturnStatements
 */
public class MCReturnStatementsJavaGenVisitor
    extends MCReturnStatementsInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCReturnStatementsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTReturnStatement node) {
    getPrinter().print("return");
    if (node.isPresentExpression()) {
      getPrinter().print(" ");
      SymTypeExpression exprType = normalize(typeOf(node.getExpression()));
      SymTypeExpression targetType = _hack_getTargetType(node.getExpression());
      printConverted(
          getPrinter(),
          targetType,
          exprType,
          p -> node.getExpression().accept(getTraverser())
      );
    }
    state.endStatement();
  }

  // hack, need real solution in the future
  // will be removed
  SymTypeExpression _hack_getTargetType(ASTExpression expr) {
    return MapBasedTypeCheck3.internal_hacky_do_not_use_getCtx4Ast()
        .getContextOfExpression(expr)
        .getTargetType();
  }
}
