/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.javalight._ast.*;
import de.monticore.javalight._visitor.JavaLightVisitor;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.Iterator;

public class JavaLightPrettyPrinter extends MCCommonStatementsPrettyPrinter implements
    JavaLightVisitor {

  private JavaLightVisitor realThis = this;

  public JavaLightPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  protected void printNode(String s) {
    getPrinter().print(s);
  }

  @Override
  public void handle(ASTExtendedBlockStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentLocalVariableDeclaration()) {
      a.getLocalVariableDeclaration().accept(getRealThis());
    }
    if (a.isPresentTypeDeclaration()) {
      a.getTypeDeclaration().accept(getRealThis());
    }
    if (a.isPresentMCStatement()) {
      a.getMCStatement().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTClassBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isStatic()) {
      getPrinter().print("static ");
    }
    a.getMCJavaBlock().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnnotationTypeDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getMCModifierList().iterator(), " ");
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
    printJavaLightList(a.getAnnotationTypeElementDeclarationList().iterator(), "");
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTAnnotationMethod a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getMCModifierList().iterator(), " ");
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
    printSeparated(a.getMCModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    printSeparated(a.getVariableDeclaratorList().iterator(), ", ");
    getPrinter().println(";");
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
  public void handle(ASTMethodDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getMethodSignature().accept(getRealThis());
    if (a.isPresentMethodBody()) {
      a.getMethodBody().accept(getRealThis());
    } else {
      getPrinter().println(";");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTInterfaceMethodDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getMethodSignature().accept(getRealThis());
    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTMethodSignature a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getMCModifierList().iterator(), " ");
    if (a.isPresentExtTypeParameters()) {
      a.getExtTypeParameters().accept(getRealThis());
    }
    a.getMCReturnType().accept(getRealThis());
    getPrinter().print(" ");
    printNode(a.getName());
    a.getFormalParameters().accept(getRealThis());
    for (int i = 0; i < a.getDimList().size(); i++) {
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
    for (int i = 0; i < a.getMCQualifiedNameList().size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      a.getMCQualifiedName(i).accept(getRealThis());
    }
  }


  @Override
  public void handle(ASTConstructorDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println();
    printSeparated(a.getMCModifierList().iterator(), " ");
    if (a.isPresentExtTypeParameters()) {
      a.getExtTypeParameters().accept(getRealThis());
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
    printSeparated(a.getMCModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    printSeparated(a.getVariableDeclaratorList().iterator(), ", ");
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getMCModifierList().iterator(), " ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    printJavaLightList(a.getConstantDeclaratorList().iterator(), ", ");
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
    printJavaLightList(a.getFormalParameterList().iterator(), ",");
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
      getPrinter().print(")");
    }
  }

  @Override
  public void handle(ASTAnnotationPairArguments a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printJavaLightList(a.getElementValuePairList().iterator(), ", ");
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
  public void handle(ASTElementValueArrayInitializer a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("{");
    for (int i = 0; i < a.getElementValueOrExprList().size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      a.getElementValueOrExpr(i).accept(getRealThis());
    }
    getPrinter().print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEmptyDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayCreator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
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
    for (ASTExpression astExpression : a.getExpressionList()) {
      getPrinter().print("[");
      astExpression.accept(getRealThis());
      getPrinter().print("]");
    }
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCreatedName a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    String sep = "";
    for (ASTMCObjectType oType : a.getMCObjectTypeList()) {
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

  @Override
  public void handle(ASTEnhancedForControl a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getFormalParameter().accept(getRealThis());
    getPrinter().print(": ");
    a.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  protected void printJavaLightList(Iterator<? extends ASTJavaLightNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTJavaLightNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }


  @Override
  public void setRealThis(JavaLightVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public JavaLightVisitor getRealThis() {
    return realThis;
  }


}
