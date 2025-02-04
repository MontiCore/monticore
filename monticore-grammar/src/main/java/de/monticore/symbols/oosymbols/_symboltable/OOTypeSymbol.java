/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.*;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OOTypeSymbol extends OOTypeSymbolTOP {

  public OOTypeSymbol(String name) {
    super(name);
  }

  public void setMethodList(List<MethodSymbol> methodList){
    for(MethodSymbol method: methodList){
      getSpannedScope().add(method);
      getSpannedScope().add((FunctionSymbol) method);
    }
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
    getSpannedScope().add((VariableSymbol) f);
  }

  public void addMethodSymbol(MethodSymbol m) {
    getSpannedScope().add(m);
    getSpannedScope().add((FunctionSymbol) m);
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

  @Override
  public List<SymTypeExpression> getSuperClassesOnly() {
    List<SymTypeExpression> normalSuperTypes = super.getSuperClassesOnly().stream()
      .filter(type -> !(type.getTypeInfo() instanceof OOTypeSymbol)).collect(Collectors.toList());
    List<SymTypeExpression> oOSuperTypes = superTypes.stream()
      .filter(type -> type.getTypeInfo() instanceof OOTypeSymbol)
      .filter(type -> ((OOTypeSymbol) type.getTypeInfo()).isIsClass())
      .collect(Collectors.toList());
    normalSuperTypes.addAll(oOSuperTypes);
    return normalSuperTypes;
  }

  @Override
  public List<SymTypeExpression> getInterfaceList() {
    return superTypes.stream()
      .filter(type -> type.getTypeInfo() instanceof OOTypeSymbol)
      .filter(type -> ((OOTypeSymbol) type.getTypeInfo()).isIsInterface())
      .collect(Collectors.toList());
  }

  @Override
  public AccessModifier getAccessModifier() {
    List<AccessModifier> modifiers = new ArrayList<>();
    if(isIsPublic()){
      modifiers.add(BasicAccessModifier.PUBLIC);
    }else if(isIsProtected()){
      modifiers.add(BasicAccessModifier.PROTECTED);
    }else if(isIsPrivate()){
      modifiers.add(BasicAccessModifier.PRIVATE);
    }else{
      modifiers.add(BasicAccessModifier.PACKAGE_LOCAL);
    }

    if(isIsStatic()){
      modifiers.add(StaticAccessModifier.STATIC);
    }else{
      modifiers.add(StaticAccessModifier.NON_STATIC);
    }

    return new CompoundAccessModifier(modifiers);
  }

  @Override
  public void setIsPublic(boolean isPublic) {
    if (isPublic) {
      this.isPrivate = false;
      this.isProtected = false;
    }
    this.isPublic = isPublic;
  }

  @Override
  public void setIsPrivate(boolean isPrivate) {
    if (isPrivate) {
      this.isPublic = false;
      this.isProtected = false;
    }
    this.isPrivate = isPrivate;
  }

  @Override
  public void setIsProtected(boolean isProtected) {
    if (isProtected) {
      this.isPrivate = false;
      this.isPublic = false;
    }
    this.isProtected = isProtected;
  }
}
