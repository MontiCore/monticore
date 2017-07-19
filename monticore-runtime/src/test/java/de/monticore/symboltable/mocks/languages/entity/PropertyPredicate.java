/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
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
