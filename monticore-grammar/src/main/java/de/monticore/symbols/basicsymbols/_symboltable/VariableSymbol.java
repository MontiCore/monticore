/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.types.check.SymTypeExpression;

import java.util.Map;
import java.util.Optional;

public class VariableSymbol extends VariableSymbolTOP {

  public VariableSymbol(String name){
    super(name);
  }

  public VariableSymbol deepClone(){
    VariableSymbol clone = new VariableSymbol(name);
    clone.setAccessModifier(this.accessModifier);
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if(type!=null){
      clone.setType(type.deepClone());
    }
    return clone;
  }

  public void replaceTypeVariables(Map<TypeVarSymbol, SymTypeExpression> replaceMap){
    //return type
    SymTypeExpression returnType = this.getType();
    TypeSymbol realTypeInfo;
    TypeSymbol typeInfo = returnType.getTypeInfo();
    if(typeInfo instanceof TypeSymbolSurrogate){
      realTypeInfo = ((TypeSymbolSurrogate) returnType.getTypeInfo()).lazyLoadDelegate();
    }else{
      realTypeInfo = typeInfo;
    }
    if(returnType.isTypeVariable() && realTypeInfo instanceof TypeVarSymbol){
      Optional<TypeVarSymbol> typeVar =  replaceMap.keySet().stream().filter(t -> t.getName().equals(realTypeInfo.getName())).findAny();
      typeVar.ifPresent(typeVarSymbol -> this.setType(replaceMap.get(typeVarSymbol)));
    }else{
      returnType.replaceTypeVariables(replaceMap);
    }
  }
}
