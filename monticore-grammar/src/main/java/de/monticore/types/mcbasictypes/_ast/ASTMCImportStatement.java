
package de.monticore.types.mcbasictypes._ast;


import java.util.List;

public  class ASTMCImportStatement extends ASTMCImportStatementTOP {

  protected ASTMCImportStatement() {}

  protected  ASTMCImportStatement (ASTMCQualifiedName qualifiedName, boolean star)  {
    setMCQualifiedName(qualifiedName);
    setStar(star);
  }

  @java.lang.Deprecated
  public List<String> getImportList()   {

    return this.mCQualifiedName.parts;

  }
}
