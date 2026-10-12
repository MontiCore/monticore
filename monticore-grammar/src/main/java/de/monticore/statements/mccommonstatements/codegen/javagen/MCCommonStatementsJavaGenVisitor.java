// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mccommonstatements.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsInheritanceHandler;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BOOLEAN;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Provides Java code generations for MCCommonStatements
 * <p>
 * Alpha Version: little more than a "pretty printer",
 * most edge-cases have not been taken into account.
 * However, as our MCCommonStatements are mostly based on Java,
 * it is expected to work in most cases out of the box.
 */
public class MCCommonStatementsJavaGenVisitor
    extends MCCommonStatementsInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCCommonStatementsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  // Q: why is there no traverse(ASTMCJavaBlock)?
  // A: Because any statement in a model
  // can be printed to multiple statements in Java.
  // To allow this behavior,
  // additional curly brackets need to be added for nearly each statement:
  // Model: if(c) s;
  // Java: if(c_j) { s_j1; s_j2; }
  // Thus, we rely on the printers of ASTNodes containing the statements
  // to add the curly brackets.
  // Compare ASTBracketExpression

  // Interface has no traverse, so we use handle instead
  @Override
  public void handle(ASTJavaModifier node) {
    String modStr = MCCommonStatementsMill.prettyPrint(node, false);
    getPrinter().print(modStr);
  }

  @Override
  public void traverse(ASTIfStatement node) {
    getPrinter().print("if (");
    printExprConvertedToBoolean(node.getCondition());
    getPrinter().print(") ");
    printStatementAsBlock(node.getThenStatement());
    if (node.isPresentElseStatement()) {
      getPrinter().print("else ");
      printStatementAsBlock(node.getElseStatement());
    }
  }

  @Override
  public void traverse(ASTForStatement node) {
    getPrinter().print("for (");
    node.getForControl().accept(getTraverser());
    getPrinter().print(") ");
    printStatementAsBlock(node.getMCStatement());
  }

  @Override
  public void traverse(ASTCommonForControl node) {
    // for-init
    if (node.isPresentForInit()) {
      if (node.getForInit().isPresentLocalVariableDeclaration()) {
        // handle the local variable declaration in a special way
        // to assure compatibility with for-init in Java
        printLocalVariableDeclaration4ForInit(
            node.getForInit().getLocalVariableDeclaration()
        );
      }
      else {
        node.getForInit().accept(getTraverser());
      }
    }
    getPrinter().print(";");

    // condition
    if (node.isPresentCondition()) {
      getPrinter().print(" ");
      printExprConvertedToBoolean(node.getCondition());
    }
    getPrinter().print(";");

    // update
    for (int i = 0; i < node.sizeExpressions(); i++) {
      if (i > 0) {
        getPrinter().print(",");
      }
      getPrinter().print(" ");
      node.getExpression(i).accept(getTraverser());
    }
  }

  /**
   * prints the local variable declaration specifically
   * to be compatible with for-init in Java.
   *
   * @param node the variable declaration to print.
   */
  protected void printLocalVariableDeclaration4ForInit(
      ASTLocalVariableDeclaration node
  ) {
    node.getMCModifierList().forEach(m -> {
      m.accept(getTraverser());
      getPrinter().print(" ");
    });

    node.getMCType().accept(getTraverser());
    getPrinter().print(" ");

    for (int i = 0; i < node.sizeVariableDeclarators(); i++) {
      ASTVariableDeclarator varDeclarator = node.getVariableDeclarator(i);
      if (i > 0) {
        getPrinter().print(", ");
      }
      getPrinter().print(varDeclarator.getDeclarator().getName());
      if (varDeclarator.isPresentVariableInit()) {
        getPrinter().print(" = ");
        varDeclarator.getVariableInit().accept(getTraverser());
      }
    }
  }

  @Override
  public void traverse(ASTForInitByExpressions node) {
    for (int i = 0; i < node.sizeExpressions(); i++) {
      if (i > 0) {
        getPrinter().print(", ");
      }
      node.getExpression(i).accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTEnhancedForControl node) {
    node.getFormalParameter().accept(getTraverser());
    getPrinter().print(" : ");
    state.startParentheses();
    node.getExpression().accept(getTraverser());
    state.endParentheses();
  }

  @Override
  public void traverse(ASTFormalParameter node) {
    node.getMCModifierList().forEach(m -> {
      m.accept(getTraverser());
      getPrinter().print(" ");
    });
    node.getMCType().accept(getTraverser());
    getPrinter().print(" ");
    getPrinter().print(node.getDeclarator().getName());
  }

  @Override
  public void traverse(ASTWhileStatement node) {
    getPrinter().print("while (");
    printExprConvertedToBoolean(node.getCondition());
    getPrinter().print(") ");
    printStatementAsBlock(node.getMCStatement());
  }

  @Override
  public void traverse(ASTDoWhileStatement node) {
    getPrinter().print("do ");
    printStatementAsBlock(node.getMCStatement());
    getPrinter().print("while (");
    printExprConvertedToBoolean(node.getCondition());
    getPrinter().print(")");
    state.endStatement();
  }

  @Override
  public void traverse(ASTSwitchStatement node) {
    getPrinter().print("switch (");
    node.getExpression().accept(getTraverser());
    getPrinter().print(") ");
    state.startStatementBlock();
    node.getSwitchBlockStatementGroupList()
        .forEach(g -> g.accept(getTraverser()));
    node.getSwitchLabelList().forEach(l -> l.accept(getTraverser()));
    state.endStatementBlock();
  }

  @Override
  public void traverse(ASTSwitchBlockStatementGroup node) {
    node.getSwitchLabelList().forEach(l -> l.accept(getTraverser()));
    node.getMCBlockStatementList().forEach(s -> s.accept(getTraverser()));
  }

  @Override
  public void traverse(ASTConstantExpressionSwitchLabel node) {
    getPrinter().print("case ");
    state.startParentheses();
    node.getConstant().accept(getTraverser());
    state.endParentheses();
    getPrinter().println(":");
  }

  @Override
  public void traverse(ASTEnumConstantSwitchLabel node) {
    getPrinter().print("case ");
    getPrinter().print(node.getEnumConstant());
    getPrinter().println(":");
  }

  @Override
  public void traverse(ASTDefaultSwitchLabel node) {
    getPrinter().println("default:");
  }

  @Override
  public void traverse(ASTExpressionStatement node) {
    node.getExpression().accept(getTraverser());
    state.endStatement();
  }

  @Override
  public void traverse(ASTEmptyStatement node) {
    state.endStatement();
  }

  @Override
  public void traverse(ASTBreakStatement node) {
    getPrinter().print("break");
    state.endStatement();
  }

  // helper

  protected void printExprConvertedToBoolean(ASTExpression expr) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    printConverted(
        getPrinter(),
        createPrimitive(BOOLEAN),
        exprType,
        p -> expr.accept(getTraverser())
    );
  }

  protected void printStatementAsBlock(ASTMCStatement stmt) {
    state.startStatementBlock();
    stmt.accept(getTraverser());
    state.endStatementBlock();
  }

}
