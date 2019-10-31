// (c) https://github.com/MontiCore/monticore

package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;
import java.util.stream.Collectors;

public class ASTMCMultipleGenericType extends ASTMCMultipleGenericTypeTOP {

  protected ASTMCMultipleGenericType() {
  }

  @Override
  public String printBaseType() {
    return this.getMCBasicGenericType().printType();
  }
}
