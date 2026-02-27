/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.CodeGenOperationPrinter;
import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getAsJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.printJavaType;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

public class CommonExpressionsJavaGenVisitor extends AbstractJavaGenVisitor
    implements CommonExpressionsHandler {

  // Traverser
  protected CommonExpressionsTraverser traverser;

  public CommonExpressionsJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  // CodeGen

  // Prefix

  @Override
  public void handle(ASTPlusPrefixExpression expr) {
    // only does numeric promotion, so skipping "+" here
    // s. JLS 21 15.15.3
    SymTypeExpression exprType = normalize(typeOf(expr));
    SymTypeExpression innerType = normalize(typeOf(expr.getExpression()));
    printConverted(
        getPrinter(), exprType, innerType,
        p -> expr.getExpression().accept(getTraverser())
    );
  }

  @Override
  public void handle(ASTMinusPrefixExpression expr) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    SymTypeExpression innerType = normalize(typeOf(expr.getExpression()));
    startParentheses();
    getPrinter().print("-");
    if (ExpressionsBasisMill.typeDispatcher().isExpressionsBasisASTLiteralExpression(expr.getExpression())) {
      expr.getExpression().accept(getTraverser());
    }
    else {
      startParentheses();
      printConverted(
          getPrinter(), exprType, innerType,
          p -> expr.getExpression().accept(getTraverser())
      );
      endParentheses();
    }
    endParentheses();
  }

  // Arithmetic

  @Override
  public void handle(ASTPlusExpression expr) {
    SymTypeExpression typeLeft = normalize(typeOf(expr.getLeft()));
    SymTypeExpression typeRight = normalize(typeOf(expr.getRight()));
    CodeGenOperationPrinter.printPlus(
        getPrinter(), normalize(typeOf(expr)), typeLeft, typeRight,
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser()));
  }

  @Override
  public void handle(ASTMultExpression expr) {
    SymTypeExpression typeLeft = normalize(typeOf(expr.getLeft()));
    SymTypeExpression typeRight = normalize(typeOf(expr.getRight()));
    CodeGenOperationPrinter.printMultiply(
        getPrinter(), normalize(typeOf(expr)), typeLeft, typeRight,
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser()));
  }

  @Override
  public void handle(ASTDivideExpression expr) {
    SymTypeExpression typeLeft = normalize(typeOf(expr.getLeft()));
    SymTypeExpression typeRight = normalize(typeOf(expr.getRight()));
    CodeGenOperationPrinter.printDivide(
        getPrinter(), normalize(typeOf(expr)), typeLeft, typeRight,
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser()));
  }

  @Override
  public void handle(ASTMinusExpression expr) {
    SymTypeExpression typeLeft = normalize(typeOf(expr.getLeft()));
    SymTypeExpression typeRight = normalize(typeOf(expr.getRight()));
    CodeGenOperationPrinter.printMinus(
        getPrinter(), normalize(typeOf(expr)), typeLeft, typeRight,
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser()));
  }

  @Override
  public void handle(ASTModuloExpression expr) {
    SymTypeExpression typeLeft = normalize(typeOf(expr.getLeft()));
    SymTypeExpression typeRight = normalize(typeOf(expr.getRight()));
    CodeGenOperationPrinter.printModulo(
        getPrinter(), normalize(typeOf(expr)), typeLeft, typeRight,
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser()));
  }

  // Numeric Comparison

  @Override
  public void handle(ASTEqualsExpression expr) {
    CodeGenOperationPrinter.printEquals(
        getPrinter(), normalize(typeOf(expr)),
        normalize(typeOf(expr.getLeft())), normalize(typeOf(expr.getRight())),
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser())
    );
  }

  @Override
  public void handle(ASTNotEqualsExpression expr) {
    CodeGenOperationPrinter.printNotEquals(
        getPrinter(), normalize(typeOf(expr)),
        normalize(typeOf(expr.getLeft())), normalize(typeOf(expr.getRight())),
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser())
    );
  }

  @Override
  public void handle(ASTGreaterThanExpression expr) {
    CodeGenOperationPrinter.printGreaterThan(
        getPrinter(), normalize(typeOf(expr)),
        normalize(typeOf(expr.getLeft())), normalize(typeOf(expr.getRight())),
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser())
    );
  }

  @Override
  public void handle(ASTLessThanExpression expr) {
    CodeGenOperationPrinter.printLessThan(
        getPrinter(), normalize(typeOf(expr)),
        normalize(typeOf(expr.getLeft())), normalize(typeOf(expr.getRight())),
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser())
    );
  }

  @Override
  public void handle(ASTGreaterEqualExpression expr) {
    CodeGenOperationPrinter.printGreaterEqual(
        getPrinter(), normalize(typeOf(expr)),
        normalize(typeOf(expr.getLeft())), normalize(typeOf(expr.getRight())),
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser())
    );
  }

  @Override
  public void handle(ASTLessEqualExpression expr) {
    CodeGenOperationPrinter.printLessEqual(
        getPrinter(), normalize(typeOf(expr)),
        normalize(typeOf(expr.getLeft())), normalize(typeOf(expr.getRight())),
        p -> expr.getLeft().accept(getTraverser()),
        p -> expr.getRight().accept(getTraverser())
    );
  }

  // Conditional

  @Override
  public void handle(ASTBooleanAndOpExpression expr) {
    startParentheses();
    expr.getLeft().accept(getTraverser());
    endParentheses();
    getPrinter().print("&&");
    startParentheses();
    expr.getRight().accept(getTraverser());
    endParentheses();
  }

  @Override
  public void handle(ASTBooleanOrOpExpression expr) {
    startParentheses();
    expr.getLeft().accept(getTraverser());
    endParentheses();
    getPrinter().print("||");
    startParentheses();
    expr.getRight().accept(getTraverser());
    endParentheses();
  }

  @Override
  public void handle(ASTBooleanNotExpression expr) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    SymTypeExpression innerType = normalize(typeOf(expr.getExpression()));
    getPrinter().print("~");
    startParentheses();
    printConverted(
        getPrinter(), exprType, innerType,
        p -> expr.getExpression().accept(getTraverser())
    );
    endParentheses();
  }

  @Override
  public void handle(ASTLogicalNotExpression expr) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    SymTypeExpression innerType = normalize(typeOf(expr.getExpression()));
    getPrinter().print("!");
    startParentheses();
    printConverted(
        getPrinter(), exprType, innerType,
        p -> expr.getExpression().accept(getTraverser())
    );
    endParentheses();
  }

  @Override
  public void handle(ASTConditionalExpression expr) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    SymTypeExpression trueType = normalize(typeOf(expr.getTrueExpression()));
    SymTypeExpression falseType = normalize(typeOf(expr.getFalseExpression()));

    startParentheses();
    expr.getCondition().accept(getTraverser());
    endParentheses();
    getPrinter().print(" ? ");
    printConverted(
        getPrinter(), exprType, trueType,
        p -> expr.getTrueExpression().accept(getTraverser())
    );
    getPrinter().print(" : ");
    printConverted(
        getPrinter(), exprType, falseType,
        p -> expr.getFalseExpression().accept(getTraverser())
    );
  }

  @Override
  public void handle(ASTBracketExpression expr) {
    // note: These Brackets are never needed in the generated code,
    // as otherwise, there exist an AST
    // for which the generator creates invalid code. E.g.:
    // A ASTMultExpression as a direct subexpression of an ASTPlusExpression
    expr.getExpression().accept(getTraverser());
  }

  // Access expressions
  @Override
  public void handle(ASTArrayAccessExpression node) {
    node.getExpression().accept(getTraverser());

    if (TypeCheck3.typeOf(node.getExpression()).isTupleType()) {
      // Case tuples
      getPrinter().print(".get");
      node.getIndexExpression().accept(getTraverser());
      getPrinter().print("()");
    }
    else {
      getPrinter().print("[");
      node.getIndexExpression().accept(getTraverser());
      getPrinter().print("]");
    }
  }

  @Override
  public void handle(ASTCallExpression node) {
    // NOTE: this is only a temporary implementation,
    // as in the future, templates provided by the functions symbols
    // are to be used instead.

    Preconditions.checkNotNull(node);
    SymTypeExpression innerType = normalize(typeOf(node.getExpression()));

    // multiple function types?
    // Java has its limits due to type erasure;
    // we cannot distinguish them
    if (innerType.isUnionType()) {
      Log.error("0xFD224 Cannot generate code for call expression "
              + "with multiple function types due to Java type erasure."
              + " Please simplify the expression."
              + " Type: " + innerType.printFullName(),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
    else if (innerType.isFunctionType()) {
      SymTypeOfFunction funcType = innerType.asFunctionType();
      boolean didPrintSpecialCase;

      // peephole optimization, this is not (strictly) required
      if (funcType.hasSymbol()) {
        FunctionSymbol funcSym = funcType.getSymbol();
        if (funcSym instanceof MethodSymbol) {
          MethodSymbol methodSym = (MethodSymbol) funcSym;
          if (!methodSym.getSpannedScope().isPresentSpanningSymbol()) {
            Log.error("0xFD225 internal error: "
                    + "method symbol has no enclosing (OO)type: "
                    + methodSym.getFullName(),
                methodSym.getSourcePosition()
            );
            return;
          }
          IScopeSpanningSymbol owner =
              methodSym.getEnclosingScope().getSpanningSymbol();
          // Person.Person()
          if (methodSym.isIsConstructor()) {
            getPrinter().print("new ");
            getPrinter().print(owner.getFullName());
            didPrintSpecialCase = true;
          }
          // MyClass.method()
          else if (methodSym.isIsStatic()) {
            getPrinter().print(methodSym.getFullName());
            didPrintSpecialCase = true;
          }
          // something .method(arguments)
          else if (node.getExpression() instanceof ASTFieldAccessExpression) {
            ASTFieldAccessExpression fieldAccessExpr =
                (ASTFieldAccessExpression) node.getExpression();
            startParentheses();
            fieldAccessExpr.getExpression().accept(getTraverser());
            endParentheses();
            getPrinter().print(".");
            getPrinter().print(fieldAccessExpr.getName());
            didPrintSpecialCase = true;
          }
          else {
            didPrintSpecialCase = false;
          }
        }
        else {
          didPrintSpecialCase = false;
        }
      }
      else {
        didPrintSpecialCase = false;
      }
      if (!didPrintSpecialCase) {
        startParentheses();
        node.getExpression().accept(getTraverser());
        endParentheses();
        getPrinter().print(".apply");
      }

      // arguments
      List<SymTypeExpression> argTypes = node.getArguments().streamExpressions()
          .map(TypeCheck3::typeOf)
          .map(SymTypeRelations::normalize)
          .collect(Collectors.toList());
      startParentheses();
      for (int i = 0; i < node.getArguments().sizeExpressions(); i++) {
        ASTExpression argExpr = node.getArguments().getExpression(i);
        if (i > 0) {
          getPrinter().print(", ");
        }
        printConverted(getPrinter(),
            funcType.getArgumentType(i),
            argTypes.get(i),
            p -> argExpr.accept(getTraverser())
        );
      }
      endParentheses();
    }
    else {
      Log.error("0xFD226 internal error: "
              + "Cannot generate code for call expression "
              + "with non-function type. Type: " + innerType.printFullName(),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    // NOTE: this is only a temporary implementation,
    // as in the future, templates provided by the functions symbols
    // are to be used instead.

    // NOTE: can be (should be) extended to provide type parameters

    SymTypeExpression outerType = normalize(typeOf(node.getExpression()));

    Type4Ast type4Ast = _internal_hacky_trafo_workaround_dont_use_overwise();

    // function references
    if (outerType.isFunctionType() && outerType.asFunctionType().hasSymbol()) {
      SymTypeOfFunction funcType = outerType.asFunctionType();
      String funcName = funcType.getSymbol().getName();

      getPrinter().print("((");
      getPrinter().print(printJavaType(getAsJavaType(outerType)));
      getPrinter().print(") ");
      if (funcType.getSymbol() instanceof MethodSymbol) {
        MethodSymbol methodSym = (MethodSymbol) funcType.getSymbol();
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
          startParentheses();
          node.getExpression().accept(getTraverser());
          endParentheses();
          getPrinter().print("::");
          getPrinter().print(funcName);
        }

      }
      else {
        String funcFullName = funcType.getSymbol().getFullName();
        // assumed to always have a "."
        String javaFuncName =
            funcName.substring(0, funcFullName.lastIndexOf("."))
                + "::"
                + funcName.substring(funcFullName.lastIndexOf(".") + 1);
        getPrinter().print(javaFuncName);
      }
      getPrinter().print(")");
    }
    // static fields
    else if (type4Ast.hasPartialTypeOfTypeIdentifierForName(node.getExpression())) {
      SymTypeExpression innerType = type4Ast.getPartialTypeOfTypeIdForName(node.getExpression());
      getPrinter().print(printJavaType(getAsJavaType(innerType)));
      getPrinter().print(".");
      getPrinter().print(node.getName());
    }
    // non-static fields
    else if (
        outerType.getSourceInfo().getSourceSymbol()
            .filter(s -> s instanceof FieldSymbol)
            .isPresent()
    ) {
      startParentheses();
      node.getExpression().accept(getTraverser());
      endParentheses();
      getPrinter().print(".");
      getPrinter().print(node.getName());
    }
    // pack.age.variable
    else if (!type4Ast.hasPartialTypeOfExpression(node.getExpression())
        && outerType.getSourceInfo().getSourceSymbol().isPresent()
    ) {
      getPrinter().print(outerType.getSourceInfo().getSourceSymbol().get().getFullName());
    }
    // unknown case
    else {
      Log.error("0xFD228 internal error: "
              + "unimplemented case for field access expression. Type of expression: "
              + outerType.printFullName(),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  // hack

  /**
   * todo, temporary, replace as soon as Trafo exists
   */
  static Type4Ast _internal_hacky_trafo_workaround_dont_use_overwise() {
    class _internal_Hack extends MapBasedTypeCheck3 {
      private _internal_Hack() {
        super(null, null);
      }

      public Type4Ast getType4Ast() {
        return super.type4Ast;
      }
    }
    return new _internal_Hack().getType4Ast();
  }

}
