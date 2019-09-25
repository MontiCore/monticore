/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes._ast;

import java.util.List;

public class ASTMCArrayType extends ASTMCArrayTypeTOP {
  public ASTMCArrayType() {
  }

  @Override
  public List<String> getNameList() {
    return getMCType().getNameList();
  }

  @Override
  public String getBaseName() {
    return getMCType().getBaseName();
  }

}
