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
    return getMutableSpannedScope().<ActionSymbol>resolveLocally(actionName, ActionSymbol.KIND);
  }
  
  public void addAction(ActionSymbol method) {
    getMutableSpannedScope().add(method);
  }
  
  public Collection<ActionSymbol> getActions() {
    return getMutableSpannedScope().resolveLocally(ActionSymbol.KIND);
  }
  
  public Optional<PropertySymbol> getProperty(String propertyName) {
    return getMutableSpannedScope().<PropertySymbol>resolveLocally(propertyName, PropertySymbol.KIND);
  }
  
  public void addProperty(PropertySymbol property) {
    getMutableSpannedScope().add(property);
  }
  
  public Collection<PropertySymbol> getProperties() {
    return getMutableSpannedScope().resolveLocally(PropertySymbol.KIND);
  }

  @Override
  public MutableScope getMutableSpannedScope() {
    return super.getMutableSpannedScope();
  }
}
