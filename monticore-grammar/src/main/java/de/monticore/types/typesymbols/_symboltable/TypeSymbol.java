package de.monticore.types.typesymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TypeSymbol extends TypeSymbolTOP {

  public TypeSymbol(String name) {
    super(name);
    isClass = false;
    isInterface = false;
    isEnum = false;
    isAbstract = false;
  }

  protected List<MethodSymbol> methodList=new ArrayList<>();

  private boolean isClass;
  private boolean isInterface;
  private boolean isEnum;
  private boolean isAbstract;



  public void setMethodList(List<MethodSymbol> methodList){
    this.methodList = methodList;
    for(MethodSymbol method: methodList){
      spannedScope.add(method);
    }
  }

  public List<SymTypeExpression> getSuperClassesOnly(){
    return superTypes.stream()
        .filter(type -> type.getTypeInfo().isClass)
        .collect(Collectors.toList());
  }



  /**
   * get a list of all the methods the type definition can access
   */

  public List<MethodSymbol> getMethodList() {
    if (spannedScope == null || spannedScope.getMethodSymbols() == null || spannedScope.getMethodSymbols().isEmpty()) {
      return Lists.newArrayList();
    }
    return spannedScope.getMethodSymbols().values();
  }

  /**
   * search in the scope for methods with a specific name
   */
  public List<MethodSymbol> getMethodList(String methodname) {
    return spannedScope.resolveMethodMany(methodname);
  }

  /**
   * get a list of all the fields the type definition can access
   */

  public List<FieldSymbol> getFieldList() {
    if (spannedScope == null || spannedScope.getFieldSymbols() == null || spannedScope.getFieldSymbols().isEmpty()) {
      return Lists.newArrayList();
    }
    return spannedScope.getFieldSymbols().values();
  }

  /**
   * search in the scope for methods with a specific name
   */
  public List<FieldSymbol> getFieldList(String fieldname) {
    return spannedScope.resolveFieldMany(fieldname);
  }

  public List<TypeVarSymbol> getTypeParameterList() {
    return spannedScope.getTypeVarSymbols().values();
  }


  public void addTypeVarSymbol(TypeVarSymbol t) {
    spannedScope.add(t);
  }

  public void addFieldSymbol(FieldSymbol f) {
    spannedScope.add(f);
  }

  public void addMethodSymbol(MethodSymbol m) {
    spannedScope.add(m);
  }

  public boolean isClass() {
    return isClass;
  }

  public void setClass(boolean aClass) {
    isClass = aClass;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public void setInterface(boolean anInterface) {
    isInterface = anInterface;
  }

  public boolean isEnum() {
    return isEnum;
  }

  public void setEnum(boolean anEnum) {
    isEnum = anEnum;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract(boolean anAbstract) {
    isAbstract = anAbstract;
  }
}
