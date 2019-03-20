/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.CommonJTypeSymbol;

public class EntitySymbol extends CommonJTypeSymbol<EntitySymbol, PropertySymbol, ActionSymbol, EntitySymbolReference> {
  
  public static final EntitySymbolKind KIND = EntitySymbolKind.KIND;
  
  public EntitySymbol(String name) {
    super(name, EntitySymbol.KIND, PropertySymbol.KIND, ActionSymbol.KIND);
  }

  public Optional<ActionSymbol> getAction(String actionName) {
    return getSpannedScope().<ActionSymbol>resolveLocally(actionName, ActionSymbol.KIND);
  }
  
  public void addAction(ActionSymbol method) {
    getSpannedScope().add(method);
  }
  
  public Collection<ActionSymbol> getActions() {
    return getSpannedScope().resolveLocally(ActionSymbol.KIND);
  }
  
  public Optional<PropertySymbol> getProperty(String propertyName) {
    return getSpannedScope().<PropertySymbol>resolveLocally(propertyName, PropertySymbol.KIND);
  }
  
  public void addProperty(PropertySymbol property) {
    getSpannedScope().add(property);
  }
  
  public Collection<PropertySymbol> getProperties() {
    return getSpannedScope().resolveLocally(PropertySymbol.KIND);
  }


}
