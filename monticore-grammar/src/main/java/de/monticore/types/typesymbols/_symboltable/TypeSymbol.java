package de.monticore.types.typesymbols._symboltable;

import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class TypeSymbol extends TypeSymbolTOP {

  public TypeSymbol(String name){
    super(name);
  }

  public TypeSymbol clone(){
    TypeSymbol clone = new TypeSymbol(name);
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setSpannedScope(this.getSpannedScope());

    List<MethodSymbol> methods = new ArrayList<>();
    for(MethodSymbol method: this.getMethods()){
      methods.add(method.clone());
    }
    clone.setMethods(methods);

    List<FieldSymbol> fields = new ArrayList<>();
    for(FieldSymbol field: this.getFields()){
      fields.add(field.clone());
    }
    clone.setFields(fields);

    List<SymTypeExpression> superTypes = new ArrayList<>();
    for(SymTypeExpression superType: this.getSuperTypes()){
      superTypes.add(superType.clone());
    }
    clone.setSuperTypes(superTypes);

    List<TypeVarSymbol> typeParameters = new ArrayList<>();
    for(TypeVarSymbol typeParameter: this.getTypeParameters()){
      typeParameters.add(typeParameter.clone());
    }
    clone.setTypeParameters(typeParameters);

    return clone;
  }

}
