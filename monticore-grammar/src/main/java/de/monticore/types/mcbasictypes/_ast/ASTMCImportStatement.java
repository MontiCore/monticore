
package de.monticore.types.mcbasictypes._ast;


import java.util.List;

public  class ASTMCImportStatement extends ASTMCImportStatementTOP {

  protected ASTMCImportStatement() {}

  protected  ASTMCImportStatement (ASTMCQualifiedName qualifiedName, boolean star)  {
    setMCQualifiedName(qualifiedName);
    setStar(star);
  }

  @Deprecated
  public List<String> getImportList()   {
    return this.mCQualifiedName.parts;
  }

  public String getQName(){
    return getMCQualifiedName().toString();
  }
}
