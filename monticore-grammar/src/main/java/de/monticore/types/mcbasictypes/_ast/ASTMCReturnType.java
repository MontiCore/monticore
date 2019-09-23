/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

public class ASTMCReturnType extends ASTMCReturnTypeTOP {
  public ASTMCReturnType() {
  }

  public String printType() {
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    this.accept(vi);
    return vi.getPrinter().getContent();
  }

}
