/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcbasictypes;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

/**
 * Extension of the generated Mill to provide a (currently handcrafted)
 * prettyprinter for this language
 */
public  class MCBasicTypesMill extends MCBasicTypesMillTOP {
  
  private static MCBasicTypesMill mcBasicTypesPrettyPrinter;
  
  private static MCBasicTypesFullPrettyPrinter prettyPrinter;
  
  /**
   * Static getter for the pretty printer that delegates to the non static implementation.
   * Only one pretty printer object is created and reused.
   * @return the pretty printer instance
   */
  public  static MCBasicTypesFullPrettyPrinter mcBasicTypesPrettyPrinter ()  {
    
    if (mcBasicTypesPrettyPrinter == null) {
      mcBasicTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    return mcBasicTypesPrettyPrinter._mcBasicTypesPrettyPrinter();
    
  }
  
  protected MCBasicTypesFullPrettyPrinter _mcBasicTypesPrettyPrinter () {
    
    if (mcBasicTypesPrettyPrinter == null) {
      mcBasicTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    // as pretty printer are stateful, it needs to be cleared before it is provided
    prettyPrinter.getPrinter().clearBuffer();
    return prettyPrinter;
  }
  
  private static MCBasicTypesFullPrettyPrinter getPrettyPrinter() {
    return new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
  }
}
