/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTMCWildcardTypeArgument extends ASTMCWildcardTypeArgumentTOP {
  public ASTMCWildcardTypeArgument(){
    super();
  }

  public Optional<ASTMCType> getMCTypeOpt() {
    return Optional.empty();
  }
}
