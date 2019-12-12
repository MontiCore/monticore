package de.monticore.types.check;

import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in JavaClassExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfJavaClassExpressions extends DeriveSymTypeOfCommonExpressions implements JavaClassExpressionsVisitor {

  private JavaClassExpressionsVisitor realThis;

  public DeriveSymTypeOfJavaClassExpressions(){
    this.realThis = this;
  }

  @Override
  public void setRealThis(JavaClassExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public JavaClassExpressionsVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void traverse(ASTThisExpression node) {
    //TODO:#2465 -> vervollstaendigen
    //no primitive type and only type allowed --> check that Expression is no field or method
    //traverse the inner expression, check that it is a type (how?); this type is the result
    //can be calculated
    lastResult.setLastAbsent();
    Log.error("0xA0300 the result of the ThisExpression cannot be calculated");
  }

  @Override
  public void traverse(ASTArrayExpression node) {
    //TODO:#2465 -> vervollstaendigen
    //can be calculated
    lastResult.setLastAbsent();
    Log.error("0xA0301 the result of the ArrayExpression cannot be calculated");
  }

  @Override
  public void traverse(ASTClassExpression node) {
    //TODO:#2465 -> vervollstaendigen
    //only type allowed --> check that Expression is no field or method
    //traverse the inner expression, check that it is a type (how?); the result is the type "Class"
    //can be calculated
    lastResult.setLastAbsent();
    Log.error("0xA0302 the result of the ClassExpression cannot be calculated");
  }

  @Override
  public void traverse(ASTSuperExpression node) {
    //TODO:#2465 -> vervollstaendigen
    //TODO: return the super type of the prior qualified name
    //can be calculated
    lastResult.setLastAbsent();
    Log.error("0xA0303 the result of the SuperExpression cannot be calculated");
  }

  @Override
  public void traverse(ASTTypeCastExpression node) {
    //innerResult is the SymTypeExpression of the type that will be casted into another type
    SymTypeExpression innerResult = null;
    //castResult is the SymTypeExpression of the type the innerResult will be casted to
    SymTypeExpression castResult = null;
    //wholeResult will be the result of the whole expression
    SymTypeExpression wholeResult = null;

    //TODO: test that innerResult is not a type
    node.getExpression().accept(realThis);
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }else{
      Log.error("0xA0269 the type of the inner result of the TypeCast cannot be calculated");
    }

    //TODO: test that castResult is a type
    //TODO: traverse method for external ExtType
    //castResult is the type in the brackets -> (ArrayList) list
    node.getExtType().accept(realThis);
    if(lastResult.isPresentLast()){
      castResult = lastResult.getLast();
    }else{
      Log.error("0xA0270 the cast type of the TypeCast cannot be calculated");
    }

    if(TypeCheck.compatible(castResult,innerResult)){
      wholeResult = castResult.deepClone();
    }

    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.setLastAbsent();
      Log.error("0xA0271 the resulting type of the TypeCastExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTInstanceofExpression node) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    SymTypeExpression wholeResult = null;

    //calculate left type: expression that is to be checked for a specific type
    //TODO: test that the left expression is not a type
    node.getExpression().accept(realThis);
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      Log.error("0xA0265 the left type of the InstanceofExpression cannot be calculated");
    }

    //calculate right type: type that the expression should be an instance of
    //TODO: traverse method for external exttype
    //TODO: test that right expression is a type
    node.getExtType().accept(realThis);
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      Log.error("0xA0266 the right type of the InstanceofExpression cannot be calculated");
    }

    if(TypeCheck.compatible(rightResult,leftResult)){
      wholeResult = SymTypeExpressionFactory.createTypeConstant("boolean");
    }

    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.setLastAbsent();
      Log.error("0xA0267 the resulting type of the InstanceofExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTPrimaryThisExpression node) {
    SymTypeExpression wholeResult = null;
    if(scope.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = scope.getSpanningSymbol();
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        wholeResult = SymTypeExpressionFactory.createTypeObject(typeSymbol.getName(),scope);
        //TODO: what if typesymbol is generic
      }else if(spanningSymbol instanceof MethodSymbol) {
        //if the scope is spanned by a method, the enclosing scope must be the scope spanned by the type
        if (null != scope.getEnclosingScope()) {
          IScopeSpanningSymbol innerSpanningSymbol = scope.getEnclosingScope().getSpanningSymbol();
          if (innerSpanningSymbol instanceof TypeSymbol) {
            TypeSymbol typeSymbol = (TypeSymbol) innerSpanningSymbol;
            wholeResult = SymTypeExpressionFactory.createTypeObject(typeSymbol.getName(),scope);
            //TODO: what if typesymbol is generic
          }else {
            lastResult.setLastAbsent();
            Log.error("0xA0261 the spanning symbol of the enclosing scope has to be a type symbol");
          }
        }else {
          lastResult.setLastAbsent();
          Log.error("0xA0268 the enclosing scope of the actual scope must not be null");
        }
      }else {
        lastResult.setLastAbsent();
        Log.error("0xA0262 the spanning symbol of the scope has to be either a method symbol or a type symbol");
      }
    }else{
      lastResult.setLastAbsent();
      Log.error("0xA0263 the scope has to be spanned by a symbol");
    }
    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.setLastAbsent();
      Log.error("0x0264 the resulting type of the PrimaryThisExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTPrimarySuperExpression node) {
    SymTypeExpression wholeResult=null;
    if(scope.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = scope.getSpanningSymbol();
      if(spanningSymbol instanceof TypeSymbol){
        //if the scope is spanned by a type, search for the supertype and return it
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        if(typeSymbol.getSuperTypeList().size()>1){
          lastResult.setLastAbsent();
          Log.error("0xA0252 In Java there cannot be more than one super class");
        }else if(typeSymbol.getSuperTypeList().isEmpty()){
          lastResult.setLastAbsent();
          Log.error("0xA0253 No supertype could be found");
        }else{
          wholeResult = typeSymbol.getSuperType(0);
        }
      }else if(spanningSymbol instanceof MethodSymbol){
        //if the scope is spanned by a method, the enclosing scope must be the scope spanned by the type
        if(null!=scope.getEnclosingScope()){
          IScopeSpanningSymbol innerSpanningSymbol = scope.getEnclosingScope().getSpanningSymbol();
          if(innerSpanningSymbol instanceof TypeSymbol){
            TypeSymbol typeSymbol = (TypeSymbol) innerSpanningSymbol;
            if(typeSymbol.getSuperTypeList().size()>1){
              lastResult.setLastAbsent();
              Log.error("0xA0254 In Java there cannot be more than one super class");
            }else if(typeSymbol.getSuperTypeList().isEmpty()){
              lastResult.setLastAbsent();
              Log.error("0xA0255 No supertype could be found");
            }else{
              wholeResult = typeSymbol.getSuperType(0);
            }
          }else{
            lastResult.setLastAbsent();
            Log.error("0xA0258 the spanning symbol of the enclosing scope has to be a type symbol");
          }
        }else{
          lastResult.setLastAbsent();
          Log.error("0xA0259 there has to be an enclosing scope present");
        }
      }else{
        lastResult.setLastAbsent();
        Log.error("0xA0256 the scope must either be spanned by a type or a method");
      }
    }else{
      lastResult.setLastAbsent();
      Log.error("0xA0257 the scope has to be spanned by a symbol");
    }
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.setLastAbsent();
      Log.error("0xA0260 the resulting type of the PrimarySuperExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTGenericInvocationExpression node) {
    //TODO:#2465 -> vervollstaendigen
    lastResult.setLastAbsent();
    Log.error("0xA0304 the result of the GenericInvocationExpression cannot be calculated");
  }

  @Override
  public void traverse(ASTPrimaryGenericInvocationExpression node) {
    //TODO:#2465 -> vervollstaendigen
    lastResult.setLastAbsent();
    Log.error("0xA0305 the result of the PrimaryGenericInvocationExpression cannot be calculated");
  }
}
