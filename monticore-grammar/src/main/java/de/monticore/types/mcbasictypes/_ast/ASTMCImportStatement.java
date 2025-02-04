/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcbasictypes._ast;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;

public  class ASTMCImportStatement extends ASTMCImportStatementTOP {

  protected ASTMCImportStatement() {}

  protected  ASTMCImportStatement (ASTMCQualifiedName qualifiedName, boolean star)  {
    setMCQualifiedName(qualifiedName);
    setStar(star);
  }

  public String getQName(){
    return getMCQualifiedName().toString();
  }

  public String printType() {
    return MCBasicTypesMill.prettyPrint(this, false);
  }

}
