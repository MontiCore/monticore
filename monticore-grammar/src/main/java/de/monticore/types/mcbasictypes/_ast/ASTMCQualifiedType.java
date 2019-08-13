/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import java.util.List;

public class ASTMCQualifiedType extends ASTMCQualifiedTypeTOP {

  public ASTMCQualifiedType() {
  }

  public ASTMCQualifiedType(ASTMCQualifiedName qualifiedName) {
    super(qualifiedName);
  }

  public List<String> getNameList() {
    return this.getMCQualifiedName().getPartList();
  }

  public String getBaseName() {
    return this.getMCQualifiedName().getBaseName();
  }

}
