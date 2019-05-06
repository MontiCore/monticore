package de.monticore.types.mcbasictypes._ast;

import de.monticore.utils.Names;

import java.util.List;

public class ASTMCQualifiedName extends ASTMCQualifiedNameTOP {

  public ASTMCQualifiedName() {
  }

  public ASTMCQualifiedName(List<String> parts) {
    super(parts);
  }

  public Boolean isQualified() {
    return parts.size() >=2 ;
  }

  public String getBaseName() {
    return parts.get(parts.size()-1);
  }

  public String toString(){
    return Names.constructQualifiedName(
            this.getPartList());
  }
}
