/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

public interface ASTMCType extends ASTMCTypeTOP {
  
  /**
   * TODO remove after Release of 5.4.0
   */
  @Deprecated
  default String printType() {
    IndentPrinter printer = new IndentPrinter();
    
    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    this.accept(vi);
    return vi.getPrinter().getContent();
  }
  
  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  default String printType(MCBasicTypesPrettyPrinter pp) {
    return pp.prettyprint(this);
  }
}
