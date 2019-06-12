/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.javastatements._ast.*;
import de.monticore.javastatements._visitor.JavaStatementsVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.Iterator;

/*
 * $Id: JavaDSLWriterVisitor.java,v 1.4 2008-07-17 08:34:01 cficek Exp $
 */

public class JavaStatementsPrettyPrinter implements
        JavaStatementsVisitor {

  protected IndentPrinter printer = null;

  private boolean WRITE_COMMENTS = false;

  private JavaStatementsVisitor realThis = this;

  public JavaStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
    setWriteComments(true);
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  protected void printNode(String s) {
    getPrinter().print(s);
  }
  
  protected void printList(Iterator<? extends ASTMCBasicTypesNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }
  
  protected void printExpressionsList(Iterator<? extends ASTExpressionsBasisNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }

  @Override
  public void handle(ASTClassBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isStatic()) {
      getPrinter().print("static ");
    }
    a.getJavaBlock().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnnotationTypeDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getModifierList().iterator(), " ");
    getPrinter().print("@ interface ");
    printNode(a.getName());
    a.getAnnotationTypeBody().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnnotationTypeBody a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println(" {");
    getPrinter().indent();
    printSeparated(a.getAnnotationTypeElementDeclarationList().iterator(), "");
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTVariableDeclarator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getDeclaratorId().accept(getRealThis());
    if (a.isPresentVariableInititializerOrExpression()) {
      getPrinter().print(" = ");
      a.getVariableInititializerOrExpression().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDeclaratorId a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printNode(a.getName());
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayInitializer a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("{");
    printSeparated(a.getVariableInititializerOrExpressionList().iterator(), ", ");
    getPrinter().print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnnotationMethod a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    printNode(a.getName());
    getPrinter().print("()");
    if (a.isPresentDefaultValue()) {
      a.getDefaultValue().accept(getRealThis());
    }
    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnnotationConstant a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    printSeparated(a.getVariableDeclaratorList().iterator(), ", ");
    getPrinter().println("");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDefaultValue a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" default ");
    a.getElementValueOrExpr().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLocalVariableDeclarationStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getLocalVariableDeclaration().accept(getRealThis());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLocalVariableDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getPrimitiveModifierList().iterator(), " ");
    getPrinter().print(" ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    printSeparated(a.getVariableDeclaratorList().iterator(), ", ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTJavaBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("{");
    getPrinter().indent();
    printSeparated(a.getBlockStatementList().iterator(), "");
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTMethodDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getMethodSignature().accept(getRealThis());
    if (a.isPresentMethodBody()) {
      a.getMethodBody().accept(getRealThis());
    }
    else {
      getPrinter().println(";");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTMethodSignature a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getModifierList().iterator(), " ");
    if (a.isPresentTypeParameters()) {
      a.getTypeParameters().accept(getRealThis());
    }
    a.getMCReturnType().accept(getRealThis());
    getPrinter().print(" ");
    printNode(a.getName());
    a.getFormalParameters().accept(getRealThis());
    for (int i = 0; i > a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    if (a.isPresentThrows()) {
      getPrinter().print(" throws ");
      a.getThrows().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTThrows a) {
    printList(a.getMCQualifiedNameList().iterator(), ", ");
  }

  @Override
  public void handle(ASTIfStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("if (");
    a.getCondition().accept(getRealThis());
    getPrinter().print(") ");
    a.getThenStatement().accept(getRealThis());
    if (a.isPresentElseStatement()) {
      getPrinter().println("else ");
      a.getElseStatement().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLabeledStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printNode(a.getLabel());
    getPrinter().print(": ");
    a.getStatement().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTForStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("for (");
    a.getForControl().accept(getRealThis());
    getPrinter().print(")");
    a.getStatement().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("while (");
    a.getCondition().accept(getRealThis());
    getPrinter().print(")");
    a.getStatement().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDoWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("do ");
    a.getStatement().accept(getRealThis());
    getPrinter().print("while (");
    a.getCondition().accept(getRealThis());
    getPrinter().println(");");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try ");
    a.getJavaBlock().accept(getRealThis());
    getPrinter().println();
    a.getExceptionHandler().accept(getRealThis());
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchExceptionsHandler a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getCatchClauseList().iterator(), "");
    if (a.isPresentFinallyBlock()) {
      getPrinter().println();
      getPrinter().println("finally");
      getPrinter().indent();
      a.getFinallyBlock().accept(getRealThis());
      getPrinter().unindent();
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFinallyBlockOnlyHandler a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("finally");
    getPrinter().indent();
    a.getFinallyBlock().accept(getRealThis());
    getPrinter().unindent();
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryStatementWithResources a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("try (");
    printSeparated(a.getResourceList().iterator(), ";");
    getPrinter().println(") ");
    a.getJavaBlock().accept(getRealThis());
    printSeparated(a.getCatchClauseList().iterator(), "");
    if (a.isPresentFinallyBlock()) {
      getPrinter().println();
      getPrinter().println("finally");
      getPrinter().indent();
      a.getFinallyBlock().accept(getRealThis());
      getPrinter().unindent();
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTResource a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getPrimitiveModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    a.getDeclaratorId().accept(getRealThis());
    getPrinter().print(" = ");
    a.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCommonForControl a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentForInit()) {
      a.getForInit().accept(getRealThis());
    }
    getPrinter().print(";");
    if (a.isPresentCondition()) {
      a.getCondition().accept(getRealThis());
    }
    getPrinter().print(";");
    printExpressionsList(a.getExpressionList().iterator(), ",");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTForInitByExpressions a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" ");
    printExpressionsList(a.getExpressionList().iterator(), ", ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEnhancedForControl a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getFormalParameter().accept(getRealThis());
    getPrinter().print(": ");
    a.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstructorDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println();
    printSeparated(a.getModifierList().iterator(), " ");
    if (a.isPresentTypeParameters()) {
      a.getTypeParameters().accept(getRealThis());
    }
    printNode(a.getName());
    a.getFormalParameters().accept(getRealThis());

    if (a.isPresentThrows()) {
      getPrinter().print(" throws ");
      a.getThrows().accept(getRealThis());
    }
    getPrinter().print(" ");
    a.getConstructorBody().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFieldDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    printSeparated(a.getVariableDeclaratorList().iterator(), ", ");
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    printSeparated(a.getConstantDeclaratorList().iterator(), ", ");
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstantDeclarator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printNode(a.getName());
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[] ");
    }
    getPrinter().print(" = ");
    a.getVariableInititializerOrExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFormalParameters a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("(");
    if (a.isPresentFormalParameterListing()) {
      a.getFormalParameterListing().accept(getRealThis());
    }
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFormalParameterListing a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getFormalParameterList().iterator(), ",");
    if (!a.getFormalParameterList().isEmpty() && a.isPresentLastFormalParameter()) {
      getPrinter().print(",");
    }
    if (a.isPresentLastFormalParameter()) {
      a.getLastFormalParameter().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFormalParameter a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getPrimitiveModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    a.getDeclaratorId().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLastFormalParameter a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getPrimitiveModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ... ");
    a.getDeclaratorId().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnnotation a) {
    getPrinter().print("@");
    a.getAnnotationName().accept(getRealThis());
    if (a.isPresentAnnotationArguments()) {
      getPrinter().print("(");
      a.getAnnotationArguments().accept(getRealThis());
      getPrinter().print(");");
    }
  }

  protected void printDimensions(int dims) {
    for (int i = 0; i < dims; i++) {
      getPrinter().print("[]");
    }
  }

  @Override
  public void handle(ASTAnnotationPairArguments a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getElementValuePairList().iterator(), ", ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTElementValuePair a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printNode(a.getName());
    getPrinter().print(" = ");
    a.getElementValueOrExpr().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTPrimitiveModifier a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" " + printModifier(a.getModifier()) + " ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTEmptyDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(");");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAssertStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("assert ");
    a.getAssertion().accept(getRealThis());
    if (a.isPresentMessage()) {
      getPrinter().print(" : ");
      a.getMessage().accept(getRealThis());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTBreakStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("break");
    if (a.isPresentLabel()) {
      printNode(a.getLabel());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTContinueStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("continue");
    if (a.isPresentLabel()) {
      printNode(a.getLabel());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTSynchronizedStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("synchronized (");
    a.getExpression().accept(getRealThis());
    getPrinter().print(") ");
    a.getJavaBlock().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTReturnStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("return ");
    if (a.isPresentExpression()) {
      a.getExpression().accept(getRealThis());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTThrowStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("throw ");
    a.getExpression().accept(getRealThis());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEmptyStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTExpressionStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getExpression().accept(getRealThis());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTSwitchStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("switch (");
    a.getExpression().accept(getRealThis());
    getPrinter().println(") {");
    getPrinter().indent();
    printSeparated(a.getSwitchBlockStatementGroupList().iterator(), "");
    printSeparated(a.getSwitchLabelList().iterator(), "");
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstantExpressionSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("case ");
    a.getConstantExpression().accept(getRealThis());
    getPrinter().println(":");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEnumConstantSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("case ");
    printNode(a.getEnumConstantName());
    getPrinter().println(":");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDefaultSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("default:");
  }

  @Override
  public void handle(ASTCatchClause a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("catch (");
    printSeparated(a.getPrimitiveModifierList().iterator(), " ");
    a.getCatchType().accept(getRealThis());
    getPrinter().print(" ");
    printNode(a.getName());
    getPrinter().print(") ");
    a.getJavaBlock().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchType a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printList(a.getMCQualifiedNameList().iterator(), "|");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTArrayCreator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("new ");
    a.getCreatedName().accept(getRealThis());
    a.getArrayDimensionSpecifier().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayDimensionByInitializer a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    getPrinter().print(" ");
    a.getArrayInitializer().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayDimensionByExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("[");
    printExpressionsList(a.getExpressionList().iterator(), "");
    getPrinter().print("]");
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCreatedName a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    String sep = "";
    for (ASTMCObjectType oType: a.getMCObjectTypeList()) {
      getPrinter().print(sep);
      sep = ".";
      oType.accept(getRealThis());
    }
    if (a.isPresentMCPrimitiveType()) {
      a.getMCPrimitiveType().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCreatorExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" new ");
    a.getCreator().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  protected void printSeparated(Iterator<? extends ASTJavaStatementsNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }

  public void setWriteComments(boolean wc) {
    WRITE_COMMENTS = wc;
  }

  public boolean isWriteCommentsEnabeled() {
    return WRITE_COMMENTS;
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTJavaStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  private String printModifier(int constant) {

    switch (constant) {
      case ASTConstantsJavaStatements.PRIVATE:
        return "private";
      case ASTConstantsJavaStatements.PUBLIC:
        return "public";
      case ASTConstantsJavaStatements.PROTECTED:
        return "protected";
      case ASTConstantsJavaStatements.STATIC:
        return "static";
      case ASTConstantsJavaStatements.TRANSIENT:
        return "transient";
      case ASTConstantsJavaStatements.FINAL:
        return "final";
      case ASTConstantsJavaStatements.ABSTRACT:
        return "abstract";
      case ASTConstantsJavaStatements.NATIVE:
        return "native";
      case ASTConstantsJavaStatements.THREADSAFE:
        return "threadsafe";
      case ASTConstantsJavaStatements.SYNCHRONIZED:
        return "synchronized";
      case ASTConstantsJavaStatements.VOLATILE:
        return "volatile";
      case ASTConstantsJavaStatements.STRICTFP:
        return "strictfp";
      default:
        return null;
    }

  }

  /**
   * @see de.monticore.javastatements._visitor.JavaStatementsVisitor#setRealThis(de.monticore.javastatements._visitor.JavaStatementsVisitor)
   */
  @Override
  public void setRealThis(JavaStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.javastatements._visitor.JavaStatementsVisitor#getRealThis()
   */
  @Override
  public JavaStatementsVisitor getRealThis() {
    return realThis;
  }
  
  
}
