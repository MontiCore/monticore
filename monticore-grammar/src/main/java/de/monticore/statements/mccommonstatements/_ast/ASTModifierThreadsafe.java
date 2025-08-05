package de.monticore.statements.mccommonstatements._ast;

public class ASTModifierThreadsafe extends ASTModifierThreadsafeTOP {
  
  @Override
  public int getModifier() {
    return ASTConstantsMCCommonStatements.THREADSAFE;
  }
}
