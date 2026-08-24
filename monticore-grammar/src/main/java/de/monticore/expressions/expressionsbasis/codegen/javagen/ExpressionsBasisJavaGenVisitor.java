/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.se_rwth.commons.logging.Log;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypePrint;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

public class ExpressionsBasisJavaGenVisitor
    extends ExpressionsBasisInheritanceHandler {

  protected JavaGenVisitorState state;

  public ExpressionsBasisJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTNameExpression node) {
    // NOTE: this is only a temporary implementation,
    // as in the future, templates provided by the functions symbols
    // are to be used instead.

    // NOTE: this is partly the same as FieldAccess -> can this be unified?

    // NOTE: can be (should be) extended to provide type parameters

    SymTypeExpression exprType = normalize(typeOf(node));

    // static field
    if (exprType.getSourceInfo().getSourceSymbol().isPresent()
        && OOSymbolsMill.typeDispatcher().isOOSymbolsField(exprType.getSourceInfo().getSourceSymbol().get())
        && OOSymbolsMill.typeDispatcher().asOOSymbolsField(exprType.getSourceInfo().getSourceSymbol().get()).isIsStatic()) {
      getPrinter().print(OOSymbolsMill.typeDispatcher().asOOSymbolsField(exprType.getSourceInfo().getSourceSymbol().get()).getFullName());
    }

    // function references
    else if (exprType.isFunctionType() && exprType.asFunctionType().hasSymbol()) {
      SymTypeOfFunction funcType = exprType.asFunctionType();
      String funcName = funcType.getSymbol().getName();

      getPrinter().print("((");
      getPrinter().print(getJavaTypePrint(exprType));
      getPrinter().print(") ");
      if (funcType.getSymbol() instanceof MethodSymbol methodSym) {
        if (!methodSym.getSpannedScope().isPresentSpanningSymbol()) {
          Log.error("0xFD227 internal error: "
                  + "method symbol has no enclosing (OO)type: "
                  + methodSym.getFullName(),
              methodSym.getSourcePosition()
          );
          return;
        }
        IScopeSpanningSymbol owner =
            methodSym.getEnclosingScope().getSpanningSymbol();
        if (methodSym.isIsConstructor()) {
          getPrinter().print(owner.getFullName());
          getPrinter().print("::new");
        }
        else if (methodSym.isIsStatic()) {
          getPrinter().print(owner.getFullName());
          getPrinter().print("::");
          getPrinter().print(funcName);
        }
        else {
          // m() non-static can only be of this
          getPrinter().print("this");
          getPrinter().print("::");
          getPrinter().print(funcName);
        }
      }
      else {
        String javaFuncFullName;
        String funcFullName = funcType.getSymbol().getFullName();
        // rough temporary estimate how to handle these
        if (funcFullName.contains(".")) {
          javaFuncFullName =
              funcName.substring(0, funcFullName.lastIndexOf("."))
                  + "::"
                  + funcName.substring(funcFullName.lastIndexOf(".") + 1);
        }
        else {
          IScope scope = funcType.getSymbol().getEnclosingScope();
          while (true) {
            if (scope instanceof IArtifactScope) {
              // missing intersection types
              javaFuncFullName = scope.getName() + "::" + funcFullName;
              break;
            }
            scope = scope.getEnclosingScope();
            Preconditions.checkNotNull(scope);
          }
        }
        getPrinter().print(javaFuncFullName);
      }
      getPrinter().print(")");
    }
    // variables
    else {
      getPrinter().print(node.getName());
    }
  }

  @Override
  public void traverse(ASTArguments node) {
    // arguments are context dependent,
    // thus, they cannot be printed in a general way.
    // This has to be done by the expression that has the arguments.
    Log.error("0xFD239 internal error: "
            + " CodeGenPrinter misconfigured/not fully implemented",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
    );
  }

  @Override
  public void traverse(ASTLiteralExpression node) {
    // explicitly pass through
    node.getLiteral().accept(getTraverser());
  }

}
