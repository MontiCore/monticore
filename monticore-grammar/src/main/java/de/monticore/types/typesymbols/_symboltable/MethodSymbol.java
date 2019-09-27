package de.monticore.types.typesymbols._symboltable;

import com.google.common.collect.Lists;

import java.util.List;

public class MethodSymbol extends MethodSymbolTOP {

  public MethodSymbol(String name){
    super(name);
  }

  public MethodSymbol clone(){
    MethodSymbol clone = new MethodSymbol(name);
    clone.setReturnType(this.getReturnType().clone());
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    if(getAstNodeOpt().isPresent()) {
      clone.setAstNode(this.getAstNode());
    }
    clone.setAccessModifier(this.accessModifier);
    if(spannedScope!=null){
      clone.setSpannedScope(this.spannedScope);
    }
    List<FieldSymbol> parameterClone = Lists.newArrayList();
    for(FieldSymbol parameter: this.getParameterList()){
      parameterClone.add(parameter.clone());
    }
    clone.setParameterList(parameterClone);
    return clone;
  }

}
