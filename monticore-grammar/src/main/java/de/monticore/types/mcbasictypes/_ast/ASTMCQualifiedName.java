/* (c) https://github.com/MontiCore/monticore */
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

  // TODO RE: Sollte man dies nicht besser und konsistenter in getQName umsetzen?
  // Es gibt eine Regel, die besagt "toString" sollte nicht überschrieben werden?
  public String toString(){
    return Names.constructQualifiedName(
            this.getPartList());
  }
}
