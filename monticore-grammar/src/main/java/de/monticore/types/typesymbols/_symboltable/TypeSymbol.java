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
    if(getAstNodeOpt().isPresent()) {
      clone.setAstNode(this.getAstNode());
    }
    List<MethodSymbol> methods = new ArrayList<>();
    for(MethodSymbol method: this.getMethodList()){
      methods.add(method.clone());
    }
    clone.setMethodList(methods);

    List<FieldSymbol> fields = new ArrayList<>();
    for(FieldSymbol field: this.getFieldList()){
      fields.add(field.clone());
    }
    clone.setFieldList(fields);

    List<SymTypeExpression> superTypes = new ArrayList<>();
    for(SymTypeExpression superType: this.getSuperTypeList()){
      superTypes.add(superType.clone());
    }
    clone.setSuperTypeList(superTypes);

    List<TypeVarSymbol> typeParameters = new ArrayList<>();
    for(TypeVarSymbol typeParameter: this.getTypeParameterList()){
      typeParameters.add(typeParameter.clone());
    }
    clone.setTypeParameterList(typeParameters);

    return clone;
  }

  public List<MethodSymbol> getMethodList(String methodname){
    List<MethodSymbol> methodSymbols = spannedScope.resolveMethodMany(methodname);
    return methodSymbols;
  }

}
