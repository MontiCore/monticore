/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.mccommonstatements._ast;

public class ASTModifierFinal extends ASTModifierFinalTOP {
  
  @Override
  public int getModifier() {
    return ASTConstantsMCCommonStatements.FINAL;
  }
}
