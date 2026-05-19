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
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.TypeContextCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaTypeConstructor;
import static de.monticore.symbols.oosymbols.types3.OOSymbolsSymTypeRelations.isConstructor;
import static de.monticore.symbols.oosymbols.types3.OOSymbolsSymTypeRelations.isMethod;
import static de.monticore.types.check.SymTypeExpressionFactory.createDeclaredType;
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

    SymTypeExpression exprType = normalize(typeOf(node));
    Preconditions.checkState(exprType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol exprSourceSym = exprType.getSourceInfo().getSourceSymbol().get();

    boolean isOOElement;
    boolean isRelativToObject;
    if (exprSourceSym instanceof FieldSymbol) {
      isOOElement = true;
      isRelativToObject = !((FieldSymbol) exprSourceSym).isIsStatic();
    }
    else if (exprSourceSym instanceof MethodSymbol) {
      isOOElement = true;
      isRelativToObject = !((MethodSymbol) exprSourceSym).isIsStatic();
    }
    else {
      isOOElement = false;
      isRelativToObject = false;
    }

    // non-static field/method -> print object
    if (isRelativToObject) {
      startParentheses();
      node.getExpression().accept(getTraverser());
      endParentheses();
    }
    // static field/method -> print Type
    else if (isOOElement) {
      OOTypeSymbol owningSymbol = (OOTypeSymbol) TypeContextCalculator
          .getEnclosingType(exprSourceSym.getEnclosingScope()).get();
      SymTypeExpression declaredType = createDeclaredType(owningSymbol);
      getPrinter().print(convert2JavaTypeConstructor(declaredType));
    }
    // pack.age....
    else {
      String fullName = exprSourceSym.getFullName();
      String packageName = fullName.substring(0, fullName.lastIndexOf('.'));
      getPrinter().print(packageName);
    }

    if (exprSourceSym instanceof VariableSymbol) {
      getPrinter().print(".");
      getPrinter().print(node.getName());
    }
    else if (isConstructor(exprType)) {
      getPrinter().print("::new");
    }
    else if (isMethod(exprType)) {
      getPrinter().print("::");
      getPrinter().print(node.getName());
    }
    // unknown case
    else {
      Log.error("0xFD228 internal error: "
              + "unimplemented case for field access expression. Type of expression: "
              + exprSourceSym.getFullName(),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}
