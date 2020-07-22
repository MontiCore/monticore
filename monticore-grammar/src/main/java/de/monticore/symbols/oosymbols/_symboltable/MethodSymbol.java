// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodSymbol extends MethodSymbolTOP {

  public MethodSymbol(String name){
    super(name);
  }

  /**
   * returns a clone of this
   */
  public MethodSymbol deepClone(){
    MethodSymbol clone = new MethodSymbol(name);
    clone.setReturnType(this.getReturnType().deepClone());
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    clone.setIsStatic(this.isStatic);
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    clone.setAccessModifier(this.accessModifier);
    if(spannedScope!=null){
      clone.setSpannedScope(this.spannedScope);
    }
    List<FieldSymbol> parameterClone = Lists.newArrayList();
    for(FieldSymbol parameter: this.getParameterList()){
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
    IOOSymbolsScope scope = getSpannedScope();
    while(scope.getEnclosingScope()!=null){
      scope = scope.getEnclosingScope();
      if(scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof OOTypeSymbol){
        typeVarSymbolList.addAll(((OOTypeSymbol)(scope.getSpanningSymbol())).getTypeParameterList());
      }
    }
    return typeVarSymbolList;
  }

  public List<FieldSymbol> getParameterList(){
    return getSpannedScope().getLocalFieldSymbols()
        .stream()
        .collect(Collectors.toList());
  }

}
