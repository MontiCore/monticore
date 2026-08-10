/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.IScope;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.stereotypes.IStereotypeReference;
import de.monticore.symboltable.stereotypes.StereoinfoDeSer;
import de.monticore.values.MCValue;
import de.se_rwth.commons.logging.Log;

import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link StereoinfoDeSer} for BasicSymbols that resolves
 * stereotypes in the symbol table based on the name given in the JSON entry.
 */
public class BasicSymbolsStereoinfoDeSer extends StereoinfoDeSer {

  public static void init() {
    instance = new BasicSymbolsStereoinfoDeSer();
  }

  @Override
  protected Map.Entry<IStereotypeReference, Optional<MCValue>> doDeserialize(
      JsonElement json,
      IScope enclosingScope) {
    if (json.getAsJsonObject().hasMember(STEREO_VALUE)) {
      Log.errorInternal(
        "0x82403 Internal error: The serialization of values for symbolic stereotypes is not yet " +
          "supported."
      );
    }

    if (!(enclosingScope instanceof IBasicSymbolsScope stereoScope)) {
      Log.error(
        "0x82404 StereotypeSymbolsStereoinfoDeSer#deserialize expected an enclosing scope of " +
          "type de.monticore.symbols.stereotypesymbols._symboltable.IStereotypeSymbolsScope, but " +
          "got " + enclosingScope.getClass().getCanonicalName()
      );
      return null;
    }

    String stereotypeName = json.getAsJsonObject().getStringMember(STEREO_TYPE);
    
    return Map.entry(
      new BasicSymbolsStereotypeReference(stereotypeName, stereoScope),
      Optional.empty()
    );
  }
}
