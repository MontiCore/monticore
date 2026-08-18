/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.mccommonstatements._ast;

public class ASTModifierVolatile extends ASTModifierVolatileTOP {
  
  @Override
  public int getModifier() {
    return ASTConstantsMCCommonStatements.VOLATILE;
  }
}
