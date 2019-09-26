/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCCustomTypeArgument extends ASTMCCustomTypeArgumentTOP {

  public ASTMCCustomTypeArgument(){
    super();
  }

  // TODO RE: entfernbar
  public Optional<ASTMCType> getMCTypeOpt(){
    return Optional.ofNullable(getMCType());
  }
}
