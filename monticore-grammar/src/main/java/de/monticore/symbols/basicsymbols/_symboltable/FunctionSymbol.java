/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.check.SymTypeExpression;

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
    clone.setReturnType(this.getReturnType().deepClone());
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
    List<TypeVarSymbol> typeVarSymbolList = getTypeVariableList();
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
    SymTypeExpression returnType = this.getReturnType();
    TypeSymbol realTypeInfo;
    TypeSymbol typeInfo = returnType.getTypeInfo();
    if(typeInfo instanceof TypeSymbolSurrogate){
      realTypeInfo = ((TypeSymbolSurrogate) returnType.getTypeInfo()).lazyLoadDelegate();
    }else{
      realTypeInfo = typeInfo;
    }
    if(returnType.isTypeVariable() && realTypeInfo instanceof TypeVarSymbol){
      Optional<TypeVarSymbol> typeVar =  replaceMap.keySet().stream().filter(t -> t.getName().equals(realTypeInfo.getName())).findAny();
      typeVar.ifPresent(typeVarSymbol -> this.setReturnType(replaceMap.get(typeVarSymbol)));
    }else{
      returnType.replaceTypeVariables(replaceMap);
    }

    for(VariableSymbol parameter: this.getParameterList()){
      parameter.replaceTypeVariables(replaceMap);
    }
  }

}
