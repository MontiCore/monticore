package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor;
import de.monticore.expressions.prettyprint.ExpressionsPrettyPrinterDelegator;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.logging.Log;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

/**
 * ordner, grammatiken, beschreibung visitor und spaeter delegatorvisitor, referenz auf grammatik
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
    //no primitive type and only type allowed --> check that Expression is no field or method
    //JAVA: can only be used in nested classes to get an instance of the enclosing class
    //traverse the inner expression, check that it is a type; this type is the current class is a nested class
    //can be calculated
    SymTypeExpression innerResult = null;
    SymTypeExpression wholeResult = null;

    node.getExpression().accept(realThis);
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }else{
      Log.error("0xA0318 the result of the inner expression of the ThisExpression cannot be calculated");//TODO
    }

    //check recursively until there is no enclosing scope
    //while the enclosing scope is a type, it is possible that the .this can be calculated
    String toResolve = new ExpressionsPrettyPrinterDelegator(new IndentPrinter()).prettyprint(node.getExpression());
    if(scope.resolveType(toResolve).isPresent()) {
      IExpressionsBasisScope testScope = scope;
      while (testScope.isPresentSpanningSymbol()) {
        if(testScope.getSpanningSymbol() instanceof TypeSymbol) {
          TypeSymbol sym = (TypeSymbol) testScope.getSpanningSymbol();
          if (sym.getName().equals(innerResult.print())) {
            wholeResult = innerResult;
          }
        }
        if (testScope.getEnclosingScope() != null) {
          testScope = testScope.getEnclosingScope();
        }
        else {
          break;
        }
      }
    }

    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.setLastAbsent();
      Log.error("0xA0300 the result of the ThisExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTArrayExpression node) {
    SymTypeExpression indexResult = null;
    SymTypeExpression arrayTypeResult = null;
    SymTypeExpression wholeResult = null;

    //has to be a type
    node.getExpression().accept(realThis);
    if(lastResult.isPresentLast()){
      arrayTypeResult = lastResult.getLast();
    }else{
      Log.error("0xA0315 the outer type of the array in the ArrayExpression cannot be calculated");
    }

    //cannot be a type and has to be a integer value
    node.getIndexExpression().accept(realThis);
    if (lastResult.isPresentLast()) {
      if (!scope.resolveType(
          new ExpressionsPrettyPrinterDelegator(new IndentPrinter()).prettyprint(node.getExpression()))
          .isPresent()) {
        indexResult = lastResult.getLast();
      }else{
        lastResult.setLastAbsent();
        Log.error("0xA0316 the inner expression of the array int the ArrayExpression cannot be a type");
      }
    }else{
      Log.error("0xA0317 the inner type of the array in the ArrayExpression cannot be calculated");
    }

    if(indexResult.isPrimitive() && ((SymTypeConstant)indexResult).isIntegralType()){
      String toResolve = new ExpressionsPrettyPrinterDelegator(new IndentPrinter()).prettyprint(node.getExpression());

      Optional<TypeSymbol> type = scope.resolveType(toResolve);
      Optional<MethodSymbol> method = scope.resolveMethod(toResolve);
      Optional<FieldSymbol> field = scope.resolveField(toResolve);

      //in which order?
      //TODO: check that indexResult is an integer
      if(type.isPresent()){
        if(arrayTypeResult instanceof SymTypeArray){
          //recursiveness
          wholeResult = SymTypeExpressionFactory.createTypeArray(arrayTypeResult.typeSymbolLoader.getName(),scope,((SymTypeArray) arrayTypeResult).getDim()+1,indexResult);
        }else {
          //not recursive
          wholeResult = SymTypeExpressionFactory.createTypeArray(arrayTypeResult.typeSymbolLoader.getName(), scope, 1, indexResult.deepClone());
        }
      }else if(method.isPresent()){
        //example: method foo() has return type int[] -> foo()[3] --> returns int
        MethodSymbol sym = method.get();
        if(sym.getReturnType() instanceof SymTypeArray){
          if(((SymTypeArray) sym.getReturnType()).getDim()>1){
            wholeResult = SymTypeExpressionFactory.createTypeArray(arrayTypeResult.typeSymbolLoader.getName(),scope,((SymTypeArray) sym.getReturnType()).getDim()-1,indexResult);
          }else {
            //determine whether it has to be a constant, generic or object
            if(sym.getReturnType().getTypeInfo().getTypeParameterList().isEmpty()){
              if(SymTypeConstant.unboxMap.containsKey(sym.getReturnType().getTypeInfo().getName())){
                wholeResult = SymTypeExpressionFactory.createTypeConstant(sym.getReturnType().getTypeInfo().getName());
              }else {
                wholeResult = SymTypeExpressionFactory.createTypeObject(sym.getReturnType().getTypeInfo().getName(), scope);
              }
            }else {
              List<SymTypeExpression> typeArgs = Lists.newArrayList();
              for(TypeVarSymbol s : sym.getReturnType().getTypeInfo().getTypeParameterList()){
                typeArgs.add(SymTypeExpressionFactory.createTypeVariable(s.getName(),scope));
              }
              wholeResult = SymTypeExpressionFactory.createGenerics(sym.getReturnType().getTypeInfo().getName(), scope, typeArgs);
            }
          }
        }
      }else if(field.isPresent()){
        //example: int[] bar -> bar[3] returns int
        FieldSymbol sym = field.get();
        if(sym.getType() instanceof SymTypeArray){
          if(((SymTypeArray) sym.getType()).getDim()>1){
            wholeResult = SymTypeExpressionFactory.createTypeArray(arrayTypeResult.typeSymbolLoader.getName(),scope,((SymTypeArray) sym.getType()).getDim()-1,indexResult);
          }else {
            //determine whether it has to be a constant, generic or object
            if(sym.getType().getTypeInfo().getTypeParameterList().isEmpty()){
              if(SymTypeConstant.unboxMap.containsKey(sym.getType().getTypeInfo().getName())){
                wholeResult = SymTypeExpressionFactory.createTypeConstant(sym.getType().getTypeInfo().getName());
              }else {
                wholeResult = SymTypeExpressionFactory.createTypeObject(sym.getType().getTypeInfo().getName(), scope);
              }
            }else {
              List<SymTypeExpression> typeArgs = Lists.newArrayList();
              for(TypeVarSymbol s : sym.getType().getTypeInfo().getTypeParameterList()){
                typeArgs.add(SymTypeExpressionFactory.createTypeVariable(s.getName(),scope));
              }
              wholeResult = SymTypeExpressionFactory.createGenerics(sym.getType().getTypeInfo().getName(), scope, typeArgs);
            }
          }
        }
      }
    }

    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.setLastAbsent();
      Log.error("0xA0301 the result of the ArrayExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTClassExpression node) {
    //TODO:#2465 -> vervollstaendigen
    //only type allowed --> check that Expression is no field or method
    //traverse the inner expression, check that it is a type (how?); the result is the type "Class"
    //can be calculated
    SymTypeExpression wholeResult = null;
    SymTypeExpression innerResult = null;

    //TODO: traverse method for external return type -> synthesizer that must be given to this class when initialising the TypeCheck or (perhaps better) that can be added to the Delegator
    node.getExtReturnType().accept(realThis);
    if(lastResult.isPresentLast()){
//      if(scope.resolveType(/*PrettyPrinter for MCSimpleGenericTypes that can also print MCBasicTypes and MCCollectionTypes*/).isPresent()){
        innerResult = lastResult.getLast();
        wholeResult = SymTypeExpressionFactory.createGenerics("Class",scope,innerResult);
//      }
    }
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.setLastAbsent();
      Log.error("0xA0302 the result of the ClassExpression cannot be calculated");
    }
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
//      if(scope.resolveType(/* print the name of the expression*/).isPresent()){
        lastResult.setLastAbsent();
        Log.error("0xA0310 the inner expression of the TypeCastExpression cannot be a type");
      }
