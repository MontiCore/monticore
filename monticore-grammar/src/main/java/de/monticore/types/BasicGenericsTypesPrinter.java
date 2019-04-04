/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

/**
 * This class provides methods for printing types as Strings. The TypesPrinter
 * is a singleton.
 */
public class BasicGenericsTypesPrinter {

  private static BasicGenericsTypesPrinter instance;

  /**
   * We have a singleton.
   */
  protected BasicGenericsTypesPrinter() {
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

  public static String printType(ASTMCType type){
    return getInstance().doPrintType(type);
  }

  protected String doPrintType(ASTMCType type) {

    IndentPrinter printer = new IndentPrinter();

    MCCollectionTypesPrettyPrinter vi = new MCCollectionTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

  public static String printType(ASTMCTypeArgument type) {
    return getInstance().doPrintType(type);
  }

  protected String doPrintType(ASTMCTypeArgument type) {

    IndentPrinter printer = new IndentPrinter();

    MCCollectionTypesPrettyPrinter vi = new MCCollectionTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

  public static String printReturnType(ASTMCReturnType type){
    return getInstance().doPrintReturnType(type);
  }

  protected String doPrintReturnType(ASTMCReturnType type){
    IndentPrinter printer = new IndentPrinter();

    MCCollectionTypesPrettyPrinter vi = new MCCollectionTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

}
