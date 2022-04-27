/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class TypeSymbol extends TypeSymbolTOP {

  public TypeSymbol(String name){
    super(name);
  }

  public void setFunctionList(List<FunctionSymbol> methodList){
    for(FunctionSymbol method: methodList){
      getSpannedScope().add(method);
    }
  }

  public List<SymTypeExpression> getSuperClassesOnly(){
    return superTypes;
  }

  public List<SymTypeExpression> getInterfaceList(){
    return superTypes;
  }

  /**
   * get a list of all the methods the type definition can access
   */
  public List<FunctionSymbol> getFunctionList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    return getSpannedScope().getLocalFunctionSymbols();
  }

  /**
   * search in the scope for methods with a specific name
   */
  public List<FunctionSymbol> getFunctionList(String methodname) {
    return getSpannedScope().resolveFunctionMany(methodname);
  }

  /**
   * get a list of all the fields the type definition can access
   */
  public List<VariableSymbol> getVariableList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    return getSpannedScope().getLocalVariableSymbols();
  }

  /**
   * search in the scope for methods with a specific name
   */
  public List<VariableSymbol> getVariableList(String fieldname) {
    return getSpannedScope().resolveVariableMany(fieldname);
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

  public void addVariableSymbol(VariableSymbol f) {
    getSpannedScope().add(f);
  }

  public void addFunctionSymbol(FunctionSymbol m) {
    getSpannedScope().add(m);
  }

  public boolean isPresentSuperClass() {
    return !getSuperClassesOnly().isEmpty();
  }

  public SymTypeExpression getSuperClass() {
    if (isPresentSuperClass()) {
      return getSuperClassesOnly().get(0);
    }
    Log.error("0xA1068 SuperClass does not exist");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

}
