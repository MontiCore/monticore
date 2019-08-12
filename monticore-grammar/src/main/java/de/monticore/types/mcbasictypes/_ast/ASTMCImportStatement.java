/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcbasictypes._ast;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

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

  public String printType() {
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    this.accept(vi);
    return vi.getPrinter().getContent();
  }
}
