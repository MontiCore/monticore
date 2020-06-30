/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.basictypesymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OOTypeSymbol extends OOTypeSymbolTOP {

  public OOTypeSymbol(String name) {
    super(name);
  }

  @Override
  public void setSpannedScope(ITypeSymbolsScope spannedScope) {
    super.setSpannedScope(spannedScope);
    this.spannedScope = spannedScope;
  }

  protected List<MethodSymbol> methodList=new ArrayList<>();

  public void setMethodList(List<MethodSymbol> methodList){
    this.methodList = methodList;
    for(MethodSymbol method: methodList){
      getSpannedScope().add(method);
    }
  }

  public List<SymTypeExpression> getSuperClassesOnly(){
    return superTypes.stream()
        .filter(type -> type.getTypeInfo().isClass)
        .collect(Collectors.toList());
  }

  public List<SymTypeExpression> getInterfaceList(){
    return superTypes.stream()
            .filter(type -> type.getTypeInfo().isInterface)
            .collect(Collectors.toList());
  }

  /**
   * get a list of all the methods the type definition can access
   */
  public List<MethodSymbol> getMethodList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    return getSpannedScope().getLocalMethodSymbols();
  }

  /**
   * search in the scope for methods with a specific name
   */
  public List<MethodSymbol> getMethodList(String methodname) {
    return getSpannedScope().resolveMethodMany(methodname);
  }

  /**
   * get a list of all the fields the type definition can access
   */
  public List<FieldSymbol> getFieldList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    return getSpannedScope().getLocalFieldSymbols();
  }

  /**
   * search in the scope for methods with a specific name
   */
  public List<FieldSymbol> getFieldList(String fieldname) {
    return getSpannedScope().resolveFieldMany(fieldname);
  }

  public List<TypeVarSymbol> getTypeParameterList() {
    if(spannedScope==null){
      return Lists.newArrayList();
    }
    return getSpannedScope().getLocalTypeVarSymbols();
  }


  public void addTypeVarSymbol(TypeVarSymbol t) {
    getSpannedScope().add(t);
  }

  public void addFieldSymbol(FieldSymbol f) {
    getSpannedScope().add(f);
  }

  public void addMethodSymbol(MethodSymbol m) {
    getSpannedScope().add(m);
  }

  public boolean isPresentSuperClass() {
    return !getSuperClassesOnly().isEmpty();
  }

  public SymTypeExpression getSuperClass() {
    if (isPresentSuperClass()) {
      return getSuperClassesOnly().get(0);
    }
    Log.error("0xA1067 SuperClass does not exist");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }


}
