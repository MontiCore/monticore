// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.javaclassexpressions.types3;

import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTInstanceofPatternExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTPattern;
import de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTPrimarySuperExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryThisExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTThisExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTTypePattern;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor2;
import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class JavaClassExpressionsTypeVisitor extends AbstractTypeVisitor
    implements JavaClassExpressionsVisitor2 {

  @Override
  public void endVisit(ASTPrimaryThisExpression primThisExpr) {
  }

  @Override
  public void endVisit(ASTThisExpression thisExpr) {
  }

  @Override
  public void endVisit(ASTPrimarySuperExpression primSuperExpr) {
  }

  @Override
  public void endVisit(ASTSuperExpression superExpr) {
  }

  @Override
  public void endVisit(ASTPrimaryGenericInvocationExpression primGenInvocExpr) {
  }

  @Override
  public void endVisit(ASTGenericInvocationExpression genInvocExpr) {
  }

  /**
   * s. JLS 21 15.8.2
   */
  @Override
  public void endVisit(ASTClassExpression classExpr) {
    SymTypeExpression result;
    Optional<SymTypeExpression> innerResult;
    SymTypeExpression innerType = getType4Ast()
        .getPartialTypeOfTypeId(classExpr.getMCReturnType());
    Optional<TypeSymbol> classSym = resolveTypeOrLogError("java.lang.Class");
    if (innerType.isObscureType()) {
      innerResult = Optional.empty();
    }
    else if (innerType.isVoidType()) {
      innerResult = resolveTypeOrLogError("java.lang.Void")
          .map(SymTypeExpressionFactory::createTypeObject);
    }
    else if (innerType.isPrimitive()) {
      innerResult = Optional.of(SymTypeRelations.box(innerType));
    }
    // hint: we require type parameters, thus it is
    // List<int>.class, not List.class
    else if (innerType.isObjectType() || innerType.isGenericType()) {
      innerResult = Optional.of(innerType);
    }
    // hint: we have simple arrays that are not objects,
    // as such, we do not allow, e.g., int[].class
    else {
      Log.error("0xFD576 '.class' cannot be used with the type "
              + innerType,
          classExpr.get_SourcePositionStart(),
          classExpr.get_SourcePositionEnd()
      );
      innerResult = Optional.empty();
    }
    if (classSym.isPresent() && innerResult.isPresent()) {
      result = createGenerics(classSym.get(), innerResult.get());
    }
    else {
      result = createObscureType();
    }
    getType4Ast().setTypeOfExpression(classExpr, result);
  }

  /**
   * s. {@link de.monticore.expressions.uglyexpressions.types3.UglyExpressionsTypeVisitor#endVisit(ASTInstanceofExpression)}
   */
  @Override
  public void endVisit(ASTInstanceofPatternExpression expr) {
    SymTypeExpression exprResult = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression typeResult = getTypeOfPattern(expr.getPattern());

    SymTypeExpression result;
    if (exprResult.isObscureType() || typeResult.isObscureType()) {
      result = createObscureType();
    }
    else {
      if (SymTypeRelations.isSubTypeOf(typeResult, exprResult)) {
        result = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
      }
      else if (SymTypeRelations.isSubTypeOf(exprResult, typeResult)) {
        Log.trace(expr.get_SourcePositionStart().toString() + ": "
                + "Found redundant instanceof-expression, "
                + "expression of type " + exprResult.printFullName()
                + " is always an instance of " + typeResult.printFullName(),
            LOG_NAME
        );
        result = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
      }
      else {
        Log.error("0xFD571 expression of type "
                + exprResult.printFullName()
                + " cannot be an instance of type "
                + typeResult.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        result = createObscureType();
      }
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Helper

  protected Optional<TypeSymbol> resolveTypeOrLogError(String name) {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    Optional<TypeSymbol> typeSymbol = gs.resolveType(name);
    if (typeSymbol.isEmpty()) {
      Log.error("0xFD578 Could not resolve type " + name);
    }
    return typeSymbol;
  }

  // not expecting an extension here anytime soon, thus simple
  protected SymTypeExpression getTypeOfPattern(ASTPattern pattern) {
    if (JavaClassExpressionsMill.typeDispatcher()
        .isJavaClassExpressionsASTTypePattern(pattern)
    ) {
      ASTTypePattern typePattern = JavaClassExpressionsMill.typeDispatcher()
          .asJavaClassExpressionsASTTypePattern(pattern);
      ASTMCType mcType = typePattern.getLocalVariableDeclaration().getMCType();
      return getType4Ast().getPartialTypeOfTypeId(mcType);
    }
    else {
      Log.error("0xFD573 internal error:"
              + " getTypeOfPattern() needs to be replaced",
          pattern.get_SourcePositionStart()
      );
      return createObscureType();
    }
  }

}