//    }else{
      Log.error("0xA0269 the type of the inner result of the TypeCast cannot be calculated");
//    }

    //TODO: test that castResult is a type
    //TODO: traverse method for external ExtType
    node.getExtType().accept(realThis);
    if(lastResult.isPresentLast()){
      castResult = lastResult.getLast();
//      if(!scope.resolveType(/* print the name of the expression*/).isPresent()){
        lastResult.setLastAbsent();
        Log.error("0xA0311 the cast expression of the TypeCastExpression must be a type");
//      }
    }else{
      Log.error("0xA0270 the cast type of the TypeCast cannot be calculated");
    }

    if(TypeCheck.compatible(castResult,innerResult)||TypeCheck.compatible(innerResult,castResult)){
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
//      if(scope.resolveType(/*print the type of the expression*/).isPresent()){
        Log.error("0xA0312 the left type of the InstanceofExpression cannot be a type");
//      }
    }else{
      Log.error("0xA0265 the left type of the InstanceofExpression cannot be calculated");
    }

    //calculate right type: type that the expression should be an instance of
    //TODO: traverse method for external exttype
    //TODO: test that right expression is a type
    node.getExtType().accept(realThis);
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
//      if(!scope.resolveType(/*print the name of the type*/).isPresent()){
        Log.error("0xA0313 the right type of the InstanceofExpression must be a type");
//      }
    }else{
      Log.error("0xA0266 the right type of the InstanceofExpression cannot be calculated");
    }

    wholeResult = SymTypeExpressionFactory.createTypeConstant("boolean");

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
