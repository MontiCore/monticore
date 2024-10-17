/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FunctionSymbol extends FunctionSymbolTOP {

  public FunctionSymbol(String name){
    super(name);
  }

  public FunctionSymbol deepClone(){
    FunctionSymbol clone = new FunctionSymbol(name);
    clone.setType(this.getType().deepClone());
    clone.setIsElliptic(this.isIsElliptic());
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    clone.setAccessModifier(this.accessModifier);
    clone.setSpannedScope(this.spannedScope);
    return clone;
  }

  public List<TypeVarSymbol> getTypeVariableList(){
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    return spannedScope.getLocalTypeVarSymbols();
  }

  public List<TypeVarSymbol> getAllAccessibleTypeVariables(){
    List<TypeVarSymbol> typeVarSymbolList =
        new ArrayList(getTypeVariableList());
    typeVarSymbolList.addAll(getTypeVariablesOfEnclosingType());
    return typeVarSymbolList;
  }

  public List<TypeVarSymbol> getTypeVariablesOfEnclosingType(){
    List<TypeVarSymbol> typeVarSymbolList = new ArrayList<>();
    IBasicSymbolsScope scope = getSpannedScope();
    while(scope.getEnclosingScope()!=null){
      scope = scope.getEnclosingScope();
      if(scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof TypeSymbol){
        typeVarSymbolList.addAll(((TypeSymbol)(scope.getSpanningSymbol())).getTypeParameterList());
      }
    }
    return typeVarSymbolList;
  }

  public List<VariableSymbol> getParameterList(){
    //TODO: how to filter for parameters?
    return Lists.newArrayList(getSpannedScope().getLocalVariableSymbols());
  }

  public void replaceTypeVariables(Map<TypeVarSymbol, SymTypeExpression> replaceMap){
    //return type
    SymTypeExpression type = this.getType();
    TypeSymbol realTypeInfo;
    TypeSymbol typeInfo = type.getTypeInfo();
    if(typeInfo instanceof TypeSymbolSurrogate){
      realTypeInfo = ((TypeSymbolSurrogate) type.getTypeInfo()).lazyLoadDelegate();
    }else{
      realTypeInfo = typeInfo;
    }
    if(type.isTypeVariable() && realTypeInfo instanceof TypeVarSymbol){
      Optional<TypeVarSymbol> typeVar =  replaceMap.keySet().stream().filter(t -> t.getName().equals(realTypeInfo.getName())).findAny();
      typeVar.ifPresent(typeVarSymbol -> this.setType(replaceMap.get(typeVarSymbol)));
    }else{
      type.replaceTypeVariables(replaceMap);
    }

    for(VariableSymbol parameter: this.getParameterList()){
      parameter.replaceTypeVariables(replaceMap);
    }
  }

  /*
   * Returns the declared(!) type of the function.
   * Note that {@link getType()} only provides the return type of the function
   */
  public SymTypeOfFunction getFunctionType() {
    SymTypeExpression returnType = getType();
    List<SymTypeExpression> parameterTypes = new ArrayList<>();
    for(VariableSymbol parameter : getParameterList()) {
      parameterTypes.add(parameter.getType());
    }
    return SymTypeExpressionFactory.createFunction(this, returnType, parameterTypes, isIsElliptic());
  }

  @Override
  public AccessModifier getAccessModifier() {
    // supporting legacy source code...
    if(accessModifier == null) {
      Log.trace("AccessModifier of function '"
              + getFullName() + "' was not set (null)",
          "BasicSymbols");
      accessModifier = AccessModifier.ALL_INCLUSION;
    }
    return accessModifier;
  }
}
