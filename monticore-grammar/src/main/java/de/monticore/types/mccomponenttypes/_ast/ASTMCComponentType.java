/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccomponenttypes._ast;

import de.monticore.types.mccomponenttypes.MCComponentTypesMill;

public interface ASTMCComponentType extends ASTMCComponentTypeTOP {

  /**
   * Conversion to a compact string, such as {@code "Component"} or
   * {@code "pkg.Component<String>"}.
   */
  @Override
  default String printType() {
    return MCComponentTypesMill.prettyPrint(this, false);
  }
}
