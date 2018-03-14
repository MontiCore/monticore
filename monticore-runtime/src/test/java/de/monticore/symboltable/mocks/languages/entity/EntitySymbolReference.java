/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.references.CommonJTypeReference;

public class EntitySymbolReference extends CommonJTypeReference<EntitySymbol> {

  public EntitySymbolReference(final String referencedSymbolName, final Scope definingScopeOfReference) {
    super(referencedSymbolName, EntitySymbol.KIND, definingScopeOfReference);
  }
}
