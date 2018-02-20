/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.references;

import java.util.Optional;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.references.SymbolReference;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.CommonTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.se_rwth.commons.logging.Log;

public class PropertySymbolReference extends PropertySymbol implements
    SymbolReference<PropertySymbol> {

  private final TypeReference<TypeSymbol> typeReference;

  private PropertySymbol referencedSymbol;

  public PropertySymbolReference(final String simpleVariableName, final Optional<String>
      definingTypeName, final Scope definingScopeOfReference) {
    super(simpleVariableName, null);

    if (definingTypeName.isPresent()) {
      typeReference = new CommonTypeReference<>(definingTypeName.get(), CommonJTypeSymbol.KIND,
          definingScopeOfReference);
    }
    else {
      typeReference = null;
    }
  }

  @Override
  public PropertySymbol getReferencedSymbol() {
    if (!isReferencedSymbolLoaded()) {
      final EntitySymbol entitySymbol = (EntitySymbol) typeReference.getReferencedSymbol();

      referencedSymbol = entitySymbol.getProperty(getName()).orElse(null);

      if (!isReferencedSymbolLoaded()) {
        Log.error("0xA1045 " + SymbolReference.class.getSimpleName() + " Could not load full information of '" +
            getName() + "' (Kind " + getKind() + ").");
      }
    }
    return referencedSymbol;
  }

  @Override
  public boolean existsReferencedSymbol() {
    if (isReferencedSymbolLoaded()) {
      return true;
    }
    final EntitySymbol entitySymbol = (EntitySymbol) typeReference.getReferencedSymbol();
    return entitySymbol.getProperty(getName()).isPresent();
  }

  @Override
  public boolean isReferencedSymbolLoaded() {
    return referencedSymbol != null;
  }
}

