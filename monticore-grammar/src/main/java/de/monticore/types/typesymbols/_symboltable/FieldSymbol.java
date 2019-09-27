package de.monticore.types.typesymbols._symboltable;

public class FieldSymbol extends FieldSymbolTOP {

  public FieldSymbol(String name){
    super(name);
  }

  public FieldSymbol clone(){
    FieldSymbol clone = new FieldSymbol(name);
    clone.setAccessModifier(this.accessModifier);
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    if(getAstNodeOpt().isPresent()) {
      clone.setAstNode(this.getAstNode());
    }
    if(getType()!=null){
      clone.setType(this.getType().clone());
    }
    return clone;
  }

}
