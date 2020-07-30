/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This DeSer reailizes serialization and deserialization of SymTypeExpressions.
 */
public class SymTypeExpressionDeSer {

  /**
   * The singleton that DeSerializes all SymTypeExpressions.
   * It is stateless and can be reused recursively.
   */
  protected static SymTypeExpressionDeSer instance;
  // not realized as static delegator, but only as singleton

  protected SymTypeArrayDeSer symTypeArrayDeSer;

  protected SymTypeConstantDeSer symTypeConstantDeSer;

  protected SymTypeOfGenericsDeSer symTypeOfGenericsDeSer;

  protected SymTypeOfObjectDeSer symTypeOfObjectDeSer;

  protected SymTypeVariableDeSer symTypeVariableDeSer;

  protected SymTypeOfWildcardDeSer symTypeOfWildcardDeSer;

  protected SymTypeExpressionDeSer() {
    //this is a singleton, do not use constructor
    this.symTypeArrayDeSer = new SymTypeArrayDeSer();
    this.symTypeConstantDeSer = new SymTypeConstantDeSer();
    this.symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();
    this.symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();
    this.symTypeVariableDeSer = new SymTypeVariableDeSer();
    this.symTypeOfWildcardDeSer = new SymTypeOfWildcardDeSer();
  }

  public static void serializeMember(JsonPrinter printer, String memberName,
      SymTypeExpression member) {
    printer.memberJson(memberName, member.printAsJson());
  }

  public static void serializeMember(JsonPrinter printer, String memberName,
      Optional<SymTypeExpression> member) {
    if (member.isPresent()) {
      printer.memberJson(memberName, member.get().printAsJson());
    }
  }

  public static void serializeMember(JsonPrinter printer, String memberName,
      List<SymTypeExpression> member) {
    printer.array(memberName, member, SymTypeExpression::printAsJson);
  }

  public static SymTypeExpression deserializeMember(String memberName, JsonObject json,
      IBasicSymbolsScope enclosingScope) {
    return getInstance().deserialize(json.getMember(memberName), enclosingScope);
  }

  public static Optional<SymTypeExpression> deserializeOptionalMember(String memberName,
      JsonObject json, IBasicSymbolsScope enclosingScope) {
    if (json.hasMember(memberName)) {
      return Optional.of(getInstance().deserialize(json.getMember(memberName), enclosingScope));
    }
    else {
      return Optional.empty();
    }
  }

  public static List<SymTypeExpression> deserializeListMember(String memberName, JsonObject json,
      IBasicSymbolsScope enclosingScope) {
    List<SymTypeExpression> result = new ArrayList<>();
    if (json.hasMember(memberName)) {
      for (JsonElement e : json.getArrayMember(memberName)) {
        result.add(getInstance().deserialize(e, enclosingScope));
      }
    }
    return result;
  }

  public static SymTypeExpression deserializeMember(String memberName, JsonObject json,
      IOOSymbolsScope enclosingScope) {
    return getInstance().deserialize(json.getMember(memberName), enclosingScope);
  }

  public static Optional<SymTypeExpression> deserializeOptionalMember(String memberName,
      JsonObject json, IOOSymbolsScope enclosingScope) {
    if (json.hasMember(memberName)) {
      return Optional.of(getInstance().deserialize(json.getMember(memberName), enclosingScope));
    }
    else {
      return Optional.empty();
    }
  }

  public static List<SymTypeExpression> deserializeListMember(String memberName, JsonObject json,
      IOOSymbolsScope enclosingScope) {
    List<SymTypeExpression> result = new ArrayList<>();
    if (json.hasMember(memberName)) {
      for (JsonElement e : json.getArrayMember(memberName)) {
        result.add(getInstance().deserialize(e, enclosingScope));
      }
    }
    return result;
  }

  public static SymTypeExpressionDeSer getInstance() {
    if (null == instance) {
      instance = new SymTypeExpressionDeSer();
    }
    return instance;
  }

  /**
   * This method can be used to set the instance of the SymTypeExpressionDeSer to a custom suptype
   *
   * @param theInstance
   */
  public static void setInstance(SymTypeExpressionDeSer theInstance) {
    if (null == theInstance) {  //in this case, "reset" to default type
      instance = new SymTypeExpressionDeSer();
    }
    else {
      instance = theInstance;
    }
  }

  public String serialize(SymTypeExpression toSerialize) {
    return toSerialize.printAsJson();
  }

  /**
   * This method is a shortcut, as there are many symbolrules indicating that a symbol has a
   * a List of SymTypeExpressions as member.
   *
   * @param serializedMember
   * @param enclosingScope
   * @return
   */
  public List<SymTypeExpression> deserializeList(JsonElement serializedMember,
      IBasicSymbolsScope enclosingScope) {
    List<SymTypeExpression> result = new ArrayList<>();
    for (JsonElement e : serializedMember.getAsJsonArray().getValues()) {
      result.add(deserialize(e, enclosingScope));
    }
    return result;
  }

  public SymTypeExpression deserialize(String serialized, IBasicSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeExpression deserialize(JsonElement serialized,
      IBasicSymbolsScope enclosingScope) {
    // void and null are stored as strings
    if (serialized.isJsonString()) {
      String value = serialized.getAsJsonString().getValue();
      if (value.equals(DefsTypeBasic._nullTypeString)) {
        return SymTypeExpressionFactory.createTypeOfNull();
      }
      else if (value.equals(DefsTypeBasic._voidTypeString)) {
        return SymTypeExpressionFactory.createTypeVoid();
      }
    }
    else if (enclosingScope instanceof IOOSymbolsScope) {
      return deserialize(serialized, (IOOSymbolsScope) enclosingScope);
    }
    Log.error(
        "0x823F3 Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
            + serialized);
    return null;
  }

  public SymTypeExpression deserialize(String serialized, IOOSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeExpression deserialize(JsonElement serialized, IOOSymbolsScope enclosingScope) {

    // void and null are stored as strings
    if (serialized.isJsonString()) {
      String value = serialized.getAsJsonString().getValue();

      if (value.equals(DefsTypeBasic._nullTypeString)) {
        return SymTypeExpressionFactory.createTypeOfNull();
      }
      else if (value.equals(DefsTypeBasic._voidTypeString)) {
        return SymTypeExpressionFactory.createTypeVoid();
      }
      else {
        Log.error(
            "0x823F3 Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
                + serialized);
        return null;
      }
    }

    // all other serialized SymTypeExrpressions are json objects with a kind
    if (JsonDeSers.isCorrectDeSerForKind(symTypeArrayDeSer.SERIALIZED_KIND, serialized)) {
      return symTypeArrayDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonDeSers.isCorrectDeSerForKind(symTypeConstantDeSer.SERIALIZED_KIND, serialized)) {
      return symTypeConstantDeSer.deserialize(serialized);
    }
    else if (JsonDeSers.isCorrectDeSerForKind(symTypeOfGenericsDeSer.SERIALIZED_KIND, serialized)) {
      return symTypeOfGenericsDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonDeSers.isCorrectDeSerForKind(symTypeOfObjectDeSer.SERIALIZED_KIND, serialized)) {
      return symTypeOfObjectDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonDeSers.isCorrectDeSerForKind(symTypeVariableDeSer.SERIALIZED_KIND, serialized)) {
      return symTypeVariableDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonDeSers.isCorrectDeSerForKind(symTypeOfWildcardDeSer.SERIALIZED_KIND, serialized)) {
      return symTypeOfWildcardDeSer.deserialize(serialized, enclosingScope);
    }
    else {
      Log.error(
          "0x823FE Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
              + serialized);
      return null;
    }
  }

}
