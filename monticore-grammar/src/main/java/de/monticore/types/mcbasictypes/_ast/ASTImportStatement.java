
package de.monticore.types.mcbasictypes._ast;


import java.util.List;

public  class ASTImportStatement extends ASTImportStatementTOP {

  protected ASTImportStatement() {}

  protected  ASTImportStatement (ASTQualifiedName qualifiedName, boolean star)  {
    setQualifiedName(qualifiedName);
    setStar(star);
  }

  @java.lang.Deprecated
  public List<String> getImportList()   {

    return this.qualifiedName.parts;

  }
}
