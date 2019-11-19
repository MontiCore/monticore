/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

import java.util.Optional;

public interface ASTMCTypeArgument extends ASTMCTypeArgumentTOP {
  public Optional<ASTMCType> getMCTypeOpt();

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  default String printType(MCCollectionTypesPrettyPrinter pp) {
    return pp.prettyprint(this);
  }
}
