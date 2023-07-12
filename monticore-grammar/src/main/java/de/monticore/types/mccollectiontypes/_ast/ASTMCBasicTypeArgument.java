/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCBasicTypeArgument extends ASTMCBasicTypeArgumentTOP {

  public ASTMCBasicTypeArgument(){
    super();
  }
  
  public Optional<ASTMCType> getMCTypeOpt(){
    return Optional.ofNullable(getMCQualifiedType());
  }
}
