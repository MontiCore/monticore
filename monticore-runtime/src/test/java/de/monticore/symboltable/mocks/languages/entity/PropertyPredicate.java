/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

public class PropertyPredicate implements SymbolPredicate {

  private final PropertySymbol propertySymbol;
  
  public PropertyPredicate(PropertySymbol propertySymbol) {
    this.propertySymbol = propertySymbol;
  }
  
  @Override
  public boolean test(Symbol symbol) {
    // TODO PN eigentlich ist nur der Variable Kind relevant. Aber um auf alle
    //         Methoden von VariabelSymbol zugreifen zu können ist der Cast notwendig
    //         zu klären ist: wie stehen Symbol-Klasse und -Kind zueinander?
    //         Kann bzw. darf es passieren, dass eine Unterklasse von PropertySymbol
    //         nicht mit dem Kind kompatibel ist?
    if ((symbol == null) || !(symbol instanceof PropertySymbol)) {
      return false;
    }
    
    PropertySymbol casted = (PropertySymbol)symbol;
    
    return casted.isKindOf(propertySymbol.getKind())
        && casted.getName().equals(propertySymbol.getName())
        && casted.getType().getName().equals(propertySymbol.getType().getName());
  }
}
