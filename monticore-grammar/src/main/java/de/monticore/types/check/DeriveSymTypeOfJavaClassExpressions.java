package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
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
    //TODO:#2465 -> vervollstaendigen
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
    //TODO:#2465 -> vervollstaendigen
    //only type allowed --> check that Expression is no field or method
    //traverse the inner expression, check that it is a type (how?); the result is the type "Class"
    //can be calculated
    SymTypeExpression wholeResult = null;
    SymTypeExpression innerResult;

    //TODO: traverse method for external return type -> synthesizer that must be given to this class when initialising the TypeCheck or (perhaps better) that can be added to the Delegator
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
    //TODO:#2465 -> vervollstaendigen
    //TODO: return the super type of the prior qualified name and execute the methods/expressions after the ".super"
    //can be calculated

    SymTypeExpression beforeSuperType = null;
    SymTypeExpression superSuffixType = null;

    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      beforeSuperType = lastResult.getLast();
    }else{
      Log.error(/*TODO*/"The result of the expression before the .super cannot be calculated");
    }

    node.getSuperSuffix().accept(getRealThis());
    if(lastResult.isPresentLast()){
      superSuffixType = lastResult.getLast();
    }else{
      Log.error(/*TODO*/"The result of the expression before the .super cannot be calculated");
    }

    //TODO wie geht es weiter? kombinieren von beiden expressions?


    lastResult.reset();
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

    //TODO: traverse method for external ExtType
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
    //TODO: test that the left expression is not a type
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
    //TODO: traverse method for external exttype
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
    SymTypeExpression wholeResult = null;
    if(scope.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = scope.getSpanningSymbol();
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        if(typeSymbol.getTypeParameterList().isEmpty()){
          wholeResult = SymTypeExpressionFactory.createTypeObject(typeSymbol.getName(),scope);
        }else{
          wholeResult = getResultOfPrimaryThisExpression(typeSymbol);
        }
      }else if(spanningSymbol instanceof MethodSymbol) {
        //if the scope is spanned by a method, the enclosing scope must be the scope spanned by the type
        if (null != scope.getEnclosingScope()) {
          IScopeSpanningSymbol innerSpanningSymbol = scope.getEnclosingScope().getSpanningSymbol();
          if (innerSpanningSymbol instanceof TypeSymbol) {
            TypeSymbol typeSymbol = (TypeSymbol) innerSpanningSymbol;
            wholeResult = getResultOfPrimaryThisExpression(typeSymbol);
          }else {
            lastResult.reset();
            Log.error("0xA0261 the spanning symbol of the enclosing scope has to be a type symbol");
          }
        }else {
          lastResult.reset();
          Log.error("0xA0268 the enclosing scope of the actual scope must not be null");
        }
      }else {
        lastResult.reset();
        Log.error("0xA0262 the spanning symbol of the scope has to be either a method symbol or a type symbol");
      }
    }else{
      lastResult.reset();
      Log.error("0xA0263 the scope has to be spanned by a symbol");
    }
    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0x0264 the resulting type of the PrimaryThisExpression cannot be calculated");
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
    if(scope.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = scope.getSpanningSymbol();
      if(spanningSymbol instanceof TypeSymbol){
        //if the scope is spanned by a type, search for the supertype and return it
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        if(typeSymbol.getSuperClassesOnly().size()>1){
          lastResult.reset();
          Log.error("0xA0252 In Java there cannot be more than one super class");
        }else if(typeSymbol.getSuperClassesOnly().isEmpty()){
          lastResult.reset();
          Log.error("0xA0253 No supertype could be found");
        }else{
          wholeResult = typeSymbol.getSuperType(0);
        }
      }else if(spanningSymbol instanceof MethodSymbol){
        //if the scope is spanned by a method, the enclosing scope must be the scope spanned by the type
        if(null!=scope.getEnclosingScope()){
          IScopeSpanningSymbol innerSpanningSymbol = scope.getEnclosingScope().getSpanningSymbol();
          if(innerSpanningSymbol instanceof TypeSymbol){
            //search for the supertype of the type and return it
            TypeSymbol typeSymbol = (TypeSymbol) innerSpanningSymbol;
            if(typeSymbol.getSuperClassesOnly().size()>1){
              lastResult.reset();
              Log.error("0xA0254 In Java there cannot be more than one super class");
            }else if(typeSymbol.getSuperClassesOnly().isEmpty()){
              lastResult.reset();
              Log.error("0xA0255 No supertype could be found");
            }else{
              wholeResult = typeSymbol.getSuperClassesOnly().get(0);
            }
          }else{
            lastResult.reset();
            Log.error("0xA0258 the spanning symbol of the enclosing scope has to be a type symbol");
          }
        }else{
          lastResult.reset();
          Log.error("0xA0259 there has to be an enclosing scope present");
        }
      }else{
        lastResult.reset();
        Log.error("0xA0256 the scope must either be spanned by a type or a method");
      }
    }else{
      lastResult.reset();
      Log.error("0xA0257 the scope has to be spanned by a symbol");
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
    //TODO:#2465 -> vervollstaendigen

    //expressions of type A.B.<String>c() or A.B.<Integer>super.<String>c() plus Arguments in the brackets
    SymTypeExpression expressionResult = null;
    SymTypeExpression primaryGenericResult = null;

    node.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      expressionResult = lastResult.getLast();
    }else{
      Log.error("0xA0320 the result of the left expression of the GenericInvocationExpression cannot be calculated");
    }

    node.getPrimaryGenericInvocationExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      primaryGenericResult = lastResult.getLast();
    }else{
      Log.error("0xA0321 the result of the PrimaryGenericInvocationExpression of the GenericInvocationExpression cannot be calculated");
    }



    lastResult.reset();
    Log.error("0xA0304 the result of the GenericInvocationExpression cannot be calculated");
  }

  @Override
  public void traverse(ASTPrimaryGenericInvocationExpression node) {
    //TODO:#2465 -> vervollstaendigen

    //expressions of the type <String>c() or <String>super.<Integer>c() plus Arguments in the brackets

    SymTypeExpression typeArgumentResult = null;
    SymTypeExpression expressionResult = null;

    node.getGenericInvocationSuffix().accept(getRealThis());
    if(lastResult.isPresentLast()){
      expressionResult = lastResult.getLast();
    }else{
      Log.error("0xA0323 the result of the InvocationSuffix of the GenericInvocationExpression cannot be calculated");
    }

    node.getExtTypeArguments().accept(getRealThis());
    if(lastResult.isPresentLast()){
      typeArgumentResult = lastResult.getLast();
    }else{
      Log.error("0xA0322 the result of the TypeArgument of the GenericInvocationExpression cannot be calculated");
    }

    //test that the arguments fit to the result of the genericinvocationsuffix

    lastResult.reset();
    Log.error("0xA0305 the result of the PrimaryGenericInvocationExpression cannot be calculated");
  }

  @Override
  public void traverse(ASTGenericInvocationSuffix node){
    //three cases: 1) SuperSuffix is present -> traverse method of SuperSuffix -> transformation (super.SuperSuffix)
    //2) Name is present -> Arguments have to be present or empty -> transformation (Method Name(Arguments))
    //3) Name is not present -> Arguments have to be present or empty -> transformation (Constructor this(Arguments)) -> return the current type
    SymTypeExpression wholeResult = null;
    //case 1)
    if(null!=node.getSuperSuffix()){
      SymTypeExpression superSuffixResult = null;
      node.getSuperSuffix().accept(getRealThis());
      if(lastResult.isPresentLast()){
        superSuffixResult = lastResult.getLast();
      }else{
        Log.error("" /*TODO*/);
      }
      wholeResult = superSuffixResult;
    }
    //case 2) similar to CallExpression
    else if(null != node.getName() && !"".equals(node.getName())) {
      if (lastResult.isPresentLast()) {
        List<MethodSymbol> methods = lastResult.getLast().getTypeInfo().getSpannedScope().resolveMethodMany(node.getName());
        if(!methods.isEmpty()){
          //find the right method if present and return it
          Optional<MethodSymbol> method = testArgumentsAndReturnReturnType(methods,node.getArguments().getExpressionList());
          if(method.isPresent()){
            wholeResult=method.get().getReturnType();
          }
        }else{
          Log.error(""/*TODO*/);
        }
      }else{
        List<MethodSymbol> methods = scope.resolveMethodMany(node.getName());
        if(!methods.isEmpty()){
          //find the right method if present and return it
          Optional<MethodSymbol> method = testArgumentsAndReturnReturnType(methods,node.getArguments().getExpressionList());
          if(method.isPresent()){
            wholeResult=method.get().getReturnType();
          }
        }else{
          Log.error(""/*TODO*/);
        }
      }
    }
    //case 3)
    else if(null == node.getName() || "".equals(node.getName())){
      if(lastResult.isPresentLast()){
        List<MethodSymbol> methods = lastResult.getLast().getTypeInfo().getSpannedScope().resolveMethodMany("this");
        Optional<MethodSymbol> method = testArgumentsAndReturnReturnType(methods,node.getArguments().getExpressionList());
        if(method.isPresent()){
          wholeResult = method.get().getReturnType();
        }else{
          Log.error(""/*TODO*/);
        }
      }else{
        List<MethodSymbol> methods = scope.resolveMethodMany("this");
        Optional<MethodSymbol> method = testArgumentsAndReturnReturnType(methods,node.getArguments().getExpressionList());
        if(method.isPresent()){
          wholeResult = method.get().getReturnType();
        }else{
          Log.error(""/*TODO*/);
        }
      }
    }

    if(wholeResult!=null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error(/*TODO*/"the result of the GenericInvocationSuffix cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTSuperSuffix node){
    SymTypeExpression wholeResult = null;
    //two cases: 1) only Arguments present -> constructor super(Arguments) -> return the super type
    if(null==node.getName()||"".equals(node.getName())) {
      //lastResult has to be present, there is always an expression before supersuffix
      if (lastResult.isPresentLast()) {
        List<SymTypeExpression> superClasses = lastResult.getLast().getTypeInfo().getSuperClassesOnly();
        if (superClasses.size() == 1) {
          //find the constructor(s) of the super class
          List<MethodSymbol> methods = lastResult.getLast().getTypeInfo().getSpannedScope().resolveMethodMany(lastResult.getLast().getTypeInfo().getName());
          //test if the arguments of the constructor and the arguments of the ASTSuperSuffix are compatible
          if(testArguments(methods, node.getArguments().getExpressionList())){
            wholeResult = superClasses.get(0);
          }
        }
        else {
          Log.error(/*TODO*/"there are either 0 or more than 1 super classes of " + lastResult.getLast().getTypeInfo().getName());
        }
      }else{

        //TODO what if lastresult is empty? is that even possible?
      }
    }else{
      //2) Name present -> ExtTypeArguments and Arguments can be present(optional) -> method super.<ExtTypeArguments>Name(Arguments)
      //if no arguments present then there cannot be ExtTypeArguments present -> field in super class
      //if arguments present then there can be ExtTypeArguments present, but they do not have to be present -> method with or without type variables
      //get the result of the ExtTypeArguments and Arguments if present -> then test the arguments with the method and fill in the actual types if there are any typevariables
      //similar to PrimaryGenericInvocationExpression
    }

    if(wholeResult != null){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error(/*TODO*/"the result of the SuperSuffix cannot be calculated");
    }
  }

  private boolean testArguments(List<MethodSymbol> methods, List<ASTExpression> parameters){
    List<SymTypeExpression> symTypeExpressions = calculateArguments(parameters);

    boolean isCorrect = false;

    outer:for(MethodSymbol method: methods){
      if(method.getParameterList().size() != symTypeExpressions.size()) {
        continue;
      }
      for(int i = 0;i<method.getParameterList().size();i++){
        if(!symTypeExpressions.get(i).isTypeVariable()){
          if(!symTypeExpressions.get(i).getTypeInfo().getName().equals(
              method.getParameter(i).getType().getTypeInfo().getName())){
            continue outer;
          }
        }else{
          //if the symTypeExpression is a typeVariable then it is either specified in the method itself or the enclosing type
          boolean isTypeVar = false;
          for(TypeVarSymbol typeVar : method.getTypeVariableList()){
            if(typeVar.getName().equals(symTypeExpressions.get(i).getTypeInfo().getName())){
              isTypeVar = true;
            }
          }
          if(method.getEnclosingScope()!=null){
            if(method.getEnclosingScope().isPresentSpanningSymbol()){
              IScopeSpanningSymbol scopeSpanningSymbol = method.getEnclosingScope().getSpanningSymbol();
              if(scopeSpanningSymbol instanceof TypeSymbol){
                TypeSymbol type = (TypeSymbol) scopeSpanningSymbol;
                for(TypeVarSymbol typeVar: type.getTypeParameterList()){
                  if(typeVar.getName().equals(symTypeExpressions.get(i).getTypeInfo().getName())){
                    isTypeVar = true;
                  }
                }
              }
            }
          }
          if(!isTypeVar){
            continue outer;
          }
        }
        if(i == symTypeExpressions.size()-1){
          isCorrect = true;
        }
      }
    }
    return isCorrect;
  }

  private Optional<MethodSymbol> testArgumentsAndReturnReturnType(List<MethodSymbol> methods ,List<ASTExpression> parameters){
    List<SymTypeExpression> symTypeExpressions = calculateArguments(parameters);

    outer:for(MethodSymbol method: methods){
      if(method.getParameterList().size() != symTypeExpressions.size()) {
        continue;
      }
      for(int i = 0;i<method.getParameterList().size();i++){
        if(!symTypeExpressions.get(i).isTypeVariable()){
          if(!symTypeExpressions.get(i).getTypeInfo().getName().equals(
              method.getParameter(i).getType().getTypeInfo().getName())){
            continue outer;
          }
        }else{
          //if the symTypeExpression is a typeVariable then it is either specified in the method itself or the enclosing type
          boolean isTypeVar = false;
          for(TypeVarSymbol typeVar : method.getTypeVariableList()){
            if(typeVar.getName().equals(symTypeExpressions.get(i).getTypeInfo().getName())){
              isTypeVar = true;
            }
          }
          if(method.getEnclosingScope()!=null){
            if(method.getEnclosingScope().isPresentSpanningSymbol()){
              IScopeSpanningSymbol scopeSpanningSymbol = method.getEnclosingScope().getSpanningSymbol();
              if(scopeSpanningSymbol instanceof TypeSymbol){
                TypeSymbol type = (TypeSymbol) scopeSpanningSymbol;
                for(TypeVarSymbol typeVar: type.getTypeParameterList()){
                  if(typeVar.getName().equals(symTypeExpressions.get(i).getTypeInfo().getName())){
                    isTypeVar = true;
                  }
                }
              }
            }
          }
          if(!isTypeVar){
            continue outer;
          }
        }
      }
      return Optional.of(method);
    }
    return Optional.empty();
  }

  public List<SymTypeExpression> calculateArguments(List<ASTExpression> parameters){
    List<SymTypeExpression> symTypeExpressions = Lists.newArrayList();
    for(int i = 0;i<parameters.size();i++){
      parameters.get(i).accept(getRealThis());
      if(lastResult.isPresentLast()){
        symTypeExpressions.add(lastResult.getLast());
      }else{
        Log.error(/*TODO*/"the result of the "+i+". parameter cannot be calculated");
      }
    }
    return symTypeExpressions;
  }
}
