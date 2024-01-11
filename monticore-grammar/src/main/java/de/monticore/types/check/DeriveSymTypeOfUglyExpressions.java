/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayDimensionByExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTClassCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsHandler;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsTraverser;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types.check.TypeCheck.compatible;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in JavaClassExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfUglyExpressions
    extends AbstractDeriveFromExpression
    implements UglyExpressionsVisitor2, UglyExpressionsHandler {

  protected UglyExpressionsTraverser traverser;

  protected ISynthesize synthesize;

  @Deprecated
  public DeriveSymTypeOfUglyExpressions() {
    // default behaviour, as this class had no synthezises beforehand
    synthesize = new FullSynthesizeFromMCFullGenericTypes();
  }

  public DeriveSymTypeOfUglyExpressions(ISynthesize synthesize) {
    this.synthesize = synthesize;
  }

  @Override
  public void setTraverser(UglyExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public UglyExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void traverse(ASTInstanceofExpression node) {
    SymTypeExpression expressionResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if (getTypeCheckResult().isType()) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0267 the expression at source position " + node.getExpression().get_SourcePositionStart() + " cannot be a type");
      return;
    }

    SymTypeExpression typeResult = SymTypeExpressionFactory.createObscureType();

    //calculate right type: type that the expression should be an instance of
    deprecated_traverse(node.getMCType());
    if (getTypeCheckResult().isPresentResult()) {
      if (!getTypeCheckResult().isType()) {
        if (!getTypeCheckResult().getResult().isObscureType()) {
          getTypeCheckResult().reset();
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          Log.error("0xA0269 the expression at source position " + node.getMCType().get_SourcePositionStart() + " must be a type");
          return;
        }
      }
      else {
        typeResult = getTypeCheckResult().getResult();
      }
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      logError("0xA0270", node.getExpression().get_SourcePositionStart());
      return;
    }

    if (!expressionResult.isObscureType() && !typeResult.isObscureType()) {
      //the method was not finished yet (either with Log.error or return) -> both types are present and thus the result is boolean
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createPrimitive("boolean");

      getTypeCheckResult().setResult(wholeResult);
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void traverse(ASTTypeCastExpression node) {
    //innerResult is the SymTypeExpression of the type that will be casted into another type
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if (getTypeCheckResult().isType()) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0262 the expression at source position " + node.getExpression().get_SourcePositionStart() + " cannot be a type");
      return;
    }
    //castResult is the SymTypeExpression of the type the innerResult will be casted to
    SymTypeExpression castResult;

    //castResult is the type in the brackets -> (ArrayList) list

    deprecated_traverse(node.getMCType());
    if (getTypeCheckResult().isPresentResult()) {
      castResult = getTypeCheckResult().getResult();
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0265 the type at source position " + node.getMCType().get_SourcePositionStart() + " cannot be calculated");
      return;
    }

    if (!innerResult.isObscureType() && !castResult.isObscureType()) {
      //wholeResult will be the result of the whole expression
      SymTypeExpression wholeResult = calculateTypeCastExpression(node, castResult, innerResult);

      storeResultOrLogError(wholeResult, node, "0xA0266");
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateTypeCastExpression(ASTTypeCastExpression node, SymTypeExpression castResult, SymTypeExpression innerResult) {
    if (compatible(castResult, innerResult) || compatible(innerResult, castResult)) {
      return castResult;
    }
    return SymTypeExpressionFactory.createObscureType();
  }

  @Override
  public void traverse(ASTClassCreator creator) {
    SymTypeExpression extType;
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    deprecated_traverse(creator.getMCType());
    if (getTypeCheckResult().isPresentResult()) {
      extType = getTypeCheckResult().getResult();
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(wholeResult);
      logError("0xA1311", creator.getMCType().get_SourcePositionStart());
      return;
    }

    if (!extType.isObscureType()) {

      if (!extType.isPrimitive()) {
        //see if there is a constructor fitting for the arguments
        List<FunctionSymbol> constructors = extType.getMethodList(extType.getTypeInfo().getName(), false, AccessModifier.ALL_INCLUSION);
        if (!constructors.isEmpty()) {
          if (testForCorrectArguments(constructors, creator.getArguments())) {
            wholeResult = extType;
          }
        }
        else if (creator.getArguments().isEmptyExpressions()) {
          //no constructor in this class -> default constructor without arguments, only possible if arguments in creator are empty
          wholeResult = extType;
        }
      }

      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(wholeResult);
      if (wholeResult.isObscureType()) {
        logError("0xA1312", creator.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void traverse(ASTArrayCreator creator) {
    SymTypeExpression extTypeResult;
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();

    deprecated_traverse(creator.getMCType());
    if (getTypeCheckResult().isPresentResult()) {
      extTypeResult = getTypeCheckResult().getResult();
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(wholeResult);
      logError("0xA0314", creator.getMCType().get_SourcePositionStart());
      return;
    }

    if (!extTypeResult.isObscureType()) {
      //the definition of the Arrays are based on the assumption that MCType is not an array
      if (!extTypeResult.isArrayType()) {
        if (creator.getArrayDimensionSpecifier() instanceof ASTArrayDimensionByExpression) {
          ASTArrayDimensionByExpression arrayInitializer = (ASTArrayDimensionByExpression) creator.getArrayDimensionSpecifier();
          int dim = arrayInitializer.getDimList().size() + arrayInitializer.getExpressionList().size();
          //teste dass alle Expressions integer-zahl sind
          for (ASTExpression expr : arrayInitializer.getExpressionList()) {
            expr.accept(getTraverser());
            if (getTypeCheckResult().isPresentResult()) {
              SymTypeExpression result = getTypeCheckResult().getResult();
              if (result.isPrimitive()) {
                if (!((SymTypePrimitive) result).isIntegralType()) {
                  getTypeCheckResult().reset();
                  getTypeCheckResult().setResult(wholeResult);
                  logError("0xA0315", expr.get_SourcePositionStart());
                  return;
                }
              }
              else {
                getTypeCheckResult().reset();
                getTypeCheckResult().setResult(wholeResult);
                logError("0xA0316", expr.get_SourcePositionStart());
                return;
              }
            }
            else {
              getTypeCheckResult().reset();
              getTypeCheckResult().setResult(wholeResult);
              logError("0xA0317", expr.get_SourcePositionStart());
              return;
            }
          }
          wholeResult = SymTypeExpressionFactory.createTypeArray(extTypeResult.getTypeInfo(), dim, extTypeResult);
        }
      }

      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(wholeResult);
      if (wholeResult.isObscureType()) {
        logError("0xA0318", creator.get_SourcePositionStart());
      }
    }
  }

  protected List<SymTypeExpression> calculateCorrectArguments(ASTArguments args) {
    List<SymTypeExpression> argList = Lists.newArrayList();
    for (int i = 0; i < args.getExpressionList().size(); i++) {
      args.getExpression(i).accept(getTraverser());
      if (getTypeCheckResult().isPresentResult()) {
        argList.add(getTypeCheckResult().getResult());
      }
      else {
        getTypeCheckResult().reset();
        argList.add(SymTypeExpressionFactory.createObscureType());
        logError("0xA0313", args.getExpressionList().get(i).get_SourcePositionStart());
      }
    }
    return argList;
  }

  protected boolean testForCorrectArguments(List<FunctionSymbol> constructors, ASTArguments arguments) {
    List<SymTypeExpression> symTypeOfArguments = calculateCorrectArguments(arguments);
    outer:
    for (FunctionSymbol constructor : constructors) {
      if (constructor.getParameterList().size() == symTypeOfArguments.size()) {
        //get the types of the constructor arguments
        List<SymTypeExpression> constructorArguments = constructor.getParameterList().stream().map(VariableSymbol::getType).collect(Collectors.toList());
        for (int i = 0; i < constructorArguments.size(); i++) {
          if (!compatible(constructorArguments.get(i), symTypeOfArguments.get(i))) {
            //wrong constructor, argument is not compatible to constructor definition
            continue outer;
          }
        }
        //if this is reached, then the arguments match a constructor's arguments -> return true
        return true;
      }
    }
    return false;
  }

  // Warning: deprecated and wrong behavior
  // (it does not set whether it's an expression or type identifier)
  // this is fixed in typecheck 3
  // and exists only here like this to not change the behaviour of typecheck1

  @Deprecated
  public void deprecated_traverse(ASTMCType type) {
    SymTypeExpression wholeResult = null;
    TypeCheckResult result = synthesize.synthesizeType(type);
    if (result.isPresentResult()) {
      wholeResult = result.getResult();
    }
    if (wholeResult != null) {
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }
    else {
      getTypeCheckResult().reset();
    }
  }

  @Deprecated
  public void deprecated_traverse(ASTMCReturnType returnType) {
    SymTypeExpression wholeResult = null;
    if (returnType.isPresentMCVoidType()) {
      wholeResult = SymTypeExpressionFactory.createTypeVoid();
    }
    else if (returnType.isPresentMCType()) {
      TypeCheckResult res = synthesize.synthesizeType(returnType);
      if (res.isPresentResult()) {
        wholeResult = res.getResult();
      }
    }
    if (wholeResult != null) {
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }
    else {
      getTypeCheckResult().reset();
    }
  }

  @Deprecated
  public void deprecated_traverse(ASTMCTypeArgument typeArgument) {
    SymTypeExpression wholeResult = null;
    if (typeArgument.getMCTypeOpt().isPresent()) {
      TypeCheckResult res = synthesize.synthesizeType(typeArgument.getMCTypeOpt().get());
      if (res.isPresentResult()) {
        wholeResult = res.getResult();
      }
    }
    if (wholeResult != null) {
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }
    else {
      getTypeCheckResult().reset();
    }
  }

}
