package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix;
import de.monticore.expressions.setexpressions._ast.ASTIsInExpression;
import de.monticore.expressions.setexpressions._ast.ASTSetInExpression;
import de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.isSubtypeOf;

/**
 * Visitor for SetExpressions
 */
public class DeriveSymTypeOfSetExpressions extends DeriveSymTypeOfExpression implements SetExpressionsVisitor {

  private SetExpressionsVisitor realThis;

  public DeriveSymTypeOfSetExpressions(){
    this.realThis = this;
  }

  @Override
  public void setRealThis(SetExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public SetExpressionsVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void traverse(ASTIsInExpression node) {
    SymTypeExpression elemResult = null;
    SymTypeExpression setResult = null;
    SymTypeExpression wholeResult = null;

    //element
    node.getElem().accept(realThis);
    if(lastResult.isPresentLast()){
      elemResult = lastResult.getLast();
    }else{
      Log.error("0xA0280 the result of the left expression of the isin cannot be calculated");
    }
    //set
    node.getSet().accept(realThis);
    if(lastResult.isPresentLast()){
      setResult = lastResult.getLast();
    }else{
      Log.error("0xA0281 the result of the right expression of the isin cannot be calculated");
    }
    List<String> collections = Lists.newArrayList("Collection","List","Set","java.util.Collection","java.util.List","java.util.Set");
    if(setResult.isGenericType()&&collections.contains(((SymTypeOfGenerics)setResult).printTypeWithoutTypeArgument())){
      SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
      if(unbox(elemResult.print()).equals(unbox(genericResult.getArgument(0).print()))||isSubtypeOf(elemResult,genericResult.getArgument(0))){
        wholeResult = SymTypeExpressionFactory.createTypeConstant("boolean");
      }
    }

    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0xA0282 the result of the IsInExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTSetInExpression node) {
    SymTypeExpression elemResult = null;
    SymTypeExpression setResult = null;
    SymTypeExpression wholeResult = null;

    //element
    node.getElem().accept(realThis);
    if(lastResult.isPresentLast()){
      elemResult = lastResult.getLast();
    }else{
      Log.error("0xA0283 the result of the left expression of the in cannot be calculated");
    }
    //set
    node.getSet().accept(realThis);
    if(lastResult.isPresentLast()){
      setResult = lastResult.getLast();
    }else{
      Log.error("0xA0284 the result of the right expression of the in cannot be calculated");
    }
    List<String> collections = Lists.newArrayList("Collection","List","Set","java.util.Collection","java.util.List","java.util.Set");
    if(setResult.isGenericType()&&collections.contains(((SymTypeOfGenerics)setResult).printTypeWithoutTypeArgument())){
      SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
      if(unbox(elemResult.print()).equals(unbox(genericResult.getArgument(0).print()))||isSubtypeOf(elemResult,genericResult.getArgument(0))){
        wholeResult = genericResult.getArgument(0).deepClone();
      }
    }

    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0xA0285 the result of the SetInExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTUnionExpressionInfix node) {
    Optional<SymTypeExpression> wholeResult = calculateUnionAndIntersectionInfix(node.getLeft(),node.getRight());

    if(wholeResult.isPresent()){
      lastResult.setLast(wholeResult.get());
      result = wholeResult.get();
    }else{
      lastResult.reset();
      Log.error("0xA0286 The result of the UnionExpressionInfix cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTIntersectionExpressionInfix node) {
    Optional<SymTypeExpression> wholeResult = calculateUnionAndIntersectionInfix(node.getLeft(),node.getRight());

    if(wholeResult.isPresent()){
      lastResult.setLast(wholeResult.get());
      result = wholeResult.get();
    }else{
      lastResult.reset();
      Log.error("0xA0287 the result of the IntersectionExpressionInfix cannot be calculated");
    }
  }

  public Optional<SymTypeExpression> calculateUnionAndIntersectionInfix(ASTExpression leftExpr, ASTExpression rightExpr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    Optional<SymTypeExpression> wholeResult = Optional.empty();

    //element
    leftExpr.accept(realThis);
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      Log.error("0xA0288 the left expression cannot be calculated");
    }
    //set
    rightExpr.accept(realThis);
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      Log.error("0xA0289 the right expression cannot be calculated");
    }
    List<String> collections = Lists.newArrayList("Collection","List","Set","java.util.Collection","java.util.List","java.util.Set");
    if(rightResult.isGenericType()&&leftResult.isGenericType()){
      SymTypeOfGenerics leftGeneric = (SymTypeOfGenerics) leftResult;
      SymTypeOfGenerics rightGeneric = (SymTypeOfGenerics) rightResult;
      String left = leftGeneric.printTypeWithoutTypeArgument();
      String right = rightGeneric.printTypeWithoutTypeArgument();
      if(collections.contains(left) && unbox(left).equals(unbox(right))) {
        if(unbox(leftGeneric.getArgument(0).print()).equals(unbox(rightGeneric.getArgument(0).print()))) {
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader(left,scope),leftGeneric.getArgument(0).deepClone()));
        }else if(isSubtypeOf(leftGeneric.getArgument(0),rightGeneric.getArgument(0))){
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader(right,scope),rightGeneric.getArgument(0).deepClone()));
        }else if(isSubtypeOf(rightGeneric.getArgument(0),leftGeneric.getArgument(0))){
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader(left,scope),leftGeneric.getArgument(0).deepClone()));
        }
      }
    }
    return wholeResult;
  }
}
