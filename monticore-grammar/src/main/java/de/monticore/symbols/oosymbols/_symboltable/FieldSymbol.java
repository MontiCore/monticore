/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

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
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if(getType()!=null){
      clone.setType(this.getType().deepClone());
    }
    return clone;
  }

}
