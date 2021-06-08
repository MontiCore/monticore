/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcfullgenerictypes;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

/**
 * Extension of the generated Mill to provide a (currently handcrafted)
 * prettyprinter for this language
 */
public  class MCFullGenericTypesMill extends MCFullGenericTypesMillTOP {
  
  private static MCFullGenericTypesMill mcFullGenericTypesPrettyPrinter;
  
  private static MCFullGenericTypesFullPrettyPrinter prettyPrinter;
  
  /**
   * Static getter for the pretty printer that delegates to the non static implementation.
   * Only one pretty printer object is created and reused.
   * @return the pretty printer instance
   */
  public  static MCFullGenericTypesFullPrettyPrinter mcFullGenericTypesPrettyPrinter ()  {
    
    if (mcFullGenericTypesPrettyPrinter == null) {
      mcFullGenericTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    return mcFullGenericTypesPrettyPrinter._mcFullGenericTypesPrettyPrinter();
    
  }
  
  protected MCFullGenericTypesFullPrettyPrinter _mcFullGenericTypesPrettyPrinter () {
    
    if (mcFullGenericTypesPrettyPrinter == null) {
      mcFullGenericTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    // as pretty printer are stateful, it needs to be cleared before it is provided
    prettyPrinter.getPrinter().clearBuffer();
    return prettyPrinter;
  }
  
  private static MCFullGenericTypesFullPrettyPrinter getPrettyPrinter() {
    return new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter());
  }
}
