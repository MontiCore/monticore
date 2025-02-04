/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.*;

import java.util.ArrayList;
import java.util.List;

public class MethodSymbol extends MethodSymbolTOP {

  public MethodSymbol(String name) {
    super(name);
  }

  /**
   * returns a clone of this
   */
  public MethodSymbol deepClone() {
    MethodSymbol clone = new MethodSymbol(name);
    clone.setType(this.type.deepClone());
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    clone.setIsConstructor(this.isConstructor);
    clone.setIsMethod(this.isMethod);
    clone.setIsElliptic(this.isElliptic);
    clone.setIsStatic(this.isStatic);
    clone.setIsFinal(this.isFinal);
    clone.setIsPrivate(this.isPrivate);
    clone.setIsProtected(this.isProtected);
    clone.setIsPublic(this.isPublic);
    clone.setIsAbstract(this.isAbstract);
    if (isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    clone.setAccessModifier(this.accessModifier);
    if (spannedScope != null) {
      clone.setSpannedScope(this.spannedScope);
    }
    return clone;
  }

  @Override
  public List<VariableSymbol> getParameterList() {
    List<VariableSymbol> params = super.getParameterList();
    params.addAll(getSpannedScope().getLocalFieldSymbols());
    return params;
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
