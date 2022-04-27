/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcbasictypes._ast;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

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
    IndentPrinter printer = new IndentPrinter();
    MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(printer);
    return pp.prettyprint(this);
  }

}
