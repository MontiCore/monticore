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
 *
 * Care: It is not extensible (as it does not fully implement the static delegator pattern)
 */
public class CollectionTypesPrinter {

  private static CollectionTypesPrinter instance;

  /**
   * We have a singleton.
   */
  protected CollectionTypesPrinter() {
  }

  /**
   * Returns the singleton instance.
   *
   * @return The instance.
   */
  private static CollectionTypesPrinter getInstance() {
    if (instance == null) {
      instance = new CollectionTypesPrinter();
    }
    return instance;
  }

  public static String printType(ASTMCType type){
    return getInstance().doPrintType(type);
  }

  protected String doPrintType(ASTMCType type) {

    IndentPrinter printer = new IndentPrinter();

    MCCollectionTypesPrettyPrinter vi = new MCCollectionTypesPrettyPrinter(printer);
    type.accept(vi);

    return vi.getPrinter().getContent();
  }

  public static String printType(ASTMCTypeArgument type) {
    return getInstance().doPrintType(type);
  }

  protected String doPrintType(ASTMCTypeArgument type) {

    IndentPrinter printer = new IndentPrinter();

    MCCollectionTypesPrettyPrinter vi = new MCCollectionTypesPrettyPrinter(printer);
    type.accept(vi);
    return vi.getPrinter().getContent();
  }

  public static String printReturnType(ASTMCReturnType type){
    return getInstance().doPrintReturnType(type);
  }

  protected String doPrintReturnType(ASTMCReturnType type){
    IndentPrinter printer = new IndentPrinter();

    MCCollectionTypesPrettyPrinter vi = new MCCollectionTypesPrettyPrinter(printer);
    type.accept(vi);
    return vi.getPrinter().getContent();
  }

}
