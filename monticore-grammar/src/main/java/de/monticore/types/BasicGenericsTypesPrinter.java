/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicGenericsTypesPrettyPrinter;

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
  public static String printType(ASTMCType type) {
    return getInstance().doPrintType(type);
  }
  
  protected String doPrintType(ASTMCType type) {

    IndentPrinter printer = new IndentPrinter();

    MCBasicGenericsTypesPrettyPrinter vi = new MCBasicGenericsTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

}
