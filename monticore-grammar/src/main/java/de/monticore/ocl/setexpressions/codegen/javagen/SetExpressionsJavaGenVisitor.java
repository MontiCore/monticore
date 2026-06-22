// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.setexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.util.Node2Name;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2BoxedJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaType;
import static de.monticore.types3.SymTypeRelations.normalize;

/**
 * Provides Java code generations for SetExpressions
 */
public class SetExpressionsJavaGenVisitor
    extends SetExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public SetExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTSetInExpression node) {
    node.getSet().accept(getTraverser());
    getPrinter().print(".contains(");
    node.getElem().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void traverse(ASTSetNotInExpression node) {
    getPrinter().print("!");
    node.getSet().accept(getTraverser());
    getPrinter().print(".contains(");
    node.getElem().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void traverse(ASTUnionExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    getPrinter().print("java.util.Set<");
    printDerivedInnerType(node);
    getPrinter().print("> ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = new java.util.HashSet<>();");

    getPrinter().print(getVarName(node));
    getPrinter().print(".addAll(");
    node.getLeft().accept(getTraverser());
    getPrinter().println(");");

    getPrinter().print(getVarName(node));
    getPrinter().print(".addAll(");
    node.getRight().accept(getTraverser());
    getPrinter().println(");");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTIntersectionExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    getPrinter().print("java.util.Set<");
    printDerivedInnerType(node);
    getPrinter().print("> ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = new java.util.HashSet<>();");

    getPrinter().print(getVarName(node));
    getPrinter().print(".addAll(");
    node.getLeft().accept(getTraverser());
    getPrinter().println(");");

    getPrinter().print(getVarName(node));
    getPrinter().print(".retainAll(");
    node.getRight().accept(getTraverser());
    getPrinter().println(");");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetMinusExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    getPrinter().print("java.util.Set<");
    printDerivedInnerType(node);
    getPrinter().print("> ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = new java.util.HashSet<>();");

    getPrinter().print(getVarName(node));
    getPrinter().print(".addAll(");
    node.getLeft().accept(getTraverser());
    getPrinter().println(");");

    getPrinter().print(getVarName(node));
    getPrinter().print(".removeAll(");
    node.getRight().accept(getTraverser());
    getPrinter().println(");");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetUnionExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    printDerivedType(node.getSet());
    getPrinter().print(" ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print(" = ");
    node.getSet().accept(getTraverser());
    state.endStatement();

    getPrinter().print("java.util.Set<");
    printDerivedInnerType(node);
    getPrinter().print("> ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = new java.util.HashSet<>();");

    getPrinter().print("for (");
    printDerivedType(node);
    getPrinter().print(" ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_item : ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().println(") {");
    getPrinter().indent();

    getPrinter().print(getVarName(node));
    getPrinter().print(".addAll(");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_item");
    getPrinter().println(");");

    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetIntersectionExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    printDerivedType(node.getSet());
    getPrinter().print(" ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print(" = ");
    node.getSet().accept(getTraverser());
    state.endStatement();

    getPrinter().print("java.util.Set<");
    printDerivedInnerType(node);
    getPrinter().print("> ");
    getPrinter().print(getVarName(node));
    getPrinter().print(" = ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().println(".stream().findAny().orElse(new java.util.HashSet<>());");

    getPrinter().print("for (");
    printDerivedType(node);
    getPrinter().print(" ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_item : ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().println(") {");
    getPrinter().indent();

    getPrinter().print(getVarName(node));
    getPrinter().print(".retainAll(");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_item");
    getPrinter().println(");");

    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetAndExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    getPrinter().print("Boolean ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = true;");

    printDerivedType(node.getSet());
    getPrinter().print(" ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_toIter");
    getPrinter().print(" = ");
    node.getSet().accept(getTraverser());
    state.endStatement();

    getPrinter().print("for (Boolean ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_item : ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_toIter");
    getPrinter().println(") {");
    getPrinter().indent();

    getPrinter().print(getVarName(node));
    getPrinter().print(" &= ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().println("_item;");

    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetOrExpression node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    getPrinter().print("Boolean ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = false;");

    printDerivedType(node.getSet());
    getPrinter().print(" ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_toIter");
    getPrinter().print(" = ");
    node.getSet().accept(getTraverser());
    state.endStatement();

    getPrinter().print("for (Boolean ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_item : ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().print("_toIter");
    getPrinter().println(") {");
    getPrinter().indent();

    getPrinter().print(getVarName(node));
    getPrinter().print(" |= ");
    getPrinter().print(getVarName(node.getSet()));
    getPrinter().println("_item;");

    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetComprehension node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));
    printDerivedType(node);
    getPrinter().print(" ");
    getPrinter().print(getVarName(node));
    getPrinter().print(" = ");
    if (node.isSet()) {
      getPrinter().println("new java.util.HashSet<>();");
    }
    else {
      getPrinter().println("new java.util.LinkedList<>();");
    }

    if (node.getLeft().isPresentGeneratorDeclaration()) {
      node.getLeft().accept(getTraverser());
    }
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      item.accept(getTraverser());
    }

    getPrinter().print(getVarName(node));
    getPrinter().print(".add(");
    if (node.getLeft().isPresentGeneratorDeclaration()) {
      getPrinter().print(node.getLeft().getGeneratorDeclaration().getName());
    }
    else if (node.getLeft().isPresentExpression()) {
      node.getLeft().getExpression().accept(getTraverser());
    }
    else {
      node.getLeft().accept(getTraverser());
    }
    getPrinter().println(");");

    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      if (!item.isPresentSetVariableDeclaration()) {
        getPrinter().unindent();
        getPrinter().println("}");
      }
    }
    if (node.getLeft().isPresentGeneratorDeclaration()) {
      getPrinter().unindent();
      getPrinter().println("}");
    }

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetComprehensionItem node) {
    if (node.isPresentExpression()) {
      SymTypeExpression type = normalize(TypeCheck3.typeOf(node.getExpression()));
      Preconditions.checkState(SymTypeRelations.isBoolean(type));
      getPrinter().print("if (");
      node.getExpression().accept(getTraverser());
      getPrinter().println(") {");
      getPrinter().indent();
    }
    else if (node.isPresentGeneratorDeclaration()) {
      node.getGeneratorDeclaration().accept(getTraverser());
    }
    else if (node.isPresentSetVariableDeclaration()) {
      ASTSetVariableDeclaration setVarDecl = node.getSetVariableDeclaration();
      if (setVarDecl.isPresentMCType()) {
        printDerivedType(TypeCheck3.symTypeFromAST(setVarDecl.getMCType()));
      }
      else if (setVarDecl.isPresentExpression()) {
        printDerivedType(setVarDecl.getExpression());
      }
      else {
        Log.error("0xFD372 invalid set variable declaration",
            setVarDecl.get_SourcePositionStart(),
            setVarDecl.get_SourcePositionEnd()
        );
      }
      getPrinter().print(" ");
      getPrinter().print(setVarDecl.getName());
      getPrinter().print(" = ");
      setVarDecl.getExpression().accept(getTraverser());
      state.endStatement();
    }
    else {
      Log.error("0xFD371 missing implementation",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  @Override
  public void traverse(ASTGeneratorDeclaration node) {
    getPrinter().print("for (");
    if (node.isPresentMCType()) {
      printDerivedType(TypeCheck3.symTypeFromAST(node.getMCType()));
    }
    else {
      printDerivedInnerType(node.getExpression());
    }
    getPrinter().print(" ");
    getPrinter().print(node.getName());
    getPrinter().print(" : ");
    node.getExpression().accept(getTraverser());
    getPrinter().println(") {");
    getPrinter().indent();
  }

  @Override
  public void traverse(ASTSetEnumeration node) {
    state.printExpressionBeginLambda(TypeCheck3.typeOf(node));

    printDerivedType(node);
    getPrinter().print(" ");
    getPrinter().print(getVarName(node));
    getPrinter().print(" = ");
    if (node.isSet()) {
      getPrinter().println("new java.util.HashSet<>();");
    }
    else {
      getPrinter().println("new java.util.LinkedList<>();");
    }

    for (ASTSetCollectionItem item : node.getSetCollectionItemList()) {
      getPrinter().print(getVarName(node));
      getPrinter().print(".addAll(");
      item.accept(getTraverser());
      getPrinter().println(");");
    }

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    state.printExpressionEndLambda();
  }

  @Override
  public void traverse(ASTSetValueItem node) {
    getPrinter().print("java.util.Collections.singleton(");
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void traverse(ASTSetValueRange node) {
    SymTypeExpression boundType = normalize(TypeCheck3.typeOf(node.getLowerBound()));
    String boundTypeStr = convert2BoxedJavaType(boundType);

    getPrinter().print("((java.util.function.Supplier<");
    getPrinter().print("java.util.List<");
    getPrinter().print(boundTypeStr);
    getPrinter().print(">>) () -> {");
    getPrinter().println();
    getPrinter().indent();

    getPrinter().print("java.util.List<");
    getPrinter().print(boundTypeStr);
    getPrinter().print("> ");
    getPrinter().print(getVarName(node));
    getPrinter().println(" = new java.util.LinkedList<>();");

    getPrinter().print(boundTypeStr);
    getPrinter().print(" ");
    getPrinter().print(getVarName(node));
    getPrinter().print("LowerBound = ");
    node.getLowerBound().accept(getTraverser());
    state.endStatement();

    getPrinter().print(boundTypeStr);
    getPrinter().print(" ");
    getPrinter().print(getVarName(node));
    getPrinter().print("UpperBound = ");
    node.getUpperBound().accept(getTraverser());
    state.endStatement();

    getPrinter().print("int ");
    getPrinter().print(getVarName(node));
    getPrinter().println("Step = 1;");

    getPrinter().print("if (");
    getPrinter().print(getVarName(node));
    getPrinter().print("LowerBound > ");
    getPrinter().print(getVarName(node));
    getPrinter().println("UpperBound) {");
    getPrinter().indent();
    getPrinter().print(getVarName(node));
    getPrinter().println("Step = -1;");
    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("for (");
    getPrinter().print(boundTypeStr);
    getPrinter().print(" ");
    getPrinter().print(getVarName(node));
    getPrinter().print("_iter = ");
    getPrinter().print(getVarName(node));
    getPrinter().print("LowerBound; ");
    getPrinter().print(getVarName(node));
    getPrinter().print("_iter * ");
    getPrinter().print(getVarName(node));
    getPrinter().print("Step <= ");
    getPrinter().print(getVarName(node));
    getPrinter().print("UpperBound * ");
    getPrinter().print(getVarName(node));
    getPrinter().print("Step; ");
    getPrinter().print(getVarName(node));
    getPrinter().print("_iter = (");
    getPrinter().print(boundTypeStr);
    getPrinter().print(")(");
    getPrinter().print(getVarName(node));
    getPrinter().print("_iter + ");
    getPrinter().print(getVarName(node));
    getPrinter().println("Step)) {");
    getPrinter().indent();

    getPrinter().print(getVarName(node));
    getPrinter().print(".add(");
    getPrinter().print(getVarName(node));
    getPrinter().println("_iter);");

    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("return ");
    getPrinter().print(getVarName(node));
    state.endStatement();

    getPrinter().unindent();
    getPrinter().print("}).get()");
  }

  protected String getVarName(ASTNode node) {
    return "var_" + Node2Name.getName(node);
  }

  protected void printDerivedType(ASTExpression node) {
    printDerivedType(TypeCheck3.typeOf(node));
  }

  protected void printDerivedType(SymTypeExpression type) {
    SymTypeExpression normalized = normalize(type);
    Preconditions.checkState(!normalized.isObscureType());
    getPrinter().print(convert2JavaType(normalized));
  }

  protected void printDerivedInnerType(ASTExpression node) {
    Optional<SymTypeExpression> innerType = getInnerType(node);
    Preconditions.checkState(innerType.isPresent());
    Preconditions.checkState(!innerType.get().isObscureType());
    getPrinter().print(convert2BoxedJavaType(innerType.get()));
  }

  protected Optional<SymTypeExpression> getInnerType(ASTExpression node) {
    SymTypeExpression type = normalize(TypeCheck3.typeOf(node));
    if (type.isObscureType() || !type.isGenericType()) {
      return Optional.empty();
    }
    SymTypeExpression innerType =
        MCCollectionSymTypeRelations.getCollectionElementType(type);
    if (innerType.isObscureType()) {
      return Optional.empty();
    }
    return Optional.of(innerType);
  }

}

