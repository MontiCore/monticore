/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.prettyprint.MCBasicGenericsTypesPrettyPrinterConcreteVisitor;
import de.se_rwth.commons.Names;

import java.util.List;

/**
 * This class provides methods for printing types as Strings. The TypesPrinter
 * is a singleton.
 */
public class BasicGenericsTypesPrinter {

  private static BasicGenericsTypesPrinter instance;

  /**
   * We have a singleton.
   */
  private BasicGenericsTypesPrinter() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  private static BasicGenericsTypesPrinter getInstance() {
    if (instance == null) {
      instance = new BasicGenericsTypesPrinter();
    }
    return instance;
  }
  
  /******************************************************************
   * INTERFACES
   ******************************************************************/
  
  /**
   * Converts an ASTType to a String
   * 
   * @param type ASTType to be converted
   * @return String representation of "type"
   */
  public static String printType(ASTType type) {
    return getInstance().doPrintType(type);
  }
  
  protected String doPrintType(ASTType type) {

    IndentPrinter printer = new IndentPrinter();

    MCBasicGenericsTypesPrettyPrinterConcreteVisitor vi = new MCBasicGenericsTypesPrettyPrinterConcreteVisitor(printer);
    return vi.prettyprint(type);
  }

}
