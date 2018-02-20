/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.types.CommonJFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

public class PropertySymbol extends CommonJFieldSymbol<JTypeReference<JTypeSymbol>> {
  
  public static final PropertySymbolKind KIND = new PropertySymbolKind();

  public PropertySymbol(String name, JTypeReference type) {
    super(name, KIND, type);
  }

}
