/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.interpreter.Value;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.stereotypes.IStereotypeSymbol;
import de.monticore.symboltable.stereotypes.StereoinfoDeSer;
import de.se_rwth.commons.logging.Log;

import java.util.Map;
import java.util.Optional;

/** Implementation of {@link StereoinfoDeSer} for BasicSymbols */
public class BasicSymbolsStereoinfoDeSer extends StereoinfoDeSer {

  public static void init() {
    instance = new BasicSymbolsStereoinfoDeSer();
  }

  @Override
  protected Map.Entry<IStereotypeSymbol, Optional<Value>> doDeserialize(JsonElement json,
                                                                        IScope enclosingScope) {
    if (json.getAsJsonObject().hasMember(STEREO_VALUE)) {
      Log.errorInternal(
        "0x82403 Internal error: The serialization of values for symbolic stereotypes is not yet " +
          "supported."
      );
    }

    String stereotypeName = json.getAsJsonObject().getStringMember(STEREO_TYPE);
    MCStereotypeSymbolSurrogateBuilder stereotypeBuilder =
      BasicSymbolsMill.mCStereotypeSymbolSurrogateBuilder()
        .setName(stereotypeName);

    if (enclosingScope instanceof IBasicSymbolsScope) {
      IBasicSymbolsScope stereoScope = ((IBasicSymbolsScope) enclosingScope);
      stereotypeBuilder.setEnclosingScope(stereoScope);
    } else {
      Log.error(
        "0x82404 StereotypeSymbolsStereoinfoDeSer#deserialize expected an enclosing scope of " +
          "type de.monticore.symbols.stereotypesymbols._symboltable.IStereotypeSymbolsScope, but " +
          "got " + enclosingScope.getClass().getCanonicalName()
      );
    }

    return Map.entry(stereotypeBuilder.build(), Optional.empty());
  }
}
