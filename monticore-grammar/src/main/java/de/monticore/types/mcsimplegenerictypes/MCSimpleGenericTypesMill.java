/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcsimplegenerictypes;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;

/**
 * Extension of the generated Mill to provide a (currently handcrafted)
 * prettyprinter for this language
 */
public  class MCSimpleGenericTypesMill extends MCSimpleGenericTypesMillTOP {
  
  private static MCSimpleGenericTypesMill mcSimpleGenericTypesPrettyPrinter;
  
  private static MCSimpleGenericTypesFullPrettyPrinter prettyPrinter;
  
  /**
   * Static getter for the pretty printer that delegates to the non static implementation.
   * Only one pretty printer object is created and reused.
   * @return the pretty printer instance
   */
  public  static MCSimpleGenericTypesFullPrettyPrinter mcSimpleGenericTypesPrettyPrinter ()  {
    
    if (mcSimpleGenericTypesPrettyPrinter == null) {
      mcSimpleGenericTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    return mcSimpleGenericTypesPrettyPrinter._mcSimpleGenericTypesPrettyPrinter();
    
  }
  
  
  protected MCSimpleGenericTypesFullPrettyPrinter _mcSimpleGenericTypesPrettyPrinter () {
    
    if (mcSimpleGenericTypesPrettyPrinter == null) {
      mcSimpleGenericTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    // as pretty printer are stateful, it needs to be cleared before it is provided
    prettyPrinter.getPrinter().clearBuffer();
    return prettyPrinter;
  }
  
  private static MCSimpleGenericTypesFullPrettyPrinter getPrettyPrinter() {
    return new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
  }
}
