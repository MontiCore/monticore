// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.assignmentexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.javagen.JavaGenSymTypeRelations;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.JavaOperationPrinter;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.logging.Log;

import static de.monticore.codegen.CodeGenOperationPrinter.printDivide;
import static de.monticore.codegen.CodeGenOperationPrinter.printMinus;
import static de.monticore.codegen.CodeGenOperationPrinter.printModulo;
import static de.monticore.codegen.CodeGenOperationPrinter.printMultiply;
import static de.monticore.codegen.CodeGenOperationPrinter.printPlus;
import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.EQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.MINUSEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PERCENTEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PLUSEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.SLASHEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.STAREQUALS;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * prints, e.g., {@code x += 2}
 * as {@code x = (typeOf(x)) (x + 2)}.
 */
public class AssignmentExpressionsJavaGenVisitor
    extends AssignmentExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public AssignmentExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  public IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodoGen

  @Override
  public void traverse(ASTIncSuffixExpression expr) {
    // NOTE: this is only a temporary implementation,
    // as in the future, templates provided by the symbols
    // are to be used instead.

    if (JavaGenSymTypeRelations.generatesToJavaNumeric(TypeCheck3.typeOf(expr.getExpression()))) {
      expr.getExpression().accept(getTraverser());
      getPrinter().print("++");
    }
    else {
      Log.error("0xFD250 Unhandled increment suffix operator "
          + ". This is an alpha version and needs to be extended."
      );
    }
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    // NOTE: this is only a temporary implementation,
    // as in the future, templates provided by the symbols
    // are to be used instead.

    if (JavaGenSymTypeRelations.generatesToJavaNumeric(TypeCheck3.typeOf(expr.getExpression()))) {
      expr.getExpression().accept(getTraverser());
      getPrinter().print("--");
    }
    else {
      Log.error("0xFD251 Unhandled increment suffix operator "
          + ". This is an alpha version and needs to be extended."
      );
    }
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr) {
    SymTypeExpression resultType = normalize(typeOf(expr));
    SymTypeExpression innerType = normalize(typeOf(expr.getExpression()));

    JavaOperationPrinter.printAssignment(
        getPrinter(),
        resultType,
        innerType,
        // left type due to conversion
        innerType,
        p -> expr.getExpression().accept(getTraverser()),
        p2 -> printPlus(getPrinter(),
            resultType,
            innerType,
            SymTypeExpressionFactory.createPrimitive("int"),
            p -> expr.getExpression().accept(getTraverser()),
            (p) -> p.print("1")
        )
    );
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr) {
    SymTypeExpression resultType = normalize(typeOf(expr));
    SymTypeExpression innerType = normalize(typeOf(expr.getExpression()));

    JavaOperationPrinter.printAssignment(
        getPrinter(),
        resultType,
        innerType,
        // left type due to conversion
        innerType,
        p -> expr.getExpression().accept(getTraverser()),
        p2 -> printMinus(
            getPrinter(),
            resultType,
            innerType,
            SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT),
            p -> expr.getExpression().accept(getTraverser()),
            (p) -> p.print("1")
        )
    );
  }

  @Override
  public void traverse(ASTAssignmentExpression assignment) {

    // should be the same as target
    SymTypeExpression resultType = normalize(typeOf(assignment));
    SymTypeExpression leftType = normalize(typeOf(assignment.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(assignment.getRight()));
    CodeGenPrintAction leftExprPrintAction = p ->
        assignment.getLeft().accept(getTraverser());
    CodeGenPrintAction rightExprPrintAction = p ->
        assignment.getRight().accept(getTraverser());

    // given expression a *= b, is typeof(a * b)
    SymTypeExpression typeOfInnerOperation;
    CodeGenPrintAction printInnerOperationAction;
    switch (assignment.getOperator()) {
      case EQUALS:
        // no real inner operation -> basically id
        typeOfInnerOperation = rightType;
        printInnerOperationAction = rightExprPrintAction;
        break;
      case PLUSEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.plus(leftType, rightType).get();
        printInnerOperationAction = p -> printPlus(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case MINUSEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.minus(leftType, rightType).get();
        printInnerOperationAction = p -> printMinus(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case STAREQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.multiply(leftType, rightType).get();
        printInnerOperationAction = p -> printMultiply(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case SLASHEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.divide(leftType, rightType).get();
        printInnerOperationAction = p -> printDivide(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case PERCENTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.modulo(leftType, rightType).get();
        printInnerOperationAction = p -> printModulo(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      // To be extended
        /*
      case LTLTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.leftShift(leftType, rightType).get();
        printInnerOperationAction = p -> (p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case GTGTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.shiftRight(leftType, rightType).get();
        printInnerOperationAction = p -> printShiftRight(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case GTGTGTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.unsignedShiftRight(leftType, rightType).get();
        printInnerOperationAction = p -> printUnsignedShiftRight(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case AND_EQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.and(leftType, rightType).get();
        printInnerOperationAction = p -> printAnd(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
      case PIPEEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.or(leftType, rightType).get();
        printInnerOperationAction = p -> printOr(p, typeOfInnerOperation, leftType, rightType, leftExprPrintAction, rightExprPrintAction);
        break;
         */
      default:
        Log.error("0xFD249 Unhandled assignment operator: "
            + assignment.getOperator()
            + ". This is an alpha version and needs to be extended."
        );
        return;
    }

    JavaOperationPrinter.printAssignment(
        getPrinter(),
        resultType,
        leftType,
        // left type due to conversion
        leftType,
        p -> assignment.getLeft().accept(getTraverser()),
        p2 -> printConverted(
            p2,
            leftType,
            typeOfInnerOperation,
            printInnerOperationAction
        )
    );
  }

}
