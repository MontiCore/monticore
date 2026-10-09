// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.assignmentexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.CodeGenPrintAction;
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
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.generatesToJavaNumeric;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.getOperandType;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.EQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.MINUSEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PERCENTEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.PLUSEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.SLASHEQUALS;
import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.STAREQUALS;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;
import static de.monticore.types3.util.SIUnitTypeRelations.hasSIUnit;

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

    if (generatesToJavaNumeric(TypeCheck3.typeOf(expr.getExpression()))) {
      expr.getExpression().accept(getTraverser());
      getPrinter().print("++");
    }
    else {
      Log.error("0xFD350 Unhandled increment suffix operator "
              + ". This is an alpha version and needs to be extended.",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    // NOTE: this is only a temporary implementation,
    // as in the future, templates provided by the symbols
    // are to be used instead.

    if (generatesToJavaNumeric(TypeCheck3.typeOf(expr.getExpression()))) {
      expr.getExpression().accept(getTraverser());
      getPrinter().print("--");
    }
    else {
      Log.error("0xFD351 Unhandled increment suffix operator "
              + ". This is an alpha version and needs to be extended.",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
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
    // the type of the assignment expression is normalized
    SymTypeExpression leftType = typeOf(assignment.getLeft());
    if (hasSIUnit(leftType) && !state.isWithoutValueConversion(assignment)) {
      printConverted(getPrinter(), typeOf(assignment), leftType,
          p -> printAssignmentExpression(assignment)
      );
    }
    else {
      printAssignmentExpression(assignment);
    }
  }

  protected void printAssignmentExpression(ASTAssignmentExpression assignment) {
    SymTypeExpression leftType = typeOf(assignment.getLeft());
    SymTypeExpression rightType = typeOf(assignment.getRight());
    SymTypeExpression leftOperandType = getOperandType(leftType);
    SymTypeExpression rightOperandType = getOperandType(rightType);
    CodeGenPrintAction leftExprPrintAction = p ->
        state.printOperand(assignment.getLeft(), getTraverser());
    CodeGenPrintAction rightExprPrintAction = p ->
        state.printOperand(assignment.getRight(), getTraverser());

    // given expression a *= b, is typeof(a * b)
    SymTypeExpression typeOfInnerOperation;
    CodeGenPrintAction printInnerOperationAction;
    switch (assignment.getOperator()) {
      case EQUALS:
        // no real inner operation -> basically id
        typeOfInnerOperation = rightType;
        printInnerOperationAction = p ->
            assignment.getRight().accept(getTraverser());
        break;
      case PLUSEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.plus(leftOperandType, rightOperandType).get();
        printInnerOperationAction = p -> printPlus(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case MINUSEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.minus(leftOperandType, rightOperandType).get();
        printInnerOperationAction = p -> printMinus(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case STAREQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.multiply(leftOperandType, rightOperandType).get();
        printInnerOperationAction = p -> printMultiply(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case SLASHEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.divide(leftOperandType, rightOperandType).get();
        printInnerOperationAction = p -> printDivide(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case PERCENTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.modulo(leftOperandType, rightOperandType).get();
        printInnerOperationAction = p -> printModulo(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      // To be extended
        /*
      case LTLTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.leftShift(leftType, rightType).get();
        printInnerOperationAction = p -> (p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case GTGTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.shiftRight(leftType, rightType).get();
        printInnerOperationAction = p -> printShiftRight(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case GTGTGTEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.unsignedShiftRight(leftType, rightType).get();
        printInnerOperationAction = p -> printUnsignedShiftRight(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case AND_EQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.and(leftType, rightType).get();
        printInnerOperationAction = p -> printAnd(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
        break;
      case PIPEEQUALS:
        typeOfInnerOperation = TypeVisitorOperatorCalculator.or(leftType, rightType).get();
        printInnerOperationAction = p -> printOr(p, typeOfInnerOperation, leftOperandType, rightOperandType, leftExprPrintAction, rightExprPrintAction);
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
        leftType,
        leftType,
        // left type due to conversion
        leftType,
        p -> state.printWithoutValueConversion(
            assignment.getLeft(), getTraverser()
        ),
        p2 -> printConverted(
            p2,
            leftType,
            typeOfInnerOperation,
            printInnerOperationAction
        )
    );
  }

}
