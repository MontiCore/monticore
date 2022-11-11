/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsHandler;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsTraverser;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.types.check.TypeCheck.compatible;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in JavaClassExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfJavaClassExpressions extends AbstractDeriveFromExpression implements JavaClassExpressionsVisitor2, JavaClassExpressionsHandler {

  protected JavaClassExpressionsTraverser traverser;

  @Override
  public JavaClassExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(JavaClassExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTThisExpression node) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    SymTypeExpression wholeResult = calculateThisExpression(node, innerResult);

    getTypeCheckResult().reset();
    getTypeCheckResult().setResult(wholeResult);
    if(wholeResult.isObscureType()) {
      Log.error("0xA0252 Could not derive the type that 'this' refers to", node.get_SourcePositionStart());
    }
  }

  protected SymTypeExpression calculateThisExpression(ASTThisExpression expr, SymTypeExpression innerResult){
    //no primitive type and only type allowed --> check that Expression is no field or method
    //JAVA: can only be used in nested classes to get an instance of the enclosing class
    //traverse the inner expression, check that it is a type; this type is the current class and is a nested class
    //can be calculated
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //check recursively until there is no enclosing scope or the spanningsymbol of the scope is a type
    //while the enclosing scope is not null, it is possible that the expression can be calculated
    int count = 0;
    if(getTypeCheckResult().isType() && getScope(expr.getEnclosingScope()).getEnclosingScope()!=null) {
      IBasicSymbolsScope testScope = getScope(expr.getEnclosingScope());
      while (testScope!=null) {
        if(testScope.isPresentSpanningSymbol()&&testScope.getSpanningSymbol() instanceof OOTypeSymbol) {
          count++;
          OOTypeSymbol sym = (OOTypeSymbol) testScope.getSpanningSymbol();
          if (sym.getName().equals(innerResult.getTypeInfo().getName())&&count>1) {
            wholeResult = innerResult;
            break;
          }
        }
        testScope = testScope.getEnclosingScope();
      }
    }
    return wholeResult;
  }

  @Override
  public void traverse(ASTArrayExpression node) {
    SymTypeExpression indexResult = acceptThisAndReturnSymTypeExpression(node.getIndexExpression());
    if(getTypeCheckResult().isType()){
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0253 the expression at source position "+node.getIndexExpression().get_SourcePositionStart()+" cannot be a type");
      return;
    }
    SymTypeExpression arrayTypeResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if(getTypeCheckResult().isType()){
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0255 the expression at source position "+node.getExpression().get_SourcePositionStart()+" cannot be a type");
      return;
    }

    if(!indexResult.isObscureType() && !arrayTypeResult.isObscureType()){
      SymTypeExpression wholeResult = calculateArrayExpression(node, arrayTypeResult, indexResult);
      storeResultOrLogError(wholeResult, node, "0xA0257");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateArrayExpression(ASTArrayExpression node, SymTypeExpression arrayTypeResult, SymTypeExpression indexResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //the type of the index has to be an integral type
    if(indexResult.isPrimitive() && ((SymTypePrimitive)indexResult).isIntegralType() && arrayTypeResult instanceof SymTypeArray){
      SymTypeArray arrayResult = (SymTypeArray) arrayTypeResult;
      wholeResult = getCorrectResultArrayExpression(node.getEnclosingScope(), indexResult, arrayTypeResult, arrayResult);
    }
    return wholeResult;
  }

  protected SymTypeExpression getCorrectResultArrayExpression(IExpressionsBasisScope scope, SymTypeExpression indexResult, SymTypeExpression arrayTypeResult, SymTypeArray arrayResult) {
    SymTypeExpression wholeResult;
    if(arrayResult.getDim()>1){
      //case 1: A[][] bar -> bar[3] returns the type A[] -> decrease the dimension of the array by 1
      wholeResult = SymTypeExpressionFactory.createTypeArray(arrayTypeResult.getTypeInfo().getName(),getScope(scope),arrayResult.getDim()-1,indexResult);
    }else {
      //case 2: A[] bar -> bar[3] returns the type A
      //determine whether the result has to be a constant, generic or object
      if(arrayResult.getTypeInfo().getTypeParameterList().isEmpty()){
        //if the return type is a primitive
        if(SymTypePrimitive.boxMap.containsKey(arrayResult.getTypeInfo().getName())){
          wholeResult = SymTypeExpressionFactory.createPrimitive(arrayResult.getTypeInfo().getName());
        }else {
          //if the return type is an object
          wholeResult = SymTypeExpressionFactory.createTypeObject(arrayResult.getTypeInfo().getName(), getScope(scope));
        }
      }else {
        //the return type must be a generic
        List<SymTypeExpression> typeArgs = Lists.newArrayList();
        for(TypeVarSymbol s : arrayResult.getTypeInfo().getTypeParameterList()){
          typeArgs.add(SymTypeExpressionFactory.createTypeVariable(s.getName(),getScope(scope)));
        }
        wholeResult = SymTypeExpressionFactory.createGenerics(arrayResult.getTypeInfo().getName(), getScope(scope), typeArgs);
        wholeResult = replaceTypeVariables(wholeResult,typeArgs,((SymTypeOfGenerics)arrayResult.getArgument()).getArgumentList());
      }
    }
    return wholeResult;
  }

  protected SymTypeExpression replaceTypeVariables(SymTypeExpression wholeResult, List<SymTypeExpression> typeArgs, List<SymTypeExpression> argumentList) {
    Map<SymTypeExpression,SymTypeExpression> map = Maps.newHashMap();
    if(typeArgs.size()!=argumentList.size()){
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA2297 Different amount of type variables and type arguments");
    }else{
      for(int i = 0;i<typeArgs.size();i++){
        map.put(typeArgs.get(i),argumentList.get(i));
      }

      List<SymTypeExpression> oldArgs = ((SymTypeOfGenerics) wholeResult).getArgumentList();
      List<SymTypeExpression> newArgs = Lists.newArrayList();
      for (SymTypeExpression oldArg : oldArgs) {
        newArgs.add(map.getOrDefault(oldArg, oldArg));
      }
      ((SymTypeOfGenerics) wholeResult).setArgumentList(newArgs);
    }
    return wholeResult;
  }

  @Override
  public void traverse(ASTClassExpression node) {
    //only type allowed --> check that Expression is no field or method
    //traverse the inner expression, check that it is a type (how?); the result is the type "Class"
    //can be calculated
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    SymTypeExpression innerResult = SymTypeExpressionFactory.createObscureType();

    node.getExtReturnType().accept(getTraverser());
    if(getTypeCheckResult().isPresentResult()){
      innerResult = getTypeCheckResult().getResult();
      wholeResult = SymTypeExpressionFactory.createGenerics("Class", getScope(node.getEnclosingScope()), innerResult);
    }
    if(!innerResult.isObscureType()) {
      storeResultOrLogError(wholeResult, node, "0xA0258");
    }
  }

  @Override
  public void traverse(ASTSuperExpression node) {
    //the expression before the super has to be a nested type
    //search for the enclosing type, get its super class and execute the supersuffix
    SymTypeExpression beforeSuperType = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if(!getTypeCheckResult().isType()){
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0259 the expression at source position "+node.getExpression().get_SourcePositionStart()+" has to be a type");
      return;
    }

    if(!beforeSuperType.isObscureType()) {
      SymTypeExpression wholeResult = calculateSuperExpression(node, beforeSuperType);
      storeResultOrLogError(wholeResult, node, "0xA0261");
    }
  }

  protected SymTypeExpression calculateSuperExpression(ASTSuperExpression node, SymTypeExpression beforeSuperType) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    int count = 0;
    boolean isOuterType = false;
    IBasicSymbolsScope testScope = getScope(node.getEnclosingScope());
    while (testScope!=null) {
      if(testScope.isPresentSpanningSymbol()&&testScope.getSpanningSymbol() instanceof TypeSymbol) {
        count++;
        TypeSymbol sym = (TypeSymbol) testScope.getSpanningSymbol();
        if (sym.getName().equals(beforeSuperType.getTypeInfo().getName())&&count>1) {
          isOuterType = true;
          break;
        }
      }
      testScope = testScope.getEnclosingScope();
    }

    if(isOuterType) {
      List<SymTypeExpression> superClasses = beforeSuperType.getTypeInfo().getSuperClassesOnly();
      if (superClasses.size() == 1) {
        SymTypeExpression superClass = superClasses.get(0);
        if (null != node.getSuperSuffix().getName() || !"".equals(node.getSuperSuffix().getName())) {
          ASTSuperSuffix superSuffix = node.getSuperSuffix();
          wholeResult = handleSuperSuffix(superSuffix, superClass);
        }
      }
    }
    return wholeResult;
  }

  protected SymTypeExpression handleSuperSuffix(ASTSuperSuffix superSuffix, SymTypeExpression superClass){
    if (superSuffix.isPresentArguments()) {
      //case 1 -> Expression.super.<TypeArgument>Method(Args)
      List<SymTypeExpression> typeArgsList = calculateTypeArguments(superSuffix.getExtTypeArgumentList());
      List<FunctionSymbol> methods = superClass.getMethodList(superSuffix.getName(), false);
      if (!methods.isEmpty() && null != superSuffix.getArguments()) {
        //check if the methods fit and return the right returntype
        ASTArguments args = superSuffix.getArguments();
        return checkMethodsAndReplaceTypeVariables(methods, args, typeArgsList);
      }
    }
    else {
      //case 2 -> Expression.super.Field
      List<VariableSymbol> fields = superClass.getFieldList(superSuffix.getName(), false);
      if (fields.size()==1) {
        return fields.get(0).getType();
      }else{
        getTypeCheckResult().reset();
        Log.error("0xA1305 There cannot be more than one field with the same name");
        return SymTypeExpressionFactory.createObscureType();
      }
    }
    return SymTypeExpressionFactory.createObscureType();
  }

  @Override
  public void traverse(ASTTypeCastExpression node) {
    //innerResult is the SymTypeExpression of the type that will be casted into another type
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if(getTypeCheckResult().isType()){
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0262 the expression at source position "+node.getExpression().get_SourcePositionStart()+" cannot be a type");
      return;
    }
    //castResult is the SymTypeExpression of the type the innerResult will be casted to
    SymTypeExpression castResult;

    //castResult is the type in the brackets -> (ArrayList) list
    node.getExtType().accept(getTraverser());
    if(getTypeCheckResult().isPresentResult()){
      castResult = getTypeCheckResult().getResult();
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0265 the type at source position "+node.getExtType().get_SourcePositionStart()+" cannot be calculated");
      return;
    }

    if(!innerResult.isObscureType() && !castResult.isObscureType()) {
      //wholeResult will be the result of the whole expression
      SymTypeExpression wholeResult = calculateTypeCastExpression(node, castResult, innerResult);

      storeResultOrLogError(wholeResult, node, "0xA0266");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateTypeCastExpression(ASTTypeCastExpression node, SymTypeExpression castResult, SymTypeExpression innerResult) {
    if(compatible(castResult,innerResult)|| compatible(innerResult,castResult)){
      return castResult;
    }
    return SymTypeExpressionFactory.createObscureType();
  }

  @Override
  public void traverse(ASTInstanceofExpression node) {
    SymTypeExpression expressionResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if(getTypeCheckResult().isType()) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0267 the expression at source position " + node.getExpression().get_SourcePositionStart() + " cannot be a type");
      return;
    }

    SymTypeExpression typeResult = SymTypeExpressionFactory.createObscureType();

    //calculate right type: type that the expression should be an instance of
    node.getExtType().accept(getTraverser());
    if(getTypeCheckResult().isPresentResult()){
      if(!getTypeCheckResult().isType()) {
        if(!getTypeCheckResult().getResult().isObscureType()) {
          getTypeCheckResult().reset();
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          Log.error("0xA0269 the expression at source position " + node.getExtType().get_SourcePositionStart() + " must be a type");
          return;
        }
      }else{
        typeResult = getTypeCheckResult().getResult();
      }
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      logError("0xA0270",node.getExpression().get_SourcePositionStart());
      return;
    }

    if(!expressionResult.isObscureType() && !typeResult.isObscureType()){
      //the method was not finished yet (either with Log.error or return) -> both types are present and thus the result is boolean
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createPrimitive("boolean");

      getTypeCheckResult().setResult(wholeResult);
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void traverse(ASTPrimaryThisExpression node) {
    //search for the nearest TypeSymbol and return its Type
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    Optional<TypeSymbol> typeSymbol=searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
    if(typeSymbol.isPresent()) {
      wholeResult = getResultOfPrimaryThisExpression(getScope(node.getEnclosingScope()), typeSymbol.get());
    }
    storeResultOrLogError(wholeResult, node, "0xA0272");
  }

  protected SymTypeExpression getResultOfPrimaryThisExpression(IBasicSymbolsScope scope, TypeSymbol typeSymbol) {
    SymTypeExpression wholeResult;
    if(typeSymbol.getTypeParameterList().isEmpty()){
      //if the return type is a primitive
      if(SymTypePrimitive.unboxMap.containsKey(typeSymbol.getName())){
        wholeResult = SymTypeExpressionFactory.createPrimitive(typeSymbol.getName());
      }else {
        //the return type is an object
        wholeResult = SymTypeExpressionFactory.createTypeObject(typeSymbol);
      }
    }else {
      //the return type must be a generic
      List<SymTypeExpression> typeArgs = Lists.newArrayList();
      for(TypeVarSymbol s : typeSymbol.getTypeParameterList()){
        typeArgs.add(SymTypeExpressionFactory.createTypeVariable(s));
      }
      wholeResult = SymTypeExpressionFactory.createGenerics(typeSymbol, typeArgs);
    }
    return wholeResult;
  }

  @Override
  public void traverse(ASTPrimarySuperExpression node) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();

    Optional<TypeSymbol> typeSymbol = searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
    if(typeSymbol.isPresent()) {
      List<SymTypeExpression> superClasses = typeSymbol.get().getSuperClassesOnly();
      if (superClasses.size() == 1) {
        wholeResult = superClasses.get(0);
      }
      else {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        Log.error("0xA0273 for super to work there has to be exactly one superclass");
        return;
      }
    }
    storeResultOrLogError(wholeResult, node, "0xA0280");
  }

  @Override
  public void traverse(ASTGenericInvocationExpression node) {
    //expressions of type A.B.<String>c() or A.B.<Integer>super.<String>c() plus Arguments in the brackets
    SymTypeExpression expressionResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    boolean isType = getTypeCheckResult().isType();

    if(!expressionResult.isObscureType()) {
      SymTypeExpression wholeResult = calculateGenericInvocationExpression(node, expressionResult, isType);
      storeResultOrLogError(wholeResult, node, "0xA0282");
    }
  }

  protected SymTypeExpression calculateGenericInvocationExpression(ASTGenericInvocationExpression node, SymTypeExpression expressionResult, boolean isType) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //the only case where you can calculate a result is Expression.<TypeArguments>method()
    //because the other cases of the GenericInvocationSuffix can only be calculated if the expression
    //is a PrimaryGenericInvocationExpression
    List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getPrimaryGenericInvocationExpression().getExtTypeArgumentList());

    //search in the scope of the type that before the "." for a method that has the right name
    if(node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().isPresentName()) {
      List<FunctionSymbol> methods = expressionResult.getMethodList(node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getName(),isType,false);
      //if the last result is a type then the method has to be static to be accessible
      if(isType){
        methods = filterStaticMethodSymbols(methods);
      }
      if (!methods.isEmpty() && null != node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getArguments()) {
        //check if the methods fit and return the right returntype
        ASTArguments args = node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getArguments();
        wholeResult = checkMethodsAndReplaceTypeVariables(methods, args, typeArgsList);
      }
    }
    return wholeResult;
  }

  protected List<FunctionSymbol> filterStaticMethodSymbols(List<FunctionSymbol> fittingMethods) {
    return fittingMethods.stream().filter(m -> m instanceof MethodSymbol).filter(m -> ((MethodSymbol) m).isIsStatic()).collect(Collectors.toList());
  }

  protected List<SymTypeExpression> calculateTypeArguments(List<ASTExtTypeArgumentExt> extTypeArgumentList) {
    //calculate each TypeArgument and return the results in a list
    List<SymTypeExpression> typeArgsList = Lists.newArrayList();
    for (ASTExtTypeArgumentExt astExtTypeArgumentExt : extTypeArgumentList) {
      astExtTypeArgumentExt.accept(getTraverser());
      if (getTypeCheckResult().isPresentResult()) {
        typeArgsList.add(getTypeCheckResult().getResult());
      } else {
        getTypeCheckResult().reset();
        typeArgsList.add(SymTypeExpressionFactory.createObscureType());
        Log.error("0xA0283 the type argument at source position " + astExtTypeArgumentExt.get_SourcePositionStart() + " cannot be calculated");
      }
    }
    return typeArgsList;
  }

  protected SymTypeExpression checkMethodsAndReplaceTypeVariables(List<FunctionSymbol> methods, ASTArguments args, List<SymTypeExpression> typeArgsList) {
    outer:
    for (FunctionSymbol method : methods) {
      if (method.getParameterList().size() != args.getExpressionList().size()) {
        //wrong method
        continue;
      }
      if (method.getTypeVariableList().size() != typeArgsList.size()) {
        //wrong method
        continue;
      }

      List<SymTypeExpression> argsList = calculateArguments(args);

      //method has the correct name, the correct number of type arguments and the correct amount of parameters
      //search for the right method by searching for the TypeVariables in the parameters and the return type of the methodsymbol
      //and if there is anything wrong jump to the next method -> do not change the methodsymbol
      //if everything is okay, return the return type of the method -> if this return type is a type variable return the typeArgument
      //that replaces this type variable
      Map<String, SymTypeExpression> transformMap = Maps.newHashMap();
      for (int j = 0; j < method.getTypeVariableList().size(); j++) {
        transformMap.put(method.getTypeVariableList().get(j).getName(), typeArgsList.get(j));
      }

      for (int j = 0; j < method.getParameterList().size(); j++) {
        VariableSymbol param = method.getParameterList().get(j);
        if (param.getType().isTypeVariable()) {
          if (!transformMap.containsKey(param.getType().print())) {
            //there is a typevariable that cannot be resolved to the correct type -> wrong method
            continue outer;
          }
          if (!argsList.get(j).deepEquals(transformMap.get(param.getType().print())) && !compatible(transformMap.get(param.getType().print()), argsList.get(j))) {
            continue outer;
          }
        } else {
          if (!argsList.get(j).deepEquals(param.getType()) && !compatible(param.getType(), argsList.get(j))) {
            continue outer;
          }
        }
      }
      if (method.getType().isTypeVariable()) {
        if (transformMap.containsKey(method.getType().print())) {
          return transformMap.get(method.getType().print());
        }
      } else {
        return method.getType();
      }
    }
    //there cannot be found a fitting method
    return SymTypeExpressionFactory.createObscureType();
  }

  protected List<SymTypeExpression> calculateArguments(ASTArguments args) {
    List<SymTypeExpression> argList = Lists.newArrayList();
    for(int i = 0;i<args.getExpressionList().size();i++){
      args.getExpression(i).accept(getTraverser());
      if(getTypeCheckResult().isPresentResult()){
        if(!getTypeCheckResult().isType()){
          argList.add(getTypeCheckResult().getResult());
        }
      }else{
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA0284",args.getExpressionList().get(i).get_SourcePositionStart());
      }
    }
    return argList;
  }

  @Override
  public void traverse(ASTPrimaryGenericInvocationExpression node) {
    //expressions of the type <String>c() or <String>super.<Integer>c() plus Arguments in the brackets
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();

    if(!node.getGenericInvocationSuffix().isPresentSuperSuffix()){
      if(node.getGenericInvocationSuffix().isPresentName()){
        //case 1: <TypeVariable>method(Args) -> similar to GenericInvocationExpression
        //can be accessed solely or after another expression -> check if lastResult is present
        IBasicSymbolsScope testScope;
        if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()){
          testScope = getTypeCheckResult().getResult().getTypeInfo().getSpannedScope();
        }else{
          testScope = getScope(node.getEnclosingScope());
        }
        //resolve for fitting methods
        List<FunctionSymbol> methods = testScope.resolveFunctionMany(node.getGenericInvocationSuffix().getName());
        if(!methods.isEmpty() && node.getGenericInvocationSuffix().isPresentArguments()){
          //check if the methods fit and return the right returntype
          ASTArguments args = node.getGenericInvocationSuffix().getArguments();
          List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getExtTypeArgumentList());
          if(!typeArgsList.isEmpty()){
            getTypeCheckResult().unsetType();
          }
          wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
        }
      }else{
        //case 2: <TypeVariable>this(Args) -> similar to PrimaryThisExpression, use method checkMethodsAndReplaceTypeVariables
        //can only be accessed solely -> there cannot be a lastresult
        //search for the nearest enclosingscope spanned by a typesymbol
        Optional<TypeSymbol> typeSymbol = searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
        if(typeSymbol.isPresent()) {
          //get the constructors of the typesymbol
          List<FunctionSymbol> methods = typeSymbol.get().getSpannedScope().resolveFunctionMany(typeSymbol.get().getName());
          if (!methods.isEmpty() && null != node.getGenericInvocationSuffix().getArguments()) {
            //check if the constructors fit and return the right returntype
            ASTArguments args = node.getGenericInvocationSuffix().getArguments();
            List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getExtTypeArgumentList());
            wholeResult = checkMethodsAndReplaceTypeVariables(methods, args, typeArgsList);
          }
        }
      }
    }else{
      ASTSuperSuffix superSuffix = node.getGenericInvocationSuffix().getSuperSuffix();
      if(!superSuffix.isPresentName()){
        //case 3: <TypeVariable>super(Args) -> find the constructor of the super class, use method checkMethodsAndReplaceTypeVariables
        //search for the nearest enclosingscope spanned by a typesymbol
        Optional<TypeSymbol> subType = searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
        //get the superclass of this typesymbol and search for its fitting constructor
        if(subType.isPresent() &&subType.get().getSuperClassesOnly().size()==1){
          SymTypeExpression superClass = subType.get().getSuperClassesOnly().get(0);
          List<FunctionSymbol> methods = superClass.getMethodList(superClass.getTypeInfo().getName(), false);
          if(!methods.isEmpty() && superSuffix.isPresentArguments()){
            //check if the constructors fit and return the right returntype
            ASTArguments args = superSuffix.getArguments();
            List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getExtTypeArgumentList());
            wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
          }
        }
      }
    }

    storeResultOrLogError(wholeResult, node, "0xA0285");
  }

  protected Optional<TypeSymbol> searchForTypeSymbolSpanningEnclosingScope(IBasicSymbolsScope scope) {
    //search for the nearest type symbol in the enclosing scopes -> for this and super to get the
    //current object
    while(scope!=null){
      if(scope.isPresentSpanningSymbol()&&scope.getSpanningSymbol() instanceof OOTypeSymbol){
        return Optional.of((OOTypeSymbol)scope.getSpanningSymbol());
      }
      scope = scope.getEnclosingScope();
    }
    //no typesymbol found
    return Optional.empty();
  }

  @Override
  public void traverse(ASTAnonymousClass creator){
    SymTypeExpression extType;
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    creator.getExtType().accept(getTraverser());
    if(getTypeCheckResult().isPresentResult()){
      extType = getTypeCheckResult().getResult();
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(wholeResult);
      logError("0xA1311",creator.getExtType().get_SourcePositionStart());
      return;
    }

    if(!extType.isObscureType()) {

      if (!extType.isPrimitive()) {
        //see if there is a constructor fitting for the arguments
        List<FunctionSymbol> constructors = extType.getMethodList(extType.getTypeInfo().getName(), false);
        if (!constructors.isEmpty()) {
          if (testForCorrectArguments(constructors, creator.getArguments())) {
            wholeResult = extType;
          }
        } else if (creator.getArguments().isEmptyExpressions()) {
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
  public void traverse(ASTArrayCreator creator){
    SymTypeExpression extTypeResult;
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();

    creator.getExtType().accept(getTraverser());
    if(getTypeCheckResult().isPresentResult()){
      extTypeResult = getTypeCheckResult().getResult();
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(wholeResult);
      logError("0xA0314", creator.getExtType().get_SourcePositionStart());
      return;
    }

    if(!extTypeResult.isObscureType()) {
      //the definition of the Arrays are based on the assumption that ExtType is not an array
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
              } else {
                getTypeCheckResult().reset();
                getTypeCheckResult().setResult(wholeResult);
                logError("0xA0316", expr.get_SourcePositionStart());
                return;
              }
            } else {
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
    for(int i = 0;i<args.getExpressionList().size();i++){
      args.getExpression(i).accept(getTraverser());
      if(getTypeCheckResult().isPresentResult()){
        argList.add(getTypeCheckResult().getResult());
      }else{
        getTypeCheckResult().reset();
        argList.add(SymTypeExpressionFactory.createObscureType());
        logError("0xA0313",args.getExpressionList().get(i).get_SourcePositionStart());
      }
    }
    return argList;
  }

  protected boolean testForCorrectArguments(List<FunctionSymbol> constructors, ASTArguments arguments) {
    List<SymTypeExpression> symTypeOfArguments = calculateCorrectArguments(arguments);
    outer: for(FunctionSymbol constructor: constructors){
      if(constructor.getParameterList().size() == symTypeOfArguments.size()){
        //get the types of the constructor arguments
        List<SymTypeExpression> constructorArguments = constructor.getParameterList().stream().map(VariableSymbol::getType).collect(Collectors.toList());
        for(int i = 0;i<constructorArguments.size();i++){
          if(!compatible(constructorArguments.get(i),symTypeOfArguments.get(i))){
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
}
