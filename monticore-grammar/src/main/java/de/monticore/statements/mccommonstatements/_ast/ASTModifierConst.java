package de.monticore.statements.mccommonstatements._ast;

public class ASTModifierConst extends ASTModifierConstTOP {
  
  @Override
  public int getModifier() {
    return ASTConstantsMCCommonStatements.CONST;
  }
}
