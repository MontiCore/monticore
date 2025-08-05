package de.monticore.statements.mccommonstatements._ast;

public class ASTModifierTransient extends ASTModifierTransientTOP {
  
  @Override
  public int getModifier() {
    return ASTConstantsMCCommonStatements.TRANSIENT;
  }
}
