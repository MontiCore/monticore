/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.se_rwth.commons.Names;

public class ASTMCQualifiedName extends ASTMCQualifiedNameTOP {

  public ASTMCQualifiedName() {
  }

  public Boolean isQualified() {
    return getPartsList().size() >=2 ;
  }

  public String getBaseName() {
    return getPartsList().get(getPartsList().size()-1);
  }

  public String getQName() {
    return Names.constructQualifiedName(
            this.getPartsList());
  }

  public String toString(){
    return getQName();
  }
}
