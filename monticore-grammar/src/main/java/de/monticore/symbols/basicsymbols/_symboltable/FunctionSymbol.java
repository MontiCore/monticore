/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    if(spannedScope!=null){
      clone.setSpannedScope(this.spannedScope);
    }
    List<VariableSymbol> parameterClone = Lists.newArrayList();
    for(VariableSymbol parameter: this.getParameterList()){
      parameterClone.add(parameter.deepClone());
    }
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
      if(scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof OOTypeSymbol){
        typeVarSymbolList.addAll(((OOTypeSymbol)(scope.getSpanningSymbol())).getTypeParameterList());
      }
    }
    return typeVarSymbolList;
  }

  public List<VariableSymbol> getParameterList(){
    //TODO: how to filter for parameters?
    return getSpannedScope().getLocalVariableSymbols();
  }

}
