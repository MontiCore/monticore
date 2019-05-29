package de.monticore.types.mcbasictypes._ast;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.FullGenericTypesPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

import java.util.List;

public interface ASTMCType extends ASTMCTypeTOP {

  public List<String> getNameList();

  public String getBaseName();

  default public String printType() {
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    this.accept(vi);
    return vi.getPrinter().getContent();
  }
}
