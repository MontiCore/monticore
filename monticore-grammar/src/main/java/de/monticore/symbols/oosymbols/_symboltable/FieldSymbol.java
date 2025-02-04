/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symboltable.modifiers.*;

import java.util.ArrayList;
import java.util.List;

public class FieldSymbol extends FieldSymbolTOP {

  public FieldSymbol(String name){
    super(name);
  }

  /**
   * returns a clone of this
   * this is only required for the type check
   * does not create a full deep clone
   */
  public FieldSymbol deepClone(){
    FieldSymbol clone = new FieldSymbol(name);
    clone.setAccessModifier(this.accessModifier);
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    clone.setPackageName(this.packageName);
    clone.setIsPrivate(this.isPrivate);
    clone.setIsProtected(this.isProtected);
    clone.setIsPublic(this.isPublic);
    clone.setIsStatic(this.isStatic);
    clone.setIsFinal(this.isFinal);
    clone.setIsDerived(this.isDerived);
    clone.setIsReadOnly(this.isReadOnly);
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if(getType()!=null){
      clone.setType(this.getType().deepClone());
    }
    return clone;
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
    if(isIsFinal()){
      modifiers.add(WritableAccessModifier.NON_WRITABLE);
    }else{
      modifiers.add(WritableAccessModifier.WRITABLE);
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
