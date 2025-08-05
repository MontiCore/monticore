package de.monticore.statements.mccommonstatements._ast;

public class ASTModifierSynchronized extends ASTModifierSynchronizedTOP {
  
  @Override
  public int getModifier() {
    return ASTConstantsMCCommonStatements.SYNCHRONIZED;
  }
}
