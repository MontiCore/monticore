/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsHandler;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsTraverser;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
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

  protected ISynthesize synthesize;

  @Deprecated
  public DeriveSymTypeOfJavaClassExpressions() {
    // default behaviour, as this class had no synthezises beforehand
    synthesize = new FullSynthesizeFromMCFullGenericTypes();
  }

  public DeriveSymTypeOfJavaClassExpressions(ISynthesize synthesize) {
    this.synthesize = synthesize;
  }

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
    if(!innerResult.isObscureType()) {
      SymTypeExpression wholeResult = calculateThisExpression(node, innerResult);

      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(wholeResult);
      if (wholeResult.isObscureType()) {
        Log.error("0xA0252 Could not derive the type that 'this' refers to", node.get_SourcePositionStart());
      }
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
  public void traverse(ASTClassExpression node) {
    //only type allowed --> check that Expression is no field or method
    //traverse the inner expression, check that it is a type (how?); the result is the type "Class"
    //can be calculated
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    SymTypeExpression innerResult = SymTypeExpressionFactory.createObscureType();

    deprecated_traverse(node.getMCReturnType());
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
      List<SymTypeExpression> typeArgsList = calculateTypeArguments(superSuffix.getMCTypeArgumentList());
      List<FunctionSymbol> methods = superClass.getMethodList(superSuffix.getName(), false, AccessModifier.ALL_INCLUSION);
      if (!methods.isEmpty() && null != superSuffix.getArguments()) {
        //check if the methods fit and return the right returntype
        ASTArguments args = superSuffix.getArguments();
        return checkMethodsAndReplaceTypeVariables(methods, args, typeArgsList);
      }
    }
    else {
      //case 2 -> Expression.super.Field
      List<VariableSymbol> fields = superClass.getFieldList(superSuffix.getName(), false, AccessModifier.ALL_INCLUSION);
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
  public void traverse(ASTInstanceofPatternExpression node) {
    SymTypeExpression expressionResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if(getTypeCheckResult().isType()) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0267 the expression at source position " + node.getExpression().get_SourcePositionStart() + " cannot be a type");
      return;
    }

    SymTypeExpression typeResult = SymTypeExpressionFactory.createObscureType();

    //calculate right type: type that the expression should be an instance of
    ASTMCType mcType = ((ASTTypePattern) node.getPattern()).getLocalVariableDeclaration().getMCType();
    deprecated_traverse(mcType);
    if(getTypeCheckResult().isPresentResult()){
      if(!getTypeCheckResult().isType()) {
        if(!getTypeCheckResult().getResult().isObscureType()) {
          getTypeCheckResult().reset();
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          Log.error("0xA0269 the expression at source position " + mcType.get_SourcePositionStart() + " must be a type");
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
    List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getPrimaryGenericInvocationExpression().getMCTypeArgumentList());

    //search in the scope of the type that before the "." for a method that has the right name
    if(node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().isPresentName()) {
      List<FunctionSymbol> methods = expressionResult.getMethodList(node.getPrimaryGenericInvocationExpression().getGenericInvocationSuffix().getName(),isType,false, AccessModifier.ALL_INCLUSION);
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

  protected List<SymTypeExpression> calculateTypeArguments(List<ASTMCTypeArgument> extTypeArgumentList) {
    //calculate each TypeArgument and return the results in a list
    List<SymTypeExpression> typeArgsList = Lists.newArrayList();
    for (ASTMCTypeArgument astMCTypeArgument : extTypeArgumentList) {
      deprecated_traverse(astMCTypeArgument);
      if (getTypeCheckResult().isPresentResult()) {
        typeArgsList.add(getTypeCheckResult().getResult());
      } else {
        getTypeCheckResult().reset();
        typeArgsList.add(SymTypeExpressionFactory.createObscureType());
        Log.error("0xA0283 the type argument at source position " + astMCTypeArgument.get_SourcePositionStart() + " cannot be calculated");
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
          List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getMCTypeArgumentList());
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
            List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getMCTypeArgumentList());
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
          List<FunctionSymbol> methods = superClass.getMethodList(superClass.getTypeInfo().getName(), false, AccessModifier.ALL_INCLUSION);
          if(!methods.isEmpty() && superSuffix.isPresentArguments()){
            //check if the constructors fit and return the right returntype
            ASTArguments args = superSuffix.getArguments();
            List<SymTypeExpression> typeArgsList = calculateTypeArguments(node.getMCTypeArgumentList());
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

  // Warning: deprecated and wrong behavior
  // (it does not set whether it's an expression or type identifier)
  // this is fixed in typecheck 3
  // and exists only here like this to not change the behaviour of typecheck1

  @Deprecated
  public void deprecated_traverse(ASTMCType type){
    SymTypeExpression wholeResult = null;
    TypeCheckResult result = synthesize.synthesizeType(type);
    if(result.isPresentResult()){
      wholeResult=result.getResult();
    }
    if(wholeResult!=null){
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }else{
      getTypeCheckResult().reset();
    }
  }

  @Deprecated
  public void deprecated_traverse(ASTMCReturnType returnType){
    SymTypeExpression wholeResult = null;
    if(returnType.isPresentMCVoidType()){
      wholeResult = SymTypeExpressionFactory.createTypeVoid();
    }else if(returnType.isPresentMCType()){
      TypeCheckResult res = synthesize.synthesizeType(returnType);
      if(res.isPresentResult()){
        wholeResult = res.getResult();
      }
    }
    if(wholeResult!=null){
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }else{
      getTypeCheckResult().reset();
    }
  }

  @Deprecated
  public void deprecated_traverse(ASTMCTypeArgument typeArgument){
    SymTypeExpression wholeResult = null;
    if(typeArgument.getMCTypeOpt().isPresent()){
      TypeCheckResult res = synthesize.synthesizeType(typeArgument.getMCTypeOpt().get());
      if(res.isPresentResult()){
        wholeResult = res.getResult();
      }
    }
    if(wholeResult!=null){
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }else{
      getTypeCheckResult().reset();
    }
  }
}
