/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

public interface ASTMCGenericType extends ASTMCGenericTypeTOP {

  default String printWithoutTypeArguments() {
    return String.join(".", getNamesList());
  }
}
