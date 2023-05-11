/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

public class PortSymbolDeSer extends PortSymbolDeSerTOP {

  @Override
  protected void serializeType(@NonNull SymTypeExpression type, @NonNull CompSymbolsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "type", type);
  }

  @Override
  protected void serializeTiming(@NonNull Timing timing, @NonNull CompSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().member("timing", timing.getName());
  }

  @Override
  protected SymTypeExpression deserializeType(@NonNull JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
  }

  @Override
  protected Timing deserializeTiming(@NonNull JsonObject symbolJson) {
    String value = symbolJson.getStringMember("timing");
    Optional<Timing> timing = Timing.of(value);

    if (timing.isPresent()) {
      return timing.get();
    } else {
      Log.error("0xD0100 Malformed json, unsupported timing '" + value + "'");
      return Timing.DEFAULT;
    }
  }
}
