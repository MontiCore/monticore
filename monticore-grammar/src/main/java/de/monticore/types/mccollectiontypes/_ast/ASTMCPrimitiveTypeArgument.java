/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCPrimitiveTypeArgument extends ASTMCPrimitiveTypeArgumentTOP {
  public ASTMCPrimitiveTypeArgument(){
    super();
  }

  public Optional<ASTMCType> getMCTypeOpt(){
    return Optional.ofNullable(getMCPrimitiveType());
  }
}
