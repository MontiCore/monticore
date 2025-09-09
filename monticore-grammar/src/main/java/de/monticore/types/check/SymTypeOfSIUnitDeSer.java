/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class SymTypeOfSIUnitDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfSIUnit";
  protected static final String SERIALIZED_NUMERATOR = "numerator";
  protected static final String SERIALIZED_DENOMINATOR = "denominator";

  // SIUnitBasic
  protected static final String SERIALIZED_DIMENSION = "dimension";
  protected static final String SERIALIZED_PREFIX = "prefix";
  protected static final String SERIALIZED_EXPONENT = "exponent";

  public String serialize(SymTypeOfSIUnit toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);

    jp.beginArray(SERIALIZED_NUMERATOR);
    toSerialize.getNumerator().stream()
        .map(this::serialize)
        .forEach(jp::valueJson);
    jp.endArray();

    jp.beginArray(SERIALIZED_DENOMINATOR);
    toSerialize.getDenominator().stream()
        .map(this::serialize)
        .forEach(jp::valueJson);
    jp.endArray();

    jp.endObject();
    return jp.getContent();
  }

  protected String serialize(SIUnitBasic toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();

    jp.member(SERIALIZED_DIMENSION, toSerialize.getDimension());
    jp.member(SERIALIZED_PREFIX, toSerialize.getPrefix());
    jp.memberNoDef(SERIALIZED_EXPONENT, toSerialize.getExponent());

    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfSIUnit deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfSIUnit deserialize(JsonObject serialized) {
    List<SIUnitBasic> numerator = new ArrayList<>();
    if (serialized.hasArrayMember(SERIALIZED_NUMERATOR)) {
      for (JsonElement serializedBasic : serialized.getArrayMember(SERIALIZED_NUMERATOR)) {
        numerator.add(deserializeSIUnitBasic(serializedBasic.getAsJsonObject()));
      }
    }
    List<SIUnitBasic> denominator = new ArrayList<>();
    if (serialized.hasArrayMember(SERIALIZED_DENOMINATOR)) {
      for (JsonElement serializedBasic : serialized.getArrayMember(SERIALIZED_DENOMINATOR)) {
        denominator.add(deserializeSIUnitBasic(serializedBasic.getAsJsonObject()));
      }
    }
    return SymTypeExpressionFactory.createSIUnit(numerator, denominator);
  }

  protected SIUnitBasic deserializeSIUnitBasic(JsonObject serialized) {
    if (serialized.hasMember(SERIALIZED_DIMENSION) &&
        serialized.hasMember(SERIALIZED_EXPONENT)
    ) {
      SIUnitBasic result;
      String dimension = serialized.getStringMember(SERIALIZED_DIMENSION);
      int exponent = serialized.getIntegerMember(SERIALIZED_EXPONENT);
      if (serialized.hasMember(SERIALIZED_PREFIX)) {
        String prefix = serialized.getStringMember(SERIALIZED_PREFIX);
        result = SymTypeExpressionFactory.createSIUnitBasic(dimension, prefix, exponent);
      }
      else {
        result = SymTypeExpressionFactory.createSIUnitBasic(dimension, exponent);
      }
      return result;
    }
    else {
      Log.error("0x9E2E0 internal error:"
          + " loading ill-structured SymTab: missing" + SERIALIZED_DIMENSION
          + ", " + SERIALIZED_PREFIX
          + ", or " + SERIALIZED_EXPONENT
          + " of SIUnitBasic " + serialized);
      return null;
    }
  }

}
