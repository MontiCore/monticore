/* (c) https://github.com/MontiCore/monticore */
package bar._symboltable;

import bar.BarMill;
import de.monticore.symboltable.SymbolWithScopeOfUnknownKind;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class BarDeSer extends BarDeSerTOP {

  @Override
  protected void deserializeSymbols(IBarScope scope, JsonObject scopeJson) {
    for (JsonObject symbol : JsonDeSers.getSymbols(scopeJson)) {
      String kind = JsonDeSers.getKind(symbol);
      ISymbolDeSer<?, ?> deSer = BarMill.globalScope().getSymbolDeSer(kind);

      if (null == deSer) {
        Log.warn("0xA1234xx81662 No DeSer found to deserialize symbol of kind `" + kind
            + "`. The following will be ignored: " + symbol);
        continue;
      }

      if ("de.monticore.symboltable.SymbolWithScopeOfUnknownKind".equals(kind)
          || "de.monticore.symboltable.SymbolWithScopeOfUnknownKind".equals(deSer.getSerializedKind())
      ) {
        SymbolWithScopeOfUnknownKind s0 = (SymbolWithScopeOfUnknownKind) deSer.deserialize(symbol);
        scope.add(s0);
        scope.addSubScope((IBarScope) s0.getSpannedScope());
      }
    }
  }

}
