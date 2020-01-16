package de.monticore.types.check;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.expressions.commonexpressions._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    //no primitive type and only type allowed --> check that Expression is no field or method
    //JAVA: can only be used in nested classes to get an instance of the enclosing class
    //traverse the inner expression, check that it is a type; this type is the current class and is a nested class
    //can be calculated
    SymTypeExpression innerResult = null;
    SymTypeExpression wholeResult = null;

    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }else{
      Log.error("0xA0318 the result of the inner expression of the ThisExpression cannot be calculated");
    }

    //check recursively until there is no enclosing scope or the spanningsymbol of the scope is a type
    //while the enclosing scope is not null, it is possible that the expression can be calculated
    if(lastResult.isType()) {
      if(scope.getEnclosingScope()!=null){
        IExpressionsBasisScope testScope = scope;
        while (testScope!=null) {
          if(testScope.isPresentSpanningSymbol()&&testScope.getSpanningSymbol() instanceof TypeSymbol) {
            TypeSymbol sym = (TypeSymbol) testScope.getSpanningSymbol();
            if (sym.getName().equals(innerResult.print())) {
              wholeResult = innerResult;
              break;
            }
          }
          testScope = testScope.getEnclosingScope();
        }
      }
    }

    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.reset();
      Log.error("0xA0300 the result of the ThisExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTArrayExpression node) {
    SymTypeExpression indexResult = null;
    SymTypeExpression arrayTypeResult = null;
    SymTypeExpression wholeResult = null;

    //cannot be a type and has to be a integer value
    node.getIndexExpression().accept(getRealThis());
    if (lastResult.isPresentLast()) {
      if (!lastResult.isType()) {
        indexResult = lastResult.getLast();
      }else{
        lastResult.reset();
        Log.error("0xA0316 the inner expression of the array in the ArrayExpression cannot be a type");
      }
    }else{
      Log.error("0xA0317 the inner type of the array in the ArrayExpression cannot be calculated");
    }

    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      if(lastResult.isType()){
        Log.error("0xA0319 the outer type of the array in the ArrayExpression cannot be a type");
      }
      arrayTypeResult = lastResult.getLast();
    }else{
      Log.error("0xA0315 the outer type of the array in the ArrayExpression cannot be calculated");
    }

    //the type of the index has to be an integral type
    if(indexResult.isPrimitive() && ((SymTypeConstant)indexResult).isIntegralType() && arrayTypeResult instanceof SymTypeArray){
      SymTypeArray arrayResult = (SymTypeArray) arrayTypeResult;
      wholeResult = getCorrectResultArrayExpression(indexResult, arrayTypeResult, arrayResult);
    }

    //if nothing found -> fail
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.reset();
      Log.error("0xA0301 the result of the ArrayExpression cannot be calculated");
    }
  }

  private SymTypeExpression getCorrectResultArrayExpression(SymTypeExpression indexResult, SymTypeExpression arrayTypeResult, SymTypeArray arrayResult) {
    SymTypeExpression wholeResult;
    if(arrayResult.getDim()>1){
      //case 1: A[][] bar -> bar[3] returns the type A[] -> decrease the dimension of the array by 1
      wholeResult = SymTypeExpressionFactory.createTypeArray(arrayTypeResult.typeSymbolLoader.getName(),scope,arrayResult.getDim()-1,indexResult);
    }else {
      //case 2: A[] bar -> bar[3] returns the type A
      //determine whether the result has to be a constant, generic or object
      if(arrayResult.getTypeInfo().getTypeParameterList().isEmpty()){
        //if the return type is a primitive
        if(SymTypeConstant.unboxMap.containsKey(arrayResult.getTypeInfo().getName())){
          wholeResult = SymTypeExpressionFactory.createTypeConstant(arrayResult.getTypeInfo().getName());
        }else {
          //if the return type is an object
          wholeResult = SymTypeExpressionFactory.createTypeObject(arrayResult.getTypeInfo().getName(), scope);
        }
      }else {
        //the return type must be a generic
        List<SymTypeExpression> typeArgs = Lists.newArrayList();
        for(TypeVarSymbol s : arrayResult.getTypeInfo().getTypeParameterList()){
          typeArgs.add(SymTypeExpressionFactory.createTypeVariable(s.getName(),scope));
        }
        wholeResult = SymTypeExpressionFactory.createGenerics(arrayResult.getTypeInfo().getName(), scope, typeArgs);
      }
    }
    return wholeResult;
  }

  @Override
  public void traverse(ASTClassExpression node) {
    //only type allowed --> check that Expression is no field or method
    //traverse the inner expression, check that it is a type (how?); the result is the type "Class"
    //can be calculated
    SymTypeExpression wholeResult = null;
    SymTypeExpression innerResult;

    node.getExtReturnType().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
      wholeResult = SymTypeExpressionFactory.createGenerics("Class",scope,innerResult);
    }
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.reset();
      Log.error("0xA0302 the result of the ClassExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTSuperExpression node) {
    //the expression before the super has to be a nested type
    //search for the enclosing type, get its super class and execute the supersuffix

    SymTypeExpression beforeSuperType = null;
    SymTypeExpression wholeResult = null;

    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()) {
      if (lastResult.isType()){
        beforeSuperType = lastResult.getLast();
      }else {
        Log.error("0x0328 the first expression of the SuperExpression has to be a type");
      }
    }else{
      Log.error(/*TODO*/"The result of the expression before the .super cannot be calculated");
    }

    List<SymTypeExpression> superClasses = beforeSuperType.getTypeInfo().getSuperClassesOnly();
    if(superClasses.size()==1){
      SymTypeExpression superClass = superClasses.get(0);
      if(null != node.getSuperSuffix().getName() || !"".equals(node.getSuperSuffix().getName())){
        ASTSuperSuffix superSuffix = node.getSuperSuffix();
        if(null!=superSuffix.getArguments()){
          //case 1 -> Expression.super.<TypeArgument>Method(Args)
          List<SymTypeExpression> typeArgsList = calculateTypeArguments(superSuffix.getExtTypeArgumentList());
          List<MethodSymbol> methods = superClass.getTypeInfo().getSpannedScope().resolveMethodMany(superSuffix.getName());
          if(!methods.isEmpty() && null!=superSuffix.getArguments()){
            //check if the methods fit and return the right returntype
            ASTArguments args = superSuffix.getArguments();
            wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
          }
        }else{
          //case 2 -> Expression.super.Field
          Optional<FieldSymbol> field = superClass.getTypeInfo().getSpannedScope().resolveField(superSuffix.getName());
          if(field.isPresent()){
            wholeResult = field.get().getType();
          }
        }
      }
    }

    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.reset();
      Log.error("0xA0303 the result of the SuperExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTTypeCastExpression node) {
    //innerResult is the SymTypeExpression of the type that will be casted into another type
    SymTypeExpression innerResult = null;
    //castResult is the SymTypeExpression of the type the innerResult will be casted to
    SymTypeExpression castResult = null;
    //wholeResult will be the result of the whole expression
    SymTypeExpression wholeResult = null;

    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
      if(lastResult.isType()){
        lastResult.reset();
        Log.error("0xA0310 the inner expression of the TypeCastExpression cannot be a type");
      }
    }else{
      Log.error("0xA0269 the type of the inner result of the TypeCast cannot be calculated");
    }

    //castResult is the type in the brackets -> (ArrayList) list
    node.getExtType().accept(getRealThis());
    if(lastResult.isPresentLast()){
      castResult = lastResult.getLast();
      lastResult.reset();
      Log.error("0xA0311 the cast expression of the TypeCastExpression must be a type");
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
      lastResult.reset();
      Log.error("0xA0271 the resulting type of the TypeCastExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTInstanceofExpression node) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    SymTypeExpression wholeResult = null;

    //calculate left type: expression that is to be checked for a specific type
    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
      if(lastResult.isType()){
        Log.error("0xA0312 the left type of the InstanceofExpression cannot be a type");
      }
    }else{
      Log.error("0xA0265 the left type of the InstanceofExpression cannot be calculated");
    }

    //calculate right type: type that the expression should be an instance of
    node.getExtType().accept(getRealThis());
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
      Log.error("0xA0313 the right type of the InstanceofExpression must be a type");
    }else{
      Log.error("0xA0266 the right type of the InstanceofExpression cannot be calculated");
    }

    wholeResult = SymTypeExpressionFactory.createTypeConstant("boolean");

    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0xA0267 the resulting type of the InstanceofExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTPrimaryThisExpression node) {
    //search for the nearest TypeSymbol and return its Type
    SymTypeExpression wholeResult = null;
    IExpressionsBasisScope testScope = scope;
    TypeSymbol typeSymbol=searchForTypeSymbolSpanningEnclosingScope(scope);
    wholeResult = getResultOfPrimaryThisExpression(typeSymbol);
    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0xA0264 the resulting type of the PrimaryThisExpression cannot be calculated");
    }
  }

  private SymTypeExpression getResultOfPrimaryThisExpression(TypeSymbol typeSymbol) {
    SymTypeExpression wholeResult;
    if(typeSymbol.getTypeParameterList().isEmpty()){
      //if the return type is a primitive
      if(SymTypeConstant.unboxMap.containsKey(typeSymbol.getName())){
        wholeResult = SymTypeExpressionFactory.createTypeConstant(typeSymbol.getName());
      }else {
        //the return type is an object
        wholeResult = SymTypeExpressionFactory.createTypeObject(typeSymbol.getName(), scope);
      }
    }else {
      //the return type must be a generic
      List<SymTypeExpression> typeArgs = Lists.newArrayList();
      for(TypeVarSymbol s : typeSymbol.getTypeParameterList()){
        typeArgs.add(SymTypeExpressionFactory.createTypeVariable(s.getName(),scope));
      }
      wholeResult = SymTypeExpressionFactory.createGenerics(typeSymbol.getName(), scope, typeArgs);
    }
    return wholeResult;
  }

  @Override
  public void traverse(ASTPrimarySuperExpression node) {
    SymTypeExpression wholeResult=null;

    TypeSymbol typeSymbol = searchForTypeSymbolSpanningEnclosingScope(scope);
    if(typeSymbol.getSuperClassesOnly().size()==1){
      wholeResult=typeSymbol.getSuperClassesOnly().get(0);
    }else {
      Log.error("0xA0261 for super to work there has to be exactly one superclass");
    }
    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0xA0260 the resulting type of the PrimarySuperExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTGenericInvocationExpression node) {
    //expressions of type A.B.<String>c() or A.B.<Integer>super.<String>c() plus Arguments in the brackets
    SymTypeExpression expressionResult = null;
    SymTypeExpression wholeResult = null;

    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      expressionResult = lastResult.getLast();
    }else{
      Log.error("0xA0320 the result of the left expression of the GenericInvocationExpression cannot be calculated");
    }

    //the only case where you can calculate a result is Expression.<TypeArguments>method()
    //because the other cases of the GenericInvocationSuffix can only be calculated if the expression
    //is a PrimaryGenericInvocationExpression

    List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getPrimaryGenericInvocationExpression().getExtTypeArgumentList());


    //search in the scope of the type that before the "." for a method that has the right name
    List<MethodSymbol> methods = expressionResult.getTypeInfo().getSpannedScope()
        .resolveMethodMany(node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getName());

    if(!methods.isEmpty() && null!=node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getArguments()){
      //check if the methods fit and return the right returntype
      ASTArguments args = node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getArguments();
      wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
    }

    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result=wholeResult;
    }else {
      lastResult.reset();
      Log.error("0xA0304 the result of the GenericInvocationExpression cannot be calculated");
    }
  }


  private List<SymTypeExpression> calculateTypeArguments(List<ASTExtTypeArgumentExt> extTypeArgumentList) {
    //calculate each TypeArgument and return the results in a list
    List<SymTypeExpression> typeArgsList = Lists.newArrayList();
    for(int i = 0;i<extTypeArgumentList.size();i++){
      extTypeArgumentList.get(i).accept(getRealThis());
      if(lastResult.isPresentLast()){
        typeArgsList.add(lastResult.getLast());
      }else{
        Log.error("0xA0327 the calculation of the "+i+1+". TypeArgument cannot be calculated");
      }
    }
    return typeArgsList;
  }

  private SymTypeExpression checkMethodsAndReplaceTypeVariables(List<MethodSymbol> methods, ASTArguments args, List<SymTypeExpression> typeArgsList) {
    outer:for(int i = 0;i<methods.size();i++){
      MethodSymbol method = methods.get(i);
      if(method.getParameterList().size()!=args.getExpressionList().size()){
        //wrong method
        continue;
      }
      if(method.getTypeVariableList().size()!=typeArgsList.size()){
        //wrong method
        continue;
      }

      List<TypeVarSymbol> typeVars = method.getTypeVariableList();
      //method has the correct name, the correct number of type arguments and the correct amount of parameters
      //search for the right method by searching for the TypeVariables in the parameters and the return type of the methodsymbol
      //and if there is anything wrong jump to the next method -> do not change the methodsymbol
      //if everything is okay, return the return type of the method -> if this return type is a type variable return the typeArgument
      //that replaces this type variable
      Map<String,SymTypeExpression> transformMap = Maps.newHashMap();
      for(int j = 0;j<method.getTypeVariableList().size();j++){
        transformMap.put(method.getTypeVariable(j).getName(),typeArgsList.get(j));
      }

      for(int j = 0;j<method.getParameterList().size();j++){
        FieldSymbol param = method.getParameter(j);
        if(param.getType().isTypeVariable()){
          if(!transformMap.containsKey(param.getType().print())){
            //there is a typevariable that cannot be resolved to the correct type -> wrong method
            continue outer;
          }
        }
      }
      if(method.getReturnType().isTypeVariable()){
        if(!transformMap.containsKey(method.getReturnType().print())){
          //the return type is a type variable that cannot be resolved -> wrong method
          continue outer;
        }else{
          return transformMap.get(method.getReturnType().print());
        }
      }else{
        return method.getReturnType();
      }
    }
    //there cannot be found a fitting method
    return null;
  }

  @Override
  public void traverse(ASTPrimaryGenericInvocationExpression node) {
    //expressions of the type <String>c() or <String>super.<Integer>c() plus Arguments in the brackets

    SymTypeExpression wholeResult = null;

    List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getExtTypeArgumentList());

    if(null==node.getGenericInvocationSuffix().getSuperSuffix()){
      if(null!=node.getGenericInvocationSuffix().getName()&&!"".equals(node.getGenericInvocationSuffix().getName())){
        //case 1: <TypeVariable>method(Args) -> similar to GenericInvocationExpression
        //can be accessed solely or after another expression -> check if lastResult is present
        ITypeSymbolsScope testScope;
        if(lastResult.isPresentLast()){
          testScope = lastResult.getLast().getTypeInfo().getSpannedScope();
        }else{
          testScope = scope;
        }
        //resolve for fitting methods
        List<MethodSymbol> methods = testScope.resolveMethodMany(node.getGenericInvocationSuffix().getName());
        if(!methods.isEmpty() && null!=node.getGenericInvocationSuffix().getArguments()){
          //check if the methods fit and return the right returntype
          ASTArguments args = node.getGenericInvocationSuffix().getArguments();
          wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
        }
      }else{
        //case 2: <TypeVariable>this(Args) -> similar to PrimaryThisExpression, use method checkMethodsAndReplaceTypeVariables
        //can only be accessed solely -> there cannot be a lastresult
        //search for the nearest enclosingscope spanned by a typesymbol
        TypeSymbol typeSymbol = searchForTypeSymbolSpanningEnclosingScope(scope);
        //get the constructors of the typesymbol
        List<MethodSymbol> methods = typeSymbol.getSpannedScope().resolveMethodMany(typeSymbol.getName());
        if(!methods.isEmpty() && null!=node.getGenericInvocationSuffix().getArguments()){
          //check if the constructors fit and return the right returntype
          ASTArguments args = node.getGenericInvocationSuffix().getArguments();
          wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
        }
      }
    }else{
      ASTSuperSuffix superSuffix = node.getGenericInvocationSuffix().getSuperSuffix();
      if(null==superSuffix.getName()||"".equals(superSuffix.getName())){
        //case 3: <TypeVariable>super(Args) -> find the constructor of the super class, use method checkMethodsAndReplaceTypeVariables
        //search for the nearest enclosingscope spanned by a typesymbol
        TypeSymbol subType = searchForTypeSymbolSpanningEnclosingScope(scope);
        //get the superclass of this typesymbol and search for its fitting constructor
        if(subType.getSuperClassesOnly().size()==1){
          SymTypeExpression superClass = subType.getSuperClassesOnly().get(0);
          List<MethodSymbol> methods = superClass.getTypeInfo().getSpannedScope().resolveMethodMany(superClass.getTypeInfo().getName());
          if(!methods.isEmpty() && null!=node.getGenericInvocationSuffix().getArguments()){
            //check if the constructors fit and return the right returntype
            ASTArguments args = node.getGenericInvocationSuffix().getArguments();
            wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
          }
        }
      }
    }

    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else {
      lastResult.reset();
      Log.error("0xA0305 the result of the PrimaryGenericInvocationExpression cannot be calculated");
    }
  }

  private TypeSymbol searchForTypeSymbolSpanningEnclosingScope(IExpressionsBasisScope scope) {
    //search for the nearest type symbol in the enclosing scopes -> for this and super to get the
    //current object
    while(scope!=null){
      if(scope.isPresentSpanningSymbol()&&scope.getSpanningSymbol() instanceof TypeSymbol){
        return (TypeSymbol)scope.getSpanningSymbol();
      }
      scope = scope.getEnclosingScope();
    }
    //no typesymbol found
    return null;
  }
}
