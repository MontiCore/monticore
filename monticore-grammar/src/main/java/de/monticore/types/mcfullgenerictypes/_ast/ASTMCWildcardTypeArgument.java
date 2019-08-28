/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCWildcardTypeArgument extends ASTMCWildcardTypeArgumentTOP {
  public ASTMCWildcardTypeArgument(){
    super();
  }

  public ASTMCWildcardTypeArgument(Optional<ASTMCType> upperBound, Optional<ASTMCType> lowerBound){
    super(upperBound,lowerBound);
  }

  public Optional<ASTMCType> getMCTypeOpt() {
    return Optional.empty();
  }
}
