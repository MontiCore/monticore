/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes._ast;

public class ASTMCBasicGenericType extends ASTMCBasicGenericTypeTOP {

  public ASTMCBasicGenericType() {
  }

  @Override
  public String printBaseType() {
    return String.join(".", getNameList());
  }
}
