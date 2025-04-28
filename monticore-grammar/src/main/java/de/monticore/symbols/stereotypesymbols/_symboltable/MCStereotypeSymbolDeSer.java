/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.stereotypesymbols._symboltable;

import de.monticore.symboltable.stereotypes.StereoValueType;
import de.monticore.symboltable.stereotypes.StereoValueTypeDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MCStereotypeSymbolDeSer extends MCStereotypeSymbolDeSerTOP {

  public static final String ALLOWED_VALUE_TYPES = "allowedValueTypes";

  @Override
  protected void serializeAllowedValueTypes(List<StereoValueType> allowedValueTypes,
                                            StereotypeSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray(ALLOWED_VALUE_TYPES);
    for (StereoValueType type : allowedValueTypes) {
      s2j.getJsonPrinter().addToArray(StereoValueTypeDeSer.serializeStereoValueType(type));
    }
    s2j.getJsonPrinter().endArray();
  }

  @Override
  protected List<StereoValueType> deserializeAllowedValueTypes(JsonObject json) {
    return json.getArrayMemberOpt(ALLOWED_VALUE_TYPES)
      .orElse(Collections.emptyList())
      .stream()
      .map(StereoValueTypeDeSer::deserializeStereoValueType)
      .collect(Collectors.toList());
  }
}
