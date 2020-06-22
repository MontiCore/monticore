/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor;
import de.monticore.types.basictypesymbols._symboltable.TypeVarSymbol;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.types.check.TypeCheck.compatible;

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
    if(typeCheckResult.isPresentLast()){
      innerResult = typeCheckResult.getLast();
    }else{
      logError("0xA0251",node.getExpression().get_SourcePositionStart());
    }

    //check recursively until there is no enclosing scope or the spanningsymbol of the scope is a type
    //while the enclosing scope is not null, it is possible that the expression can be calculated
    int count = 0;
    if(typeCheckResult.isType()) {
      if(getScope(node.getEnclosingScope()).getEnclosingScope()!=null){
        ITypeSymbolsScope testScope = getScope(node.getEnclosingScope());
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
    }

    if(wholeResult!=null){
      typeCheckResult.setLast(wholeResult);
    }else {
      typeCheckResult.reset();
      logError("0xA0252",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTArrayExpression node) {
    SymTypeExpression indexResult = null;
    SymTypeExpression arrayTypeResult = null;
    SymTypeExpression wholeResult = null;

    //cannot be a type and has to be a integer value
    node.getIndexExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      if (!typeCheckResult.isType()) {
        indexResult = typeCheckResult.getLast();
      }else{
        typeCheckResult.reset();
        Log.error("0xA0253 the expression at source position"+node.getIndexExpression().get_SourcePositionStart()+" cannot be a type");
      }
    }else{
      logError("0xA0254",node.get_SourcePositionStart());
    }

    node.getExpression().accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      if(typeCheckResult.isType()){
        Log.error("0xA0255 the expression at source position "+node.getExpression().get_SourcePositionStart()+" cannot be a type");
      }
      arrayTypeResult = typeCheckResult.getLast();
    }else{
      logError("0xA0256",node.get_SourcePositionStart());
    }

    //the type of the index has to be an integral type
    if(indexResult.isTypeConstant() && ((SymTypeConstant)indexResult).isIntegralType() && arrayTypeResult instanceof SymTypeArray){
      SymTypeArray arrayResult = (SymTypeArray) arrayTypeResult;
      wholeResult = getCorrectResultArrayExpression(node.getEnclosingScope(), indexResult, arrayTypeResult, arrayResult);
    }

    //if nothing found -> fail
    if(wholeResult!=null){
      typeCheckResult.setLast(wholeResult);
    }else {
      typeCheckResult.reset();
      logError("0xA0257",node.get_SourcePositionStart());
    }
  }

  private SymTypeExpression getCorrectResultArrayExpression(IExpressionsBasisScope scope, SymTypeExpression indexResult, SymTypeExpression arrayTypeResult, SymTypeArray arrayResult) {
    SymTypeExpression wholeResult;
    if(arrayResult.getDim()>1){
      //case 1: A[][] bar -> bar[3] returns the type A[] -> decrease the dimension of the array by 1
      wholeResult = SymTypeExpressionFactory.createTypeArray(arrayTypeResult.typeSymbolSurrogate.getName(),getScope(scope),arrayResult.getDim()-1,indexResult);
    }else {
      //case 2: A[] bar -> bar[3] returns the type A
      //determine whether the result has to be a constant, generic or object
      if(arrayResult.getTypeInfo().getTypeParameterList().isEmpty()){
        //if the return type is a primitive
        if(SymTypeConstant.boxMap.containsKey(arrayResult.getTypeInfo().getName())){
          wholeResult = SymTypeExpressionFactory.createTypeConstant(arrayResult.getTypeInfo().getName());
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

  private SymTypeExpression replaceTypeVariables(SymTypeExpression wholeResult, List<SymTypeExpression> typeArgs, List<SymTypeExpression> argumentList) {
    Map<SymTypeExpression,SymTypeExpression> map = Maps.newHashMap();
    if(typeArgs.size()!=argumentList.size()){
      Log.error("0xA0297 different amount of type variables and type arguments");
    }else{
      for(int i = 0;i<typeArgs.size();i++){
        map.put(typeArgs.get(i),argumentList.get(i));
      }

      List<SymTypeExpression> oldArgs = ((SymTypeOfGenerics) wholeResult).getArgumentList();
      List<SymTypeExpression> newArgs = Lists.newArrayList();
      for(int i = 0;i<oldArgs.size();i++){
        if(map.containsKey(oldArgs.get(i))){
          newArgs.add(map.get(oldArgs.get(i)));
        }else{
          newArgs.add(oldArgs.get(i));
        }
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
    SymTypeExpression wholeResult = null;
    SymTypeExpression innerResult;

    node.getExtReturnType().accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      innerResult = typeCheckResult.getLast();
      wholeResult = SymTypeExpressionFactory.createGenerics("Class",getScope(node.getEnclosingScope()),innerResult);
    }
    if(wholeResult!=null){
      typeCheckResult.setLast(wholeResult);
    }else {
      typeCheckResult.reset();
      logError("0xA0258",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTSuperExpression node) {
    //the expression before the super has to be a nested type
    //search for the enclosing type, get its super class and execute the supersuffix


    SymTypeExpression beforeSuperType = null;
    SymTypeExpression wholeResult = null;

    node.getExpression().accept(getRealThis());
    if(typeCheckResult.isPresentLast()) {
      if (typeCheckResult.isType()){
        beforeSuperType = typeCheckResult.getLast();
      }else {
        Log.error("0xA0259 the expression at source position "+node.getExpression().get_SourcePositionStart()+" has to be a type");
      }
    }else{
      logError("0xA0260",node.getExpression().get_SourcePositionStart());
    }

    int count = 0;
    boolean isOuterType = false;
    ITypeSymbolsScope testScope = getScope(node.getEnclosingScope());
    while (testScope!=null) {
      if(testScope.isPresentSpanningSymbol()&&testScope.getSpanningSymbol() instanceof OOTypeSymbol) {
        count++;
        OOTypeSymbol sym = (OOTypeSymbol) testScope.getSpanningSymbol();
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
          if (superSuffix.isPresentArguments()) {
            //case 1 -> Expression.super.<TypeArgument>Method(Args)
            List<SymTypeExpression> typeArgsList = calculateTypeArguments(superSuffix.getExtTypeArgumentList());
            List<MethodSymbol> methods = superClass.getMethodList(superSuffix.getName());
            if (!methods.isEmpty() && null != superSuffix.getArguments()) {
              //check if the methods fit and return the right returntype
              ASTArguments args = superSuffix.getArguments();
              wholeResult = checkMethodsAndReplaceTypeVariables(methods, args, typeArgsList);
            }
          }
          else {
            //case 2 -> Expression.super.Field
            List<FieldSymbol> fields = superClass.getFieldList(superSuffix.getName());
            if (fields.size()==1) {
              wholeResult = fields.get(0).getType();
            }else{
              Log.error("0xA0304 there cannot be more than one field with the same name");
            }
          }
        }
      }
    }
    if(wholeResult!=null){
      typeCheckResult.setLast(wholeResult);
    }else {
      typeCheckResult.reset();
      logError("0xA0261",node.get_SourcePositionStart());
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
    if(typeCheckResult.isPresentLast()){
      innerResult = typeCheckResult.getLast();
      if(typeCheckResult.isType()){
        typeCheckResult.reset();
        Log.error("0xA0262 the expression at source position "+node.getExpression().get_SourcePositionStart()+" cannot be a type");
      }
    }else{
      logError("0xA0263",node.getExpression().get_SourcePositionStart());
    }

    //castResult is the type in the brackets -> (ArrayList) list
    node.getExtType().accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      castResult = typeCheckResult.getLast();
    }else{
      Log.error("0xA0265 the type at source position "+node.getExtType().get_SourcePositionStart()+" cannot be calculated");
    }

    if(compatible(castResult,innerResult)|| compatible(innerResult,castResult)){
      wholeResult = castResult.deepClone();
    }

    if(null!=wholeResult){
      typeCheckResult.setLast(wholeResult);
    }else{
      typeCheckResult.reset();
      logError("0xA0266",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTInstanceofExpression node) {
    SymTypeExpression wholeResult = null;

    //calculate left type: expression that is to be checked for a specific type
    node.getExpression().accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      if(typeCheckResult.isType()){
        typeCheckResult.reset();
        Log.error("0xA0267 the expression at source position "+node.getExpression().get_SourcePositionStart()+" cannot be a type");
      }
    }else{
      typeCheckResult.reset();
      logError("0xA0268",node.getExpression().get_SourcePositionStart());
    }

    //calculate right type: type that the expression should be an instance of
    node.getExtType().accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      if(!typeCheckResult.isType()) {
        typeCheckResult.reset();
        Log.error("0xA0269 the expression at source position "+node.getExtType().get_SourcePositionStart()+" must be a type");
      }
    }else{
      typeCheckResult.reset();
      logError("0xA0270",node.getExpression().get_SourcePositionStart());
    }

    wholeResult = SymTypeExpressionFactory.createTypeConstant("boolean");

    if(null!=wholeResult){
      typeCheckResult.setLast(wholeResult);
    }else{
      typeCheckResult.reset();
      logError("0xA0271",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTPrimaryThisExpression node) {
    //search for the nearest TypeSymbol and return its Type
    SymTypeExpression wholeResult = null;
    OOTypeSymbol typeSymbol=searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
    if(typeSymbol!=null) {
      wholeResult = getResultOfPrimaryThisExpression(getScope(node.getEnclosingScope()), typeSymbol);
    }
    if(null!=wholeResult){
      typeCheckResult.setLast(wholeResult);
    }else{
      typeCheckResult.reset();
      logError("0xA0272",node.get_SourcePositionStart());
    }
  }

  private SymTypeExpression getResultOfPrimaryThisExpression(ITypeSymbolsScope scope, OOTypeSymbol typeSymbol) {
    SymTypeExpression wholeResult;
    if(typeSymbol.getTypeParameterList().isEmpty()){
      //if the return type is a primitive
      if(SymTypeConstant.unboxMap.containsKey(typeSymbol.getName())){
        wholeResult = SymTypeExpressionFactory.createTypeConstant(typeSymbol.getName());
      }else {
        //the return type is an object
        wholeResult = SymTypeExpressionFactory.createTypeObject(typeSymbol.getName(), typeSymbol.getEnclosingScope());
      }
    }else {
      //the return type must be a generic
      List<SymTypeExpression> typeArgs = Lists.newArrayList();
      for(TypeVarSymbol s : typeSymbol.getTypeParameterList()){
        typeArgs.add(SymTypeExpressionFactory.createTypeVariable(s.getName(),typeSymbol.getEnclosingScope()));
      }
      wholeResult = SymTypeExpressionFactory.createGenerics(typeSymbol.getName(), typeSymbol.getEnclosingScope(), typeArgs);
    }
    return wholeResult;
  }

  @Override
  public void traverse(ASTPrimarySuperExpression node) {
    SymTypeExpression wholeResult=null;

    OOTypeSymbol typeSymbol = searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
    if(typeSymbol!=null) {
      if (typeSymbol.getSuperClassesOnly().size() == 1) {
        wholeResult = typeSymbol.getSuperClassesOnly().get(0);
      }
      else {
        Log.error("0xA0273 for super to work there has to be exactly one superclass");
      }
    }
    if(wholeResult!=null){
      typeCheckResult.setLast(wholeResult);
    }else{
      typeCheckResult.reset();
      logError("0xA0280",node.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTGenericInvocationExpression node) {
    //expressions of type A.B.<String>c() or A.B.<Integer>super.<String>c() plus Arguments in the brackets
    SymTypeExpression expressionResult = null;
    SymTypeExpression wholeResult = null;
    boolean isType = false;

    node.getExpression().accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      if(typeCheckResult.isType()){
       isType = true;
      }
      expressionResult = typeCheckResult.getLast();
    }else{
      logError("0xA0281",node.getExpression().get_SourcePositionStart());
    }

    //the only case where you can calculate a result is Expression.<TypeArguments>method()
    //because the other cases of the GenericInvocationSuffix can only be calculated if the expression
    //is a PrimaryGenericInvocationExpression

    List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getPrimaryGenericInvocationExpression().getExtTypeArgumentList());


    //search in the scope of the type that before the "." for a method that has the right name
    if(node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().isPresentName()) {
      List<MethodSymbol> methods = expressionResult.getMethodList(node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getName(),isType);
      //if the last result is a type then the method has to be static to be accessible
      if(isType){
        methods = filterStaticMethods(methods);
      }
      if (!methods.isEmpty() && null != node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getArguments()) {
        //check if the methods fit and return the right returntype
        ASTArguments args = node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getArguments();
        wholeResult = checkMethodsAndReplaceTypeVariables(methods, args, typeArgsList);
      }
    }

    if(wholeResult!=null){
      typeCheckResult.setLast(wholeResult);
    }else {
      typeCheckResult.reset();
      logError("0xA0282",node.get_SourcePositionStart());
    }
  }

  private List<MethodSymbol> filterStaticMethods(List<MethodSymbol> methods) {
    return methods.stream().filter(MethodSymbol::isIsStatic).collect(Collectors.toList());
  }


  private List<SymTypeExpression> calculateTypeArguments(List<ASTExtTypeArgumentExt> extTypeArgumentList) {
    //calculate each TypeArgument and return the results in a list
    List<SymTypeExpression> typeArgsList = Lists.newArrayList();
    for(int i = 0;i<extTypeArgumentList.size();i++){
      extTypeArgumentList.get(i).accept(getRealThis());
      if(typeCheckResult.isPresentLast()){
        typeArgsList.add(typeCheckResult.getLast());
      }else{
        Log.error("0xA0283 the type argument at source position "+extTypeArgumentList.get(i).get_SourcePositionStart()+" cannot be calculated");
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

      List<SymTypeExpression> argsList = calculateArguments(args);

      //method has the correct name, the correct number of type arguments and the correct amount of parameters
      //search for the right method by searching for the TypeVariables in the parameters and the return type of the methodsymbol
      //and if there is anything wrong jump to the next method -> do not change the methodsymbol
      //if everything is okay, return the return type of the method -> if this return type is a type variable return the typeArgument
      //that replaces this type variable
      Map<String,SymTypeExpression> transformMap = Maps.newHashMap();
      for(int j = 0;j<method.getTypeVariableList().size();j++){
        transformMap.put(method.getTypeVariableList().get(j).getName(),typeArgsList.get(j));
      }

      for(int j = 0;j<method.getParameterList().size();j++){
        FieldSymbol param = method.getParameterList().get(j);
        if(param.getType().isTypeVariable()){
          if(!transformMap.containsKey(param.getType().print())){
            //there is a typevariable that cannot be resolved to the correct type -> wrong method
            continue outer;
          }
          if(!argsList.get(j).deepEquals(transformMap.get(param.getType().print()))&&!compatible(transformMap.get(param.getType().print()),argsList.get(j))){
            continue outer;
          }
        }else{
          if(!argsList.get(j).deepEquals(param.getType())&&!compatible(param.getType(),argsList.get(j))){
            continue outer;
          }
        }
      }
      if(method.getReturnType().isTypeVariable()){
        if(transformMap.containsKey(method.getReturnType().print())){
          return transformMap.get(method.getReturnType().print());
        }
      }else{
        return method.getReturnType();
      }
    }
    //there cannot be found a fitting method
    return null;
  }

  private List<SymTypeExpression> calculateArguments(ASTArguments args) {
    List<SymTypeExpression> argList = Lists.newArrayList();
    for(int i = 0;i<args.getExpressionList().size();i++){
      args.getExpression(i).accept(getRealThis());
      if(typeCheckResult.isPresentLast()){
        if(!typeCheckResult.isType()){
          argList.add(typeCheckResult.getLast());
        }
      }else{
        logError("0xA0284",args.getExpressionList().get(i).get_SourcePositionStart());
      }
    }
    return argList;
  }

  @Override
  public void traverse(ASTPrimaryGenericInvocationExpression node) {
    //expressions of the type <String>c() or <String>super.<Integer>c() plus Arguments in the brackets

    SymTypeExpression wholeResult = null;

    if(!node.getGenericInvocationSuffix().isPresentSuperSuffix()){
      if(node.getGenericInvocationSuffix().isPresentName()){
        //case 1: <TypeVariable>method(Args) -> similar to GenericInvocationExpression
        //can be accessed solely or after another expression -> check if lastResult is present
        ITypeSymbolsScope testScope;
        if(typeCheckResult.isPresentLast()){
          testScope = typeCheckResult.getLast().getTypeInfo().getSpannedScope();
        }else{
          testScope = getScope(node.getEnclosingScope());
        }
        //resolve for fitting methods
        List<MethodSymbol> methods = testScope.resolveMethodMany(node.getGenericInvocationSuffix().getName());
        if(!methods.isEmpty() && node.getGenericInvocationSuffix().isPresentArguments()){
          //check if the methods fit and return the right returntype
          ASTArguments args = node.getGenericInvocationSuffix().getArguments();
          List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getExtTypeArgumentList());
          if(!typeArgsList.isEmpty()){
            typeCheckResult.unsetType();
          }
          wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
        }
      }else{
        //case 2: <TypeVariable>this(Args) -> similar to PrimaryThisExpression, use method checkMethodsAndReplaceTypeVariables
        //can only be accessed solely -> there cannot be a lastresult
        //search for the nearest enclosingscope spanned by a typesymbol
        OOTypeSymbol typeSymbol = searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
        if(typeSymbol!=null) {
          //get the constructors of the typesymbol
          List<MethodSymbol> methods = typeSymbol.getSpannedScope().resolveMethodMany(typeSymbol.getName());
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
        OOTypeSymbol subType = searchForTypeSymbolSpanningEnclosingScope(getScope(node.getEnclosingScope()));
        //get the superclass of this typesymbol and search for its fitting constructor
        if(subType!=null&&subType.getSuperClassesOnly().size()==1){
          SymTypeExpression superClass = subType.getSuperClassesOnly().get(0);
          List<MethodSymbol> methods = superClass.getMethodList(superClass.getTypeInfo().getName());
          if(!methods.isEmpty() && superSuffix.isPresentArguments()){
            //check if the constructors fit and return the right returntype
            ASTArguments args = superSuffix.getArguments();
            List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getExtTypeArgumentList());
            wholeResult = checkMethodsAndReplaceTypeVariables(methods,args,typeArgsList);
          }
        }
      }
    }

    if(wholeResult!=null){
      typeCheckResult.setLast(wholeResult);
    }else {
      typeCheckResult.reset();
      logError("0xA0285",node.get_SourcePositionStart());
    }
  }

  private OOTypeSymbol searchForTypeSymbolSpanningEnclosingScope(ITypeSymbolsScope scope) {
    //search for the nearest type symbol in the enclosing scopes -> for this and super to get the
    //current object
    while(scope!=null){
      if(scope.isPresentSpanningSymbol()&&scope.getSpanningSymbol() instanceof OOTypeSymbol){
        return (OOTypeSymbol)scope.getSpanningSymbol();
      }
      scope = scope.getEnclosingScope();
    }
    //no typesymbol found
    return null;
  }
}
