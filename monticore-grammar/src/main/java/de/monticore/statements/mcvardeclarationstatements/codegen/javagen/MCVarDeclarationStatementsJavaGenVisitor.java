// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mcvardeclarationstatements.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclarationStatement;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsInheritanceHandler;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.MapBasedTypeCheck3;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Provides Java code generations for MCVarDeclarationStatements
 */
public class MCVarDeclarationStatementsJavaGenVisitor
    extends MCVarDeclarationStatementsInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCVarDeclarationStatementsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  // This is a rather simplified implementation,
  // that may need to be extended to support some corner cases
  // in some languages.

  // recursive functions are currently not supported
  // they could be supported, e.g., through temporary variables:
  /*
  Function1<Integer, Integer> s = ((Supplier<Function1<Integer, Integer>>)
      () -> {
        @SuppressWarnings("unchecked")
        Function1<Integer, Integer>[] tmp = new Function1[1];
        tmp[0] = (Integer n) -> (((n) > (0)) ? (n) + ((tmp[0]).apply((n) - (1))) : 0);
        return tmp[0];
      }
  ).get();
  */

  @Override
  public void traverse(ASTLocalVariableDeclarationStatement node) {
    // Q: Why is the logic not in traverse(ASTLocalVariableDeclaration)?
    // A: Because that is not a statement.
    // Thus, it is not obvious,
    // whether we can print a statement in that context.
    // Thus, the logic is written here, as this is a statement,
    // And statements are printed to Java statements.
    ASTLocalVariableDeclaration varDeclaration =
        node.getLocalVariableDeclaration();

    // for `int x = 2, y = 3` we will print
    // `int x = 2; int y = 3;`,
    // just for simplicity
    for (ASTVariableDeclarator varDeclarator : varDeclaration.getVariableDeclaratorList()) {
      for (ASTMCModifier modifier : varDeclaration.getMCModifierList()) {
        modifier.accept(getTraverser());
        getPrinter().print(" ");
      }
      varDeclaration.getMCType().accept(getTraverser());
      getPrinter().print(" ");
      varDeclarator.accept(getTraverser());
      state.endStatement();
    }
  }

  // Note: The current usage of VariableDeclarator in MontiCore
  // should make this directly compatible with VariableDeclarators in Java.
  // Thus, we can use a direct mapping.
  @Override
  public void traverse(ASTVariableDeclarator node) {
    getPrinter().print(node.getDeclarator().getName());
    if (node.isPresentVariableInit()) {
      getPrinter().print(" = ");
      node.getVariableInit().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTSimpleInit exprInit) {
    ASTExpression expr = exprInit.getExpression();
    SymTypeExpression exprType = typeOf(expr);
    SymTypeExpression targetType = _hack_getTargetType(expr);
    printConverted(
        getPrinter(),
        targetType,
        exprType,
        p -> expr.accept(getTraverser())
    );
  }

  // hack, need real solution in the future
  // will be removed
  SymTypeExpression _hack_getTargetType(ASTExpression expr) {
    return MapBasedTypeCheck3.internal_hacky_do_not_use_getCtx4Ast()
        .getContextOfExpression(expr)
        .getTargetType();
  }

}
