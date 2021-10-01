/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

public class MethodSymbol extends MethodSymbolTOP {

  public MethodSymbol(String name){
    super(name);
  }

  /**
   * returns a clone of this
   */
  public MethodSymbol deepClone(){
    MethodSymbol clone = new MethodSymbol(name);
    clone.setReturnType(this.returnType.deepClone());
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    clone.setIsStatic(this.isStatic);
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    clone.setAccessModifier(this.accessModifier);
    if(spannedScope!=null){
      clone.setSpannedScope(this.spannedScope);
    }
    return clone;
  }

}
