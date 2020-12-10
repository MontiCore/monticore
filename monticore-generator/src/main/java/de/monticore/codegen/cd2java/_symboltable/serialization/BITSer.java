/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.HookPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BITSer {

  protected Map<String, BITSerStrategy> bitsers = new HashMap<>();

  public BITSer() {
    init();
  }

  public void init() {
    bitsers.put("boolean", new BITSerStrategy("Boolean"));
    bitsers.put("Boolean", new BITSerStrategy("Boolean"));
    bitsers.put("java.lang.Boolean", new BITSerStrategy("Boolean"));

    bitsers.put("String", new BITSerStrategy("String"));
    bitsers.put("java.lang.String", new BITSerStrategy("String"));

    bitsers.put("int", new BITSerStrategy("Integer"));
    bitsers.put("java.lang.Integer", new BITSerStrategy("Integer"));
    bitsers.put("Integer", new BITSerStrategy("Integer"));

    bitsers.put("double", new BITSerStrategy("Double"));
    bitsers.put("java.lang.Double", new BITSerStrategy("Double"));
    bitsers.put("Double", new BITSerStrategy("Double"));

    bitsers.put("float", new BITSerStrategy("Float"));
    bitsers.put("java.lang.Float", new BITSerStrategy("Float"));
    bitsers.put("Float", new BITSerStrategy("Float"));

    bitsers.put("long", new BITSerStrategy("Long"));
    bitsers.put("java.lang.Long", new BITSerStrategy("Long"));
    bitsers.put("Long", new BITSerStrategy("Long"));
  }

  /**
   * Return a hook for the hook point of a serialization method implementation, if a built-in
   * serialization exists. Otherwise, return an empty optional.
   * @param attrType
   * @param attrName
   * @return
   */
  public Optional<HookPoint> getSerialHook(String attrType, String attrName) {
    if (attrType.startsWith("java.util.List<") && attrType.endsWith(">")) {
      attrType = attrType.substring("java.util.List<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getListSerialHook(attrName));
      }
    }
    else if (attrType.startsWith("List<") && attrType.endsWith(">")) {
      attrType = attrType.substring("List<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getListSerialHook(attrName));
      }
    }
    else if (attrType.startsWith("java.util.Optional<") && attrType.endsWith(">")) {
      attrType = attrType.substring("java.util.Optional<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getOptSerialHook(attrName));
      }
    }
    else if (attrType.startsWith("Optional<") && attrType.endsWith(">")) {
      attrType = attrType.substring("Optional<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getOptSerialHook(attrName));
      }
    }
    else {
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getSerialHook(attrName));
      }
    }
    return Optional.empty();
  }

  /**
   * Return a hook for the hook point of a deserialization method implementation, if a built-in
   * deserialization exists. Otherwise, return an empty optional.
   * @param attrType
   * @param attrName
   * @param jsonName
   * @return
   */
  public Optional<HookPoint> getDeserialHook(String attrType, String attrName, String jsonName) {
    if (attrType.startsWith("java.util.List<") && attrType.endsWith(">")) {
      attrType = attrType.substring("java.util.List<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getListDeserialHook(jsonName, attrName));
      }
    }
    else if (attrType.startsWith("List<") && attrType.endsWith(">")) {
      attrType = attrType.substring("List<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getListDeserialHook(jsonName, attrName));
      }
    }
    else if (attrType.startsWith("java.util.Optional<") && attrType.endsWith(">")) {
      attrType = attrType.substring("java.util.Optional<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getOptDeserialHook(jsonName, attrName));
      }
    }
    else if (attrType.startsWith("Optional<") && attrType.endsWith(">")) {
      attrType = attrType.substring("Optional<".length(), attrType.length() - 1);
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getOptDeserialHook(jsonName, attrName));
      }
    }
    else {
      if (bitsers.containsKey(attrType)) {
        return Optional.of(bitsers.get(attrType).getDeserialHook(jsonName, attrName));
      }
    }
    return Optional.empty();
  }

}
