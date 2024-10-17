package de.monticore.expressions.uglyexpressions.types3;

import de.monticore.expressions.uglyexpressions._ast.ASTArrayCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayDimensionByExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayDimensionSpecifier;
import de.monticore.expressions.uglyexpressions._ast.ASTClassCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTCreatorExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.types3.util.TypeContextCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class UglyExpressionsTypeVisitor
    extends AbstractTypeVisitor
    implements UglyExpressionsVisitor2 {

  protected OOWithinTypeBasicSymbolsResolver oOWithinTypeResolver;

  protected TypeContextCalculator typeCtxCalc;

  protected UglyExpressionsTypeVisitor(
      OOWithinTypeBasicSymbolsResolver oOWithinTypeResolver,
      TypeContextCalculator typeCtxCalc) {
    this.oOWithinTypeResolver = oOWithinTypeResolver;
    this.typeCtxCalc = typeCtxCalc;
  }

  public UglyExpressionsTypeVisitor() {
    // default values
    this(
        new OOWithinTypeBasicSymbolsResolver(),
        new TypeContextCalculator()
    );
  }

  public void setOOWithinTypeBasicSymbolsResolver(
      OOWithinTypeBasicSymbolsResolver withinTypeResolver) {
    this.oOWithinTypeResolver = withinTypeResolver;
  }

  public void setTypeContextCalculator(TypeContextCalculator typeCtxCalc) {
    this.typeCtxCalc = typeCtxCalc;
  }

  protected OOWithinTypeBasicSymbolsResolver getOOWithinTypeResolver() {
    return oOWithinTypeResolver;
  }

  protected TypeContextCalculator getTypeCtxCalc() {
    return typeCtxCalc;
  }

  @Override
  public void endVisit(ASTTypeCastExpression expr) {
    SymTypeExpression typeResult = getType4Ast().getPartialTypeOfTypeId(expr.getMCType());
    SymTypeExpression exprResult = getType4Ast().getPartialTypeOfExpr(expr.getExpression());

    SymTypeExpression result;
    if (typeResult.isObscureType() || exprResult.isObscureType()) {
      // if any inner obscure then error already logged
      result = createObscureType();
    }
    else if (SymTypeRelations.isNumericType(typeResult) && SymTypeRelations.isNumericType(exprResult)) {
      // allow to cast numbers down, e.g., (int) 5.0 or (byte) 5
      result = typeResult;
    }
    else if (
        SymTypeRelations.isSubTypeOf(typeResult, exprResult) || // downcast
            SymTypeRelations.isSubTypeOf(exprResult, typeResult) // upcast
    ) {
      // check typecast is possible
      result = typeResult;
    }
    else {
      Log.error("0xFD204 The expression of type '"
              + exprResult.printFullName()
              + "' cannot be cast to the given type '"
              + typeResult.printFullName()
              + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTInstanceofExpression expr) {
    SymTypeExpression exprResult = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression typeResult = getType4Ast().getPartialTypeOfTypeId(expr.getMCType());

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
        Log.error("0xFD203 expression of type "
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

  @Override
  public void endVisit(ASTCreatorExpression expr) {
    if (getType4Ast().hasPartialTypeOfExpression(expr)) {
      return;
    }
    SymTypeExpression createdType = getCreatorType(expr.getCreator());
    getType4Ast().setTypeOfExpression(expr, createdType);
  }

  /**
   * This needs to be extended, whenever a new Creator is added.
   * <p>
   * Returns the type the CreatorExpressions is to have.
   * This is calculated here,
   * as a Creator is neither an ASTExpression nor an ASTMCType,
   * thus we do not need to modify Type4Ast.
   */
  protected SymTypeExpression getCreatorType(ASTCreator creator) {
    //todo use TypeDispatcher as soon as it is fixed
    SymTypeExpression creatorType;
    if (creator instanceof ASTClassCreator) {
      creatorType = getClassCreatorType((ASTClassCreator) creator);
    }
    else if (creator instanceof ASTArrayCreator) {
      creatorType = getArrayCreatorType((ASTArrayCreator) creator);
    }
    else {
      // hint: if the other ASTCreators have been added in another class,
      // it becomes necessary to pass informations to and from them in the AST,
      // e.g., by adding them to Type4AST, etc.
      // However, this would make Type4AST be reliant on UglyExpressions
      // -> in this case, use a simple ASTNode interface,
      // which, however, provides less type safety.
      Log.error("0xFD550 internal error:"
              + "new Creators have to be added to the type check",
          creator.get_SourcePositionStart(),
          creator.get_SourcePositionEnd()
      );
      creatorType = createObscureType();
    }
    return creatorType;
  }

  protected SymTypeExpression getClassCreatorType(ASTClassCreator creator) {
    SymTypeExpression result;
    SymTypeExpression typeToCreate =
        getType4Ast().getPartialTypeOfTypeId(creator.getMCType());
    List<SymTypeExpression> argumentTypes =
        creator.getArguments().streamExpressions()
            .map(arg -> getType4Ast().getPartialTypeOfExpr(arg))
            .collect(Collectors.toList());
    List<SymTypeOfFunction> constructors = getConstructors(creator);
    if (constructors.isEmpty()) {
      result = createObscureType();
    }
    else {
      List<SymTypeOfFunction> applicableConstructors = constructors.stream()
          .filter(c -> FunctionRelations.canBeCalledWith(c, argumentTypes))
          .collect(Collectors.toList());
      Optional<SymTypeOfFunction> mostSpecificConstructor = FunctionRelations
          .getMostSpecificFunctionOrLogError(applicableConstructors);
      if (mostSpecificConstructor.isPresent()) {
        result = mostSpecificConstructor.get().getType();
      }
      else {
        Log.error("0xFD553 could not find constructor to call for "
                + typeToCreate.printFullName()
                + " given arguments:"
                + System.lineSeparator()
                + argumentTypes.stream()
                .map(SymTypeExpression::printFullName)
                .collect(Collectors.joining(System.lineSeparator()))
                + System.lineSeparator()
                + "constructors found:"
                + System.lineSeparator()
                + constructors.stream()
                .map(SymTypeExpression::printFullName)
                .collect(Collectors.joining(System.lineSeparator()))
                + System.lineSeparator(),
            creator.get_SourcePositionStart(),
            creator.get_SourcePositionEnd()
        );
        result = createObscureType();
      }
    }
    return result;
  }

  protected SymTypeExpression getArrayCreatorType(ASTArrayCreator creator) {
    SymTypeExpression result;
    SymTypeExpression innerType =
        getType4Ast().getPartialTypeOfTypeId(creator.getMCType());
    Optional<Integer> dimensions =
        getArrayDimensionSpecifierSize(creator.getArrayDimensionSpecifier());
    if (innerType.isObscureType() || dimensions.isEmpty()) {
      result = createObscureType();
    }
    else {
      // we assume that we can create an array of nearly any type
      // note: these should not even be possible as MCTypes:
      if (innerType.isNullType()
          || innerType.isVoidType()
          || innerType.isWildcard()
      ) {
        Log.error("0xFD556 unexpected type to create a new array of: "
                + innerType.printFullName(),
            creator.get_SourcePositionStart(),
            creator.get_SourcePositionEnd()
        );
        result = createObscureType();
      }
      else {
        result = SymTypeExpressionFactory
            .createTypeArray(innerType, dimensions.get());
      }
    }
    return result;
  }

  // Helper

  /**
   * only returns the dimensionality if the expression types are correct
   * if the expressions are not correct, Log::error will be used
   */
  protected Optional<Integer> getArrayDimensionSpecifierSize(
      ASTArrayDimensionSpecifier dimSpec) {
    Optional<Integer> dimensions;
    // todo use typedispatcher as soon as it works
    if (dimSpec instanceof ASTArrayDimensionByExpression) {
      ASTArrayDimensionByExpression dims = (ASTArrayDimensionByExpression) dimSpec;
      List<SymTypeExpression> expressions = dims.getExpressionList().stream()
          .map(e -> getType4Ast().getPartialTypeOfExpr(e))
          .collect(Collectors.toList());
      boolean expressionsOK = true;
      if (expressions.stream().anyMatch(SymTypeExpression::isObscureType)) {
        expressionsOK = false;
      }
      else {
        for (SymTypeExpression expr : expressions) {
          if (!SymTypeRelations.isIntegralType(expr)) {
            Log.error("0xFD556 to specify the size of an array "
                    + "integral values are expected"
                    + ", but instead got "
                    + expr.printFullName(),
                dims.get_SourcePositionStart(),
                dims.get_SourcePositionEnd()
            );
            expressionsOK = false;
          }
        }
      }
      if (expressionsOK) {
        dimensions = Optional.of(dims.sizeExpressions() + dims.sizeDim());
      }
      else {
        dimensions = Optional.empty();
      }
    }
    else {
      Log.error("0xFD555 internal error:"
              + "new ArrayDimensionSpecifier have to be added to the type check",
          dimSpec.get_SourcePositionStart(),
          dimSpec.get_SourcePositionEnd()
      );
      dimensions = Optional.empty();
    }
    return dimensions;
  }

  /**
   * Constructors of the inner type, NOT filtered by the argument expressions.
   * Important: The {@link ASTClassCreator#getMCType()} has to have
   * its type stored in the map already.
   *
   * @return if empty, an error will have been logged
   */
  protected List<SymTypeOfFunction> getConstructors(
      ASTClassCreator creator
  ) {
    List<SymTypeOfFunction> constructors;
    SymTypeExpression typeToCreate =
        getType4Ast().getPartialTypeOfTypeId(creator.getMCType());
    if (typeToCreate.isObscureType()) {
      constructors = Collections.emptyList();
    }
    else if (typeToCreate.isObjectType() || typeToCreate.isGenericType()) {
      // search for a constructor
      // Hint: to support default constructors,
      // a corresponding OOWithinTypeBasicSymbolsResolver is required
      AccessModifier modifier = getTypeCtxCalc().getAccessModifier(
          typeToCreate.getTypeInfo(), creator.getEnclosingScope());
      constructors = getOOWithinTypeResolver()
          .resolveConstructors(typeToCreate, modifier, c -> true);
      if (constructors.isEmpty()) {
        Log.error("0xFD557 could not find constructors for "
                + typeToCreate.printFullName(),
            creator.get_SourcePositionStart(),
            creator.get_SourcePositionEnd()
        );
      }
    }
    else {
      Log.error("0xFD552 unexpected type "
              + typeToCreate.printFullName()
              + " for \"new\"",
          creator.getMCType().get_SourcePositionStart(),
          creator.getMCType().get_SourcePositionEnd()
      );
      constructors = Collections.emptyList();
    }
    return constructors;
  }
}
