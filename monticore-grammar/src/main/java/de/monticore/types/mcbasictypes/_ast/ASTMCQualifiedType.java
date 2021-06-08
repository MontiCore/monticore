/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import java.util.List;

public class ASTMCQualifiedType extends ASTMCQualifiedTypeTOP {

  public ASTMCQualifiedType() {
  }

  public List<String> getNameList() {
    return this.getMCQualifiedName().getPartsList();
  }
}
