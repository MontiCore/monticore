package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCWildcardType extends ASTMCWildcardTypeTOP {
  public ASTMCWildcardType(){
    super();
  }

  public ASTMCWildcardType(Optional<ASTMCType> upperBound, Optional<ASTMCType> lowerBound){
    super(upperBound,lowerBound);
  }

  public Optional<ASTMCType> getMCTypeOpt() {
    return Optional.empty();
  }
}
