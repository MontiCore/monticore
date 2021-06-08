/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCCustomTypeArgument extends ASTMCCustomTypeArgumentTOP {

  public ASTMCCustomTypeArgument(){
    super();
  }

  // TODO RE: das hier tut das falsche: Der Typ eines Arrays unterscheidet sich vom Typ seines arguments
  @Deprecated
  public Optional<ASTMCType> getMCTypeOpt(){
    return Optional.ofNullable(getMCType());
  }
  
  // TODO: Klasse kann entfernt werden
  
}
