/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mccollectiontypes;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesFullPrettyPrinter;

/**
 * Extension of the generated Mill to provide a (currently handcrafted)
 * prettyprinter for this language
 */
public  class MCCollectionTypesMill extends MCCollectionTypesMillTOP {
  
  private static MCCollectionTypesMill mcCollectionTypesPrettyPrinter;
  
  private static MCCollectionTypesFullPrettyPrinter prettyPrinter;
  
  /**
   * Static getter for the pretty printer that delegates to the non static implementation.
   * Only one pretty printer object is created and reused.
   * @return the pretty printer instance
   */
  public  static MCCollectionTypesFullPrettyPrinter mcCollectionTypesPrettyPrinter ()  {
    
    if (mcCollectionTypesPrettyPrinter == null) {
      mcCollectionTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    return mcCollectionTypesPrettyPrinter._mcCollectionTypesPrettyPrinter();
    
  }
  
  protected MCCollectionTypesFullPrettyPrinter _mcCollectionTypesPrettyPrinter () {
    
    if (mcCollectionTypesPrettyPrinter == null) {
      mcCollectionTypesPrettyPrinter = getMill();
    }
    if (prettyPrinter == null) {
      prettyPrinter = getPrettyPrinter();
    }
    // as pretty printer are stateful, it needs to be cleared before it is provided
    prettyPrinter.getPrinter().clearBuffer();
    return prettyPrinter;
  }
  
  private static MCCollectionTypesFullPrettyPrinter getPrettyPrinter() {
    return new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());
  }
}
