/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
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

  protected static final String LOG_NAME = "SymTypeExpressionDeSer";

  /**
   * The singleton that DeSerializes all SymTypeExpressions.
   * It is stateless and can be reused recursively.
   */
  protected static SymTypeExpressionDeSer instance;
  // not realized as static delegator, but only as singleton

  protected SymTypeArrayDeSer symTypeArrayDeSer;

  protected SymTypePrimitiveDeSer symTypePrimitiveDeSer;

  protected SymTypeOfGenericsDeSer symTypeOfGenericsDeSer;

  protected SymTypeOfIntersectionDeSer symTypeOfIntersectionDeSer;

  protected SymTypeOfObjectDeSer symTypeOfObjectDeSer;

  protected SymTypeOfRegExDeSer symTypeOfRegExDeSer;

  protected SymTypeOfTupleDeSer symTypeOfTupleDeSer;

  protected SymTypeOfUnionDeSer symTypeOfUnionDeSer;

  protected SymTypeVariableDeSer symTypeVariableDeSer;

  protected SymTypeOfWildcardDeSer symTypeOfWildcardDeSer;

  protected SymTypeOfFunctionDeSer symTypeOfFunctionDeSer;

  protected SymTypeExpressionDeSer() {
    //this is a singleton, do not use constructor
    this.symTypeArrayDeSer = new SymTypeArrayDeSer();
    this.symTypePrimitiveDeSer = new SymTypePrimitiveDeSer();
    this.symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();
    this.symTypeOfIntersectionDeSer = new SymTypeOfIntersectionDeSer();
    this.symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();
    this.symTypeOfRegExDeSer = new SymTypeOfRegExDeSer();
    this.symTypeOfTupleDeSer = new SymTypeOfTupleDeSer();
    this.symTypeOfUnionDeSer = new SymTypeOfUnionDeSer();
    this.symTypeVariableDeSer = new SymTypeVariableDeSer();
    this.symTypeOfWildcardDeSer = new SymTypeOfWildcardDeSer();
    this.symTypeOfFunctionDeSer = new SymTypeOfFunctionDeSer();
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

  @Deprecated
  public static SymTypeExpression deserializeMember(String memberName, JsonObject json) {
    Log.debug("Using globalscope to deserialize \""
        + memberName + "\". This may create incorrect surrogates.", LOG_NAME);
    return deserializeMember(memberName, json, BasicSymbolsMill.globalScope());
  }

  public static SymTypeExpression deserializeMember(String memberName,
      JsonObject json, IBasicSymbolsScope enclosingScope) {
    return getInstance().deserialize(json.getMember(memberName), enclosingScope);
  }

  @Deprecated
  public static Optional<SymTypeExpression> deserializeOptionalMember(
      String memberName, JsonObject json) {
    Log.debug("Using globalscope to deserialize \""
        + memberName + "\". This may create incorrect surrogates.", LOG_NAME);
    return deserializeOptionalMember(memberName, json, BasicSymbolsMill.globalScope());
  }

    public static Optional<SymTypeExpression> deserializeOptionalMember(
        String memberName, JsonObject json, IBasicSymbolsScope enclosingScope) {
    if (json.hasMember(memberName)) {
      return Optional.of(getInstance()
          .deserialize(json.getMember(memberName), enclosingScope));
    }
    else {
      return Optional.empty();
    }
  }

  @Deprecated
  public static List<SymTypeExpression> deserializeListMember(String memberName, JsonObject json) {
    Log.debug("Using globalscope to deserialize \""
        + memberName + "\". This may create incorrect surrogates.", LOG_NAME);
    return deserializeListMember(memberName, json, BasicSymbolsMill.globalScope());
  }

  public static List<SymTypeExpression> deserializeListMember(
      String memberName, JsonObject json, IBasicSymbolsScope enclosingScope) {
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
    // this may not be the most optimal implementation,
    // however, we currently do not need more
    // void and null are stored as strings
    if(toSerialize.isNullType()) {
      return "\""+BasicSymbolsMill.NULL +"\"";
    }
    if(toSerialize.isVoidType()) {
      return "\""+BasicSymbolsMill.VOID +"\"";
    }
    if(toSerialize.isArrayType()) {
      return symTypeArrayDeSer.serialize((SymTypeArray)toSerialize);
    }
    if(toSerialize.isFunctionType()) {
      return symTypeOfFunctionDeSer.serialize((SymTypeOfFunction) toSerialize);
    }
    if(toSerialize.isGenericType()) {
      return symTypeOfGenericsDeSer.serialize((SymTypeOfGenerics) toSerialize);
    }
    if(toSerialize.isIntersectionType()) {
      return symTypeOfIntersectionDeSer.serialize((SymTypeOfIntersection) toSerialize);
    }
    if(toSerialize.isObjectType()) {
      return symTypeOfObjectDeSer.serialize((SymTypeOfObject) toSerialize);
    }
    if(toSerialize.isRegExType()) {
      return symTypeOfRegExDeSer.serialize((SymTypeOfRegEx) toSerialize);
    }
    if(toSerialize.isTupleType()) {
      return symTypeOfTupleDeSer.serialize((SymTypeOfTuple) toSerialize);
    }
    if(toSerialize.isUnionType()) {
      return symTypeOfUnionDeSer.serialize((SymTypeOfUnion)toSerialize);
    }
    if(toSerialize.isPrimitive()) {
      return symTypePrimitiveDeSer.serialize((SymTypePrimitive)toSerialize);
    }
    if(toSerialize.isTypeVariable()) {
      return symTypeVariableDeSer.serialize((SymTypeVariable) toSerialize);
    }
    if(toSerialize.isWildcard()) {
      return symTypeOfWildcardDeSer.serialize((SymTypeOfWildcard) toSerialize);
    }
    Log.error("0x823FD Internal error: Loading ill-structured SymTab: No way to serialize SymType;");
    return null;
  }

  /**
   * This method is a shortcut, as there are many symbolrules indicating that a symbol has a
   * a List of SymTypeExpressions as member.
   *
   * @param serializedMember
   * @return
   */
  public List<SymTypeExpression> deserializeList(JsonElement serializedMember) {
    List<SymTypeExpression> result = new ArrayList<>();
    for (JsonElement e : serializedMember.getAsJsonArray().getValues()) {
      result.add(deserialize(e));
    }
    return result;
  }

  public SymTypeExpression deserialize(String serialized) {
    return deserialize(JsonParser.parse(serialized));
  }

  @Deprecated
  public SymTypeExpression deserialize(JsonElement serialized) {
    Log.debug("Using globalscope to deserialize \n"
        + serialized + "\nThis may create incorrect surrogates.", LOG_NAME);
    return deserialize(serialized, BasicSymbolsMill.globalScope());
  }

  public SymTypeExpression deserialize(JsonElement serialized,
      IBasicSymbolsScope enclosingScope) {

    // void and null are stored as strings
    if (serialized.isJsonString()) {
      switch(serialized.getAsJsonString().getValue()){
        case BasicSymbolsMill.NULL:
          return SymTypeExpressionFactory.createTypeOfNull();
        case BasicSymbolsMill.VOID:
          return SymTypeExpressionFactory.createTypeVoid();
      }
    }

    // all other serialized SymTypeExrpressions are json objects with a kind
    if (serialized.isJsonObject()) {
      JsonObject o = serialized.getAsJsonObject();
      switch (JsonDeSers.getKind(o)) {
        case SymTypeArrayDeSer.SERIALIZED_KIND:
          return symTypeArrayDeSer.deserialize(o, enclosingScope);
        case SymTypePrimitiveDeSer.SERIALIZED_KIND:
          return symTypePrimitiveDeSer.deserialize(o);
        case SymTypeOfGenericsDeSer.SERIALIZED_KIND:
          return symTypeOfGenericsDeSer.deserialize(o, enclosingScope);
        case SymTypeOfIntersectionDeSer.SERIALIZED_KIND:
          return symTypeOfIntersectionDeSer.deserialize(o, enclosingScope);
        case SymTypeOfObjectDeSer.SERIALIZED_KIND:
          return symTypeOfObjectDeSer.deserialize(o, enclosingScope);
        case SymTypeOfRegExDeSer.SERIALIZED_KIND:
          return symTypeOfRegExDeSer.deserialize(o);
        case SymTypeOfTupleDeSer.SERIALIZED_KIND:
          return symTypeOfTupleDeSer.deserialize(o);
        case SymTypeOfUnionDeSer.SERIALIZED_KIND:
          return symTypeOfUnionDeSer.deserialize(o, enclosingScope);
        case SymTypeVariableDeSer.SERIALIZED_KIND:
          return symTypeVariableDeSer.deserialize(o, enclosingScope);
        case SymTypeOfWildcardDeSer.SERIALIZED_KIND:
          return symTypeOfWildcardDeSer.deserialize(o, enclosingScope);
        case SymTypeOfFunctionDeSer.SERIALIZED_KIND:
          return symTypeOfFunctionDeSer.deserialize(o, enclosingScope);
      }
    }

    Log.error(
        "0x823FE Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
            + serialized);
    return null;
  }

}
