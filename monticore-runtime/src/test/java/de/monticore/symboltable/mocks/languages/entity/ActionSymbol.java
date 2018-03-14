/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.CommonJMethodSymbol;

import java.util.Optional;

public class ActionSymbol extends CommonJMethodSymbol<EntitySymbol, EntitySymbolReference, PropertySymbol> {
  
  public static final ActionSymbolKind KIND = new ActionSymbolKind();
  
  public ActionSymbol(String name) {
    super(name, KIND);
  }

  @Override
  protected MutableScope createSpannedScope() {
    return new ActionScope(this);
  }

  public Optional<PropertySymbol> getVariable(String name) {
    return getSpannedScope().<PropertySymbol>resolveLocally(name, PropertySymbol.KIND);
  }
  
  public void addVariable(PropertySymbol variable) {
    getMutableSpannedScope().add(variable);
  }

  
  @Override
  public String toString() {
    return "method " + super.toString() + ":" + getSpannedScope().getLocalSymbols();
  }

}
