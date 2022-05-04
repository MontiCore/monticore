/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.generating.templateengine.HookPoint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Built-In Type Serializations
 */
public class BITSer {

  protected Map<String, BITSerStrategy> bitsers = new LinkedHashMap<>();

  public BITSer() {
    init();
  }

  public void init() {

    BITSerStrategy bool = new BITSerStrategy("Boolean", "false");
    bitsers.put("boolean", bool);
    bitsers.put("Boolean", bool);
    bitsers.put("java.lang.Boolean", bool);

    BITSerStrategy string = new BITSerStrategy("String", "\"\"");
    bitsers.put("String", string);
    bitsers.put("java.lang.String", string);

    BITSerStrategy _int = new BITSerStrategy("Integer", "0");
    bitsers.put("int", _int);
    bitsers.put("java.lang.Integer", _int);
    bitsers.put("Integer", _int);

    BITSerStrategy _double = new BITSerStrategy("Double", "0.0");
    bitsers.put("double", _double);
    bitsers.put("java.lang.Double", _double);
    bitsers.put("Double", _double);

    BITSerStrategy _float = new BITSerStrategy("Float", "0.0f");
    bitsers.put("float", _float);
    bitsers.put("java.lang.Float", _float);
    bitsers.put("Float", _float);

    BITSerStrategy _long = new BITSerStrategy("Long", "0L");
    bitsers.put("long", _long);
    bitsers.put("java.lang.Long", _long);
    bitsers.put("Long", _long);
  }

  /**
   * Return a hook for the hook point of a serialization method implementation, if a built-in
   * serialization exists. Otherwise, return an empty optional.
   *
   * @param attrType
   * @param attrName
   * @return
   */
  public Optional<HookPoint> getSerialHook(String attrType, String attrName) {
    if (bitsers.containsKey(attrType)) {
      return Optional.of(bitsers.get(attrType).getSerialHook(attrName));
    }

    Optional<String> listGenType = getListGenType(attrType);
    if (listGenType.isPresent() && bitsers.containsKey(listGenType.get())) {
      return Optional.of(bitsers.get(listGenType.get()).getListSerialHook(attrName));
    }

    Optional<String> optGenType = getOptionalGenType(attrType);
    if (optGenType.isPresent() && bitsers.containsKey(optGenType.get())) {
      return Optional.of(bitsers.get(optGenType.get()).getOptSerialHook(attrName));
    }

    return Optional.empty();
  }

  /**
   * Return a hook for the hook point of a deserialization method implementation, if a built-in
   * deserialization exists. Otherwise, return an empty optional.
   *
   * @param attrType
   * @param attrName
   * @param jsonName
   * @return
   */
  public Optional<HookPoint> getDeserialHook(String attrType, String attrName, String jsonName) {
    if (bitsers.containsKey(attrType)) {
      return Optional.of(bitsers.get(attrType).getDeserialHook(jsonName, attrName));
    }

    Optional<String> listGenType = getListGenType(attrType);
    if (listGenType.isPresent() && bitsers.containsKey(listGenType.get())) {
      return Optional.of(bitsers.get(listGenType.get()).getListDeserialHook(jsonName, attrName));
    }

    Optional<String> optGenType = getOptionalGenType(attrType);
    if (optGenType.isPresent() && bitsers.containsKey(optGenType.get())) {
      return Optional.of(bitsers.get(optGenType.get()).getOptDeserialHook(jsonName, attrName));
    }

    return Optional.empty();
  }

  public static Optional<String> getListGenType(String attrType) {
    if (attrType.startsWith("java.util.List<") && attrType.endsWith(">")) {
      attrType = attrType.substring("java.util.List<".length(), attrType.length() - 1);
      return Optional.of(attrType);
    }
    else if (attrType.startsWith("List<") && attrType.endsWith(">")) {
      attrType = attrType.substring("List<".length(), attrType.length() - 1);
      return Optional.of(attrType);
    }
    return Optional.empty();
  }

  public static Optional<String> getOptionalGenType(String attrType) {
    if (attrType.startsWith("java.util.Optional<") && attrType.endsWith(">")) {
      attrType = attrType.substring("java.util.Optional<".length(), attrType.length() - 1);
      return Optional.of(attrType);
    }
    else if (attrType.startsWith("Optional<") && attrType.endsWith(">")) {
      attrType = attrType.substring("Optional<".length(), attrType.length() - 1);
      return Optional.of(attrType);
    }
    return Optional.empty();
  }

}
