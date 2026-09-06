// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mcassertstatements.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._visitor.MCAssertStatementsInheritanceHandler;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Provides Java code generations for MCAssertStatements
 */
public class MCAssertStatementsJavaGenVisitor
    extends MCAssertStatementsInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCAssertStatementsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTAssertStatement node) {
    SymTypeExpression exprType = normalize(typeOf(node.getAssertion()));

    getPrinter().print("assert ");
    state.startParentheses();
    printConverted(
        getPrinter(),
        createPrimitive(BasicSymbolsMill.BOOLEAN),
        exprType,
        p -> node.getAssertion().accept(getTraverser())
    );
    state.endParentheses();

    // message
    if (node.isPresentMessage()) {
      getPrinter().println(" :");
      getPrinter().indent();
      state.startParentheses();
      node.getMessage().accept(getTraverser());
      state.endParentheses();
      getPrinter().unindent();
    }

    state.endStatement();
  }

}
