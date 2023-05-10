/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.NoSuchElementException;

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
    String timingString = symbolJson.getStringMember("timing");

    return Timing.of(timingString).orElseThrow(() ->
      new NoSuchElementException(String.format("Malformed Json: no such timing '%s'", timingString)));
  }
}
