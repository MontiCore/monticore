/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCTypeParameters;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCTypeVariableDeclaration;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

/**
 * This class provides methods for printing types as Strings. The TypesPrinter
 * is a singleton.
 */
public class FullGenericTypesPrinter {

  private static FullGenericTypesPrinter instance;

  /**
   * We have a singleton.
   */
  private FullGenericTypesPrinter() {
  }

  /**
   * Returns the singleton instance.
   *
   * @return The instance.
   */
  private static FullGenericTypesPrinter getInstance() {
    if (instance == null) {
      instance = new FullGenericTypesPrinter();
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

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

  public static String printType(ASTMCTypeArgument type){
    return getInstance().doPrintType(type);
  }

  protected String doPrintType(ASTMCTypeArgument type){
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

  public static String printType(ASTMCTypeParameters type){
    return getInstance().doPrintType(type);
  }

  protected String doPrintType(ASTMCTypeParameters type){
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

  public static String printType(ASTMCTypeVariableDeclaration type){
    return getInstance().doPrintType(type);
  }

  protected String doPrintType(ASTMCTypeVariableDeclaration type){
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

  public static String printReturnType(ASTMCReturnType type){
    return getInstance().doPrintReturnType(type);
  }

  protected String doPrintReturnType(ASTMCReturnType type){
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

}
