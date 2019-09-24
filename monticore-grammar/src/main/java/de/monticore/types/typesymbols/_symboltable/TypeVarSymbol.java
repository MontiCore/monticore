package de.monticore.types.typesymbols._symboltable;

import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class TypeVarSymbol extends TypeVarSymbolTOP {

  public TypeVarSymbol(String name){
    super(name);
  }

  public TypeVarSymbol clone(){
    TypeVarSymbol clone = new TypeVarSymbol(name);
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    List<SymTypeExpression> upperBounds = new ArrayList<>();
    for(SymTypeExpression upperBound : this.getUpperBound()){
      upperBounds.add(upperBound.clone());
    }
    clone.setUpperBound(upperBounds);
    return clone;
  }
}
