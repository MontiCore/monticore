package de.monticore.statements.mccommonstatements._ast;

public class ASTModifierStatic extends ASTModifierStaticTOP {
  
  @Override
  public int getModifier() {
    return ASTConstantsMCCommonStatements.STATIC;
  }
}
