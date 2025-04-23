/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.stereotypesymbols._symboltable;

import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.stereotypes.StereoValueType;
import de.monticore.symboltable.stereotypes.StereoValueTypeDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MCStereotypeSymbolDeSer extends MCStereotypeSymbolDeSerTOP {

  public static final String ANNOTATED_ELEMENT = "annotatedElement";
  public static final String ALLOWED_VALUE_TYPES = "allowedValueTypes";

  @Override
  protected void serializeAnnotatedElement(Class<? extends ISymbol> annotatedElement,
                                           StereotypeSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().member(ANNOTATED_ELEMENT, annotatedElement.getCanonicalName());
  }

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
  protected Class<? extends ISymbol> deserializeAnnotatedElement(JsonObject json) {
    String annotatedElement = json.getStringMember(ANNOTATED_ELEMENT);
    try {
      Class<?> clazz = Class.forName(annotatedElement);
      if (ISymbol.class.isAssignableFrom(clazz)) {
        return (Class<? extends ISymbol>) clazz;
      }
    } catch (ClassNotFoundException e) {
      // Error will be printed below
    }

    Log.error(
      "0x82400 Internal error: Loading ill-structured SymTab: Unknown serialization of" +
      "StereoValueType: " + json);
    return null;
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
