/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public interface ASTMCTypeArgument extends ASTMCTypeArgumentTOP {
  public Optional<ASTMCType> getMCTypeOpt();
}
