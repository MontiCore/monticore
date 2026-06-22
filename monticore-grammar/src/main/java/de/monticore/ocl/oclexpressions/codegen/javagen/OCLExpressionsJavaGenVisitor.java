// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.oclexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.util.Node2Name;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.oclexpressions._ast.ASTAnyExpression;
import de.monticore.ocl.oclexpressions._ast.ASTEquivalentExpression;
import de.monticore.ocl.oclexpressions._ast.ASTExistsExpression;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.monticore.ocl.oclexpressions._ast.ASTIfThenElseExpression;
import de.monticore.ocl.oclexpressions._ast.ASTImpliesExpression;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTIterateExpression;
import de.monticore.ocl.oclexpressions._ast.ASTLetinExpression;
import de.monticore.ocl.oclexpressions._ast.ASTOCLAtPreQualification;
import de.monticore.ocl.oclexpressions._ast.ASTOCLTransitiveQualification;
import de.monticore.ocl.oclexpressions._ast.ASTOCLVariableDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTTypeIfExpression;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaType;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Provides Java code generations for OCLExpressions
 */
public class OCLExpressionsJavaGenVisitor
    extends OCLExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public OCLExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTTypeIfExpression node) {
    SymTypeExpression exprType = normalize(TypeCheck3.typeOf(node));
    SymTypeExpression castedType = normalize(TypeCheck3.symTypeFromAST(node.getMCType()));

    state.printExpressionBeginLambda(exprType);

    getPrinter().print(convert2JavaType(exprType));
    getPrinter().print(" ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    getPrinter().print("if (");
    getPrinter().print(node.getName());
    getPrinter().print(" instanceof ");
    getPrinter().print(convert2JavaType(castedType));
    getPrinter().println(") {");
    getPrinter().indent();

    getPrinter().print(convert2JavaType(castedType));
    getPrinter().print(" ");
    getPrinter().print(node.getName());
    getPrinter().print(" = (");
    getPrinter().print(convert2JavaType(castedType));
    getPrinter().print(") ");
    getPrinter().print(node.getName());
    state.endStatement();

    getPrinter().print(getVarName(node));
    getPrinter().print(" = ");
    node.getThenExpression().accept(getTraverser());
    state.endStatement();

    getPrinter().unindent();
    getPrinter().println("} else {");
    getPrinter().indent();

    getPrinter().print(getVarName(node));
    getPrinter().print(" = ");
    printConverted(getPrinter(),
        exprType,
        normalize(TypeCheck3.typeOf(node.getElseExpression())),
        p -> node.getElseExpression().accept(getTraverser())
    );
    state.endStatement();

    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTIfThenElseExpression node) {
    // basically a copy of ASTConditionalExpression
    SymTypeExpression exprType = normalize(typeOf(node));
    SymTypeExpression trueType = normalize(typeOf(node.getThenExpression()));
    SymTypeExpression falseType = normalize(typeOf(node.getElseExpression()));

    state.startParentheses();
    node.getCondition().accept(getTraverser());
    state.endParentheses();
    getPrinter().print(" ? ");
    printConverted(
        getPrinter(), exprType, trueType,
        p -> node.getThenExpression().accept(getTraverser())
    );
    getPrinter().print(" : ");
    printConverted(
        getPrinter(), exprType, falseType,
        p -> node.getElseExpression().accept(getTraverser())
    );
  }

  @Override
  public void traverse(ASTImpliesExpression node) {
    getPrinter().print("!(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(") || (");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void traverse(ASTForallExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    getPrinter().print("Boolean ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = true;");

    for (ASTInDeclaration dec : node.getInDeclarationList()) {
      dec.accept(getTraverser());
    }

    getPrinter().print(getVarName(node));
    getPrinter().print(" &= ");
    node.getExpression().accept(getTraverser());
    state.endStatement();

    for (int i = node.getInDeclarationList().size() - 1; i >= 0; i--) {
      printEndBrackets(node.getInDeclaration(i));
    }

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTExistsExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    getPrinter().print("Boolean ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = false;");

    for (ASTInDeclaration dec : node.getInDeclarationList()) {
      dec.accept(getTraverser());
    }

    getPrinter().print(getVarName(node));
    getPrinter().print(" |= ");
    node.getExpression().accept(getTraverser());
    state.endStatement();

    for (int i = node.getInDeclarationList().size() - 1; i >= 0; i--) {
      printEndBrackets(node.getInDeclaration(i));
    }

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTLetinExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    for (ASTOCLVariableDeclaration dec : node.getOCLVariableDeclarationList()) {
      dec.accept(getTraverser());
    }

    getPrinter().print("return ");
    node.getExpression().accept(getTraverser());
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTIterateExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node.getInit().getExpression()));

    node.getInit().accept(getTraverser());

    node.getIteration().accept(getTraverser());
    getPrinter().print(node.getName());
    getPrinter().print(" = ");
    node.getValue().accept(getTraverser());
    state.endStatement();

    printEndBrackets(node.getIteration());

    getPrinter().print("return ");
    getPrinter().print(node.getName());
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTEquivalentExpression node) {
    ASTExpression left = node.getLeft();
    ASTExpression right = node.getRight();
    SymTypeExpression leftType = normalize(TypeCheck3.typeOf(left));
    if (leftType.isPrimitive()) {
      getPrinter().print("java.util.Objects.equals(");
      left.accept(getTraverser());
      getPrinter().print(", ");
      right.accept(getTraverser());
      getPrinter().print(")");
    }
    else {
      left.accept(getTraverser());
      getPrinter().print(".equals(");
      right.accept(getTraverser());
      getPrinter().print(")");
    }
  }

  @Override
  public void traverse(ASTInDeclaration node) {
    Optional<SymTypeExpression> innerTypeOpt = getInnerType(node);
    if (innerTypeOpt.isEmpty()) {
      Log.error("0xFD380 could not derive inner type",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
      return;
    }

    if (!node.isPresentExpression()) {
      Log.error("0xFD381 unsupported in-declaration without expression",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
      return;
    }

    String innerTypeStr = convert2JavaType(innerTypeOpt.get());
    for (ASTInDeclarationVariable var : node.getInDeclarationVariableList()) {
      getPrinter().print("for (");
      getPrinter().print(innerTypeStr);
      getPrinter().print(" ");
      getPrinter().print(var.getName());
      getPrinter().print(" : ");
      node.getExpression().accept(getTraverser());
      getPrinter().println(") {");
      getPrinter().indent();
    }
  }

  @Override
  public void traverse(ASTOCLVariableDeclaration node) {
    if (node.isPresentMCType()) {
      getPrinter().print(convert2JavaType(
          normalize(TypeCheck3.symTypeFromAST(node.getMCType()))
      ));
    }
    else if (node.isPresentExpression()) {
      getPrinter().print(convert2JavaType(
          normalize(TypeCheck3.typeOf(node.getExpression()))
      ));
    }
    else {
      Log.error("0xFD382 invalid OCL variable declaration",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
      return;
    }

    getPrinter().print(" ");
    getPrinter().print(node.getName());
    if (node.isPresentExpression()) {
      getPrinter().print(" = ");
      node.getExpression().accept(getTraverser());
    }
    state.endStatement();
  }

  @Override
  public void traverse(ASTAnyExpression node) {
    getPrinter().print("(");
    node.getExpression().accept(getTraverser());
    getPrinter().print(").stream().findAny().get()");
  }

  @Override
  public void traverse(ASTOCLAtPreQualification node) {
    state._willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void traverse(ASTOCLTransitiveQualification node) {
    state._willBeRemoved_logUnimplemented(node);
  }

  protected Optional<SymTypeExpression> getInnerType(ASTInDeclaration node) {
    if (node.isPresentMCType()) {
      SymTypeExpression type = normalize(TypeCheck3.symTypeFromAST(node.getMCType()));
      if (type.isObscureType()) {
        return Optional.empty();
      }
      return Optional.of(type);
    }
    if (!node.isPresentExpression()) {
      return Optional.empty();
    }
    SymTypeExpression exprType = normalize(TypeCheck3.typeOf(node.getExpression()));
    if (!exprType.isGenericType()) {
      return Optional.empty();
    }
    SymTypeExpression innerType =
        MCCollectionSymTypeRelations.getCollectionElementType(exprType);
    if (innerType.isObscureType()) {
      return Optional.empty();
    }
    return Optional.of(innerType);
  }

  protected void printEndBrackets(ASTInDeclaration node) {
    if (!node.isPresentExpression()) {
      return;
    }
    for (int i = 0; i < node.getInDeclarationVariableList().size(); i++) {
      getPrinter().unindent();
      getPrinter().println("}");
    }
  }

  protected String getVarName(ASTNode node) {
    return "var_" + Node2Name.getName(node);
  }

}

