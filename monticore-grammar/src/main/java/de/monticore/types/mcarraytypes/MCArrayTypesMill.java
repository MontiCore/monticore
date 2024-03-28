/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcarraytypes;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCArrayTypesFullPrettyPrinter;

public class MCArrayTypesMill extends MCArrayTypesMillTOP {

  protected static MCArrayTypesMill mcArrayTypesPrettyPrinter;

  protected static MCArrayTypesFullPrettyPrinter prettyPrinter;

  /**
   * Static getter for the pretty printer that delegates to the non static implementation.
   * Only one pretty printer object is created and reused.
   * @return the pretty printer instance
   */
  @Deprecated(forRemoval = true)
  public  static MCArrayTypesFullPrettyPrinter mcArrayTypesPrettyPrinter ()  {

    if (mcArrayTypesPrettyPrinter == null) {
      mcArrayTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    return mcArrayTypesPrettyPrinter._mcArrayTypesPrettyPrinter();

  }

  protected MCArrayTypesFullPrettyPrinter _mcArrayTypesPrettyPrinter () {

    if (mcArrayTypesPrettyPrinter == null) {
      mcArrayTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    // as pretty printer are stateful, it needs to be cleared before it is provided
    prettyPrinter.getPrinter().clearBuffer();
    return prettyPrinter;
  }

  protected static MCArrayTypesFullPrettyPrinter getPrettyPrinter() {
    return new MCArrayTypesFullPrettyPrinter(new IndentPrinter());
  }

}
