package de.monticore.types.mccollectiontypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCBasicTypeArgument extends ASTMCBasicTypeArgumentTOP {

  public ASTMCBasicTypeArgument(){
    super();
  }

  public ASTMCBasicTypeArgument(ASTMCQualifiedType type){
    super(type);
  }

  public Optional<ASTMCType> getMCTypeOpt(){
    return Optional.ofNullable(getMCQualifiedType());
  }
}
