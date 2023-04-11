/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.javalight._ast.*;
import de.monticore.javalight._visitor.JavaLightHandler;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.javalight._visitor.JavaLightVisitor2;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;

import java.util.Iterator;

@Deprecated(forRemoval = true)
public class JavaLightPrettyPrinter implements
    JavaLightVisitor2, JavaLightHandler {

  protected JavaLightTraverser traverser;
  
  protected IndentPrinter printer;
  
  public JavaLightPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  @Override
  public JavaLightTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(JavaLightTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  protected void printNode(String s) {
    getPrinter().print(s);
  }

  @Override
  public void handle(ASTMethodDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getMCModifierList().stream().forEach(m -> {getPrinter().print(" "); m.accept(getTraverser()); getPrinter().print(" ");});
    if (a.isPresentExtTypeParameters()) {
      a.getExtTypeParameters().accept(getTraverser());
    }
    a.getMCReturnType().accept(getTraverser());
    getPrinter().print(" ");
    printNode(a.getName());
    a.getFormalParameters().accept(getTraverser());
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    if (a.isPresentThrows()) {
      getPrinter().print(" throws ");
      a.getThrows().accept(getTraverser());
    }
    if (a.isPresentMCJavaBlock()) {
      a.getMCJavaBlock().accept(getTraverser());
    } else {
      getPrinter().println(";");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTThrows a) {
    for (int i = 0; i < a.getMCQualifiedNameList().size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      a.getMCQualifiedName(i).accept(getTraverser());
    }
  }


  @Override
  public void handle(ASTConstructorDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println();
    a.getMCModifierList().stream().forEach(m -> {getPrinter().print(" "); m.accept(getTraverser()); getPrinter().print(" ");});
    if (a.isPresentExtTypeParameters()) {
      a.getExtTypeParameters().accept(getTraverser());
    }
    printNode(a.getName());
    a.getFormalParameters().accept(getTraverser());

    if (a.isPresentThrows()) {
      getPrinter().print(" throws ");
      a.getThrows().accept(getTraverser());
    }
    a.getMCJavaBlock().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getLocalVariableDeclaration().accept(getTraverser());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFormalParameters a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("(");
    if (a.isPresentFormalParameterListing()) {
      a.getFormalParameterListing().accept(getTraverser());
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
      a.getLastFormalParameter().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLastFormalParameter a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getJavaModifierList().iterator(), " ");
    a.getMCType().accept(getTraverser());
    getPrinter().print(" ... ");
    a.getDeclaratorId().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnnotation a) {
    getPrinter().print("@");
    a.getAnnotationName().accept(getTraverser());
    if (a.isPresentAnnotationArguments()) {
      getPrinter().print("(");
      a.getAnnotationArguments().accept(getTraverser());
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
    a.getElementValueOrExpr().accept(getTraverser());
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
      a.getElementValueOrExpr(i).accept(getTraverser());
    }
    getPrinter().print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayDimensionByInitializer a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    getPrinter().print(" ");
    a.getArrayInit().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  protected void printJavaLightList(Iterator<? extends ASTJavaLightNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = separator;
    }
  }

  protected void printSeparated(Iterator<? extends ASTMCCommonStatementsNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = separator;
    }
  }
  
}
