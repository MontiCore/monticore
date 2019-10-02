package de.monticore.types.typesymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class TypeSymbol extends TypeSymbolTOP {

  public TypeSymbol(String name){
    super(name);
  }

  public TypeSymbol deepClone(){
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
      methods.add(method.deepClone());
    }
    clone.setMethodList(methods);

    List<FieldSymbol> fields = new ArrayList<>();
    for(FieldSymbol field: this.getFieldList()){
      fields.add(field.deepClone());
    }
    clone.setFieldList(fields);

    List<SymTypeExpression> superTypes = new ArrayList<>();
    for(SymTypeExpression superType: this.getSuperTypeList()){
      superTypes.add(superType.deepClone());
    }
    clone.setSuperTypeList(superTypes);

    List<TypeVarSymbol> typeParameters = new ArrayList<>();
    for(TypeVarSymbol typeParameter: this.getTypeParameterList()){
      typeParameters.add(typeParameter.deepClone());
    }
    clone.setTypeParameterList(typeParameters);

    return clone;
  }

  @Override
  public List<MethodSymbol> getMethodList(){
    if(spannedScope==null || spannedScope.getMethodSymbols()==null||spannedScope.getMethodSymbols().isEmpty()){
      return Lists.newArrayList();
    }
    return spannedScope.getMethodSymbols().values();
  }

  public List<MethodSymbol> getMethodList(String methodname){
    List<MethodSymbol> methodSymbols = spannedScope.resolveMethodMany(methodname);
    return methodSymbols;
  }

}
