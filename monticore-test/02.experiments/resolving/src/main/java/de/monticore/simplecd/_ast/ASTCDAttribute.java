/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd._ast;

import de.monticore.simplecd._symboltable.ISimpleCDScope;

public class ASTCDAttribute extends ASTCDAttributeTOP {

  @Override
  public void setEnclosingScope(ISimpleCDScope enclosingScope) {
    super.setEnclosingScope(enclosingScope);
    this.getMCType().setEnclosingScope(enclosingScope);
  }
}
