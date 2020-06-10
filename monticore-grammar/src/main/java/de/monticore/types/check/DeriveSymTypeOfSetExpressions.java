/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix;
import de.monticore.expressions.setexpressions._ast.ASTIsInExpression;
import de.monticore.expressions.setexpressions._ast.ASTSetInExpression;
import de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbolLoader;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.isSubtypeOf;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in SetExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
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
    //e isin E checks whether e is an Element in Set E and can only be calculated if e is a subtype or the same type as the set type

    SymTypeExpression elemResult = null;
    SymTypeExpression setResult = null;
    SymTypeExpression wholeResult = null;

    //element
    node.getElem().accept(realThis);
    if(typeCheckResult.isPresentLast()){
      elemResult = typeCheckResult.getLast();
    }else{
      logError("0xA0286",node.getElem().get_SourcePositionStart());
    }
    //set
    node.getSet().accept(realThis);
    if(typeCheckResult.isPresentLast()){
      setResult = typeCheckResult.getLast();
    }else{
      logError("0xA0287",node.getSet().get_SourcePositionStart());
    }
    List<String> collections = Lists.newArrayList("List","Set");
    boolean correct = false;
    for(String s: collections) {
      if (setResult.isGenericType() && setResult.getTypeInfo().getName().equals(s)) {
        correct = true;
      }
    }
    if(correct){
      SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
      if(unbox(elemResult.print()).equals(unbox(genericResult.getArgument(0).print()))||isSubtypeOf(elemResult,genericResult.getArgument(0))){
        wholeResult = SymTypeExpressionFactory.createTypeConstant("boolean");
      }
    }

    if(null!=wholeResult){
      typeCheckResult.setLast(wholeResult);
    }else{
      typeCheckResult.reset();
      logError("0xA0288",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTSetInExpression node) {
    //e in E checks whether e is an Element in Set E and can only be calculated if e is a subtype or the same type as the set type

    SymTypeExpression elemResult = null;
    SymTypeExpression setResult = null;
    SymTypeExpression wholeResult = null;

    //element
    node.getElem().accept(realThis);
    if(typeCheckResult.isPresentLast()){
      elemResult = typeCheckResult.getLast();
    }else{
      logError("0xA0289",node.getElem().get_SourcePositionStart());
    }
    //set
    node.getSet().accept(realThis);
    if(typeCheckResult.isPresentLast()){
      setResult = typeCheckResult.getLast();
    }else{
      logError("0xA0290",node.getSet().get_SourcePositionStart());
    }
    List<String> collections = Lists.newArrayList("List","Set");
    boolean correct = false;
    for(String s: collections) {
      if (setResult.isGenericType() && setResult.getTypeInfo().getName().equals(s)) {
        correct = true;
      }
    }
    if(correct){
      SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
      if(unbox(elemResult.print()).equals(unbox(genericResult.getArgument(0).print()))||isSubtypeOf(elemResult,genericResult.getArgument(0))){
        wholeResult = genericResult.getArgument(0).deepClone();
      }
    }

    if(null!=wholeResult){
      typeCheckResult.setLast(wholeResult);
    }else{
      typeCheckResult.reset();
      logError("0xA0291",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTUnionExpressionInfix node) {
    //union of two sets -> both sets need to have the same type or their types need to be sub/super types
    Optional<SymTypeExpression> wholeResult = calculateUnionAndIntersectionInfix(node, node.getLeft(),node.getRight());

    if(wholeResult.isPresent()){
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0292",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTIntersectionExpressionInfix node) {
    //intersection of two sets -> both sets need to have the same type or their types need to be sub/super types
    Optional<SymTypeExpression> wholeResult = calculateUnionAndIntersectionInfix(node, node.getLeft(),node.getRight());

    if(wholeResult.isPresent()){
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0293",node.get_SourcePositionStart());
    }
  }

  public Optional<SymTypeExpression> calculateUnionAndIntersectionInfix(ASTExpression expr, ASTExpression leftExpr, ASTExpression rightExpr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    Optional<SymTypeExpression> wholeResult = Optional.empty();

    //element
    leftExpr.accept(realThis);
    if(typeCheckResult.isPresentLast()){
      leftResult = typeCheckResult.getLast();
    }else{
      logError("0xA0294",leftExpr.get_SourcePositionStart());
    }
    //set
    rightExpr.accept(realThis);
    if(typeCheckResult.isPresentLast()){
      rightResult = typeCheckResult.getLast();
    }else{
      logError("0xA0295",rightExpr.get_SourcePositionStart());
    }
    List<String> collections = Lists.newArrayList("List","Set");
    if(rightResult.isGenericType()&&leftResult.isGenericType()){
      SymTypeOfGenerics leftGeneric = (SymTypeOfGenerics) leftResult;
      SymTypeOfGenerics rightGeneric = (SymTypeOfGenerics) rightResult;
      String left = leftGeneric.getTypeInfo().getName();
      String right = rightGeneric.getTypeInfo().getName();
      if(collections.contains(left) && unbox(left).equals(unbox(right))) {
        if(unbox(leftGeneric.getArgument(0).print()).equals(unbox(rightGeneric.getArgument(0).print()))) {
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new OOTypeSymbolLoader(left,getScope(expr.getEnclosingScope())),leftGeneric.getArgument(0).deepClone()));
        }else if(isSubtypeOf(leftGeneric.getArgument(0),rightGeneric.getArgument(0))){
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new OOTypeSymbolLoader(right,getScope(expr.getEnclosingScope())),rightGeneric.getArgument(0).deepClone()));
        }else if(isSubtypeOf(rightGeneric.getArgument(0),leftGeneric.getArgument(0))){

          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new OOTypeSymbolLoader(left,getScope(expr.getEnclosingScope())),leftGeneric.getArgument(0).deepClone()));
        }
      }
    }
    return wholeResult;
  }
}
