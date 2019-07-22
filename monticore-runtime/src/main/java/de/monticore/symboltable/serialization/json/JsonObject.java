/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;

import de.monticore.symboltable.serialization.JsonPrinter;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonObject implements JsonElement {
  
  protected Map<String, JsonElement> attributes;
  
  public JsonObject() {
    this.attributes = new HashMap<>();
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#isJsonObject()
   */
  @Override
  public boolean isJsonObject() {
    return true;
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#getAsJsonObject()
   */
  @Override
  public JsonObject getAsJsonObject() {
    return this;
  }
  
  /**
   * @return attributes
   */
  public Map<String, JsonElement> getAttributes() {
    return this.attributes;
  }
  
  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(Map<String, JsonElement> attributes) {
    this.attributes = attributes;
  }
  
  /**
   * TODO: Write me!
   *
   * @return
   * @see java.util.Map#size()
   */
  public int size() {
    return this.attributes.size();
  }
  
  /**
   * TODO: Write me!
   *
   * @return
   * @see java.util.Map#isEmpty()
   */
  public boolean isEmpty() {
    return this.attributes.isEmpty();
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey(String key) {
    return this.attributes.containsKey(key);
  }
  
  /**
   * @param value
   * @return
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  public boolean containsValue(JsonElement value) {
    return this.attributes.containsValue(value);
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  public JsonElement get(String key) {
    return this.attributes.get(key);
  }
  
  public Optional<String> getStringOpt(String key) {
    if(attributes.containsKey(key)) {
      Optional.ofNullable(attributes.get(key).getAsJsonString().getValue());
    }
    return Optional.empty();
  }
  
  public Optional<Boolean> getBooleanOpt(String key) {
    if(attributes.containsKey(key)) {
      Optional.ofNullable(attributes.get(key).getAsJsonBoolean().getValue());
    }
    return Optional.empty();
  }
  
  /**
   * @param key
   * @param value
   * @return
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public JsonElement put(String key, JsonElement value) {
    return this.attributes.put(key, value);
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#remove(java.lang.Object)
   */
  public JsonElement remove(String key) {
    return this.attributes.remove(key);
  }
  
  /**
   * @return
   * @see java.util.Map#keySet()
   */
  public Set<String> keySet() {
    return this.attributes.keySet();
  }
  
  /**
   * @return
   * @see java.util.Map#values()
   */
  public Collection<JsonElement> values() {
    return this.attributes.values();
  }
  
  /**
   * @return
   * @see java.util.Map#entrySet()
   */
  public Set<Entry<String, JsonElement>> entrySet() {
    return this.attributes.entrySet();
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    for (String s : attributes.keySet()) {
      printer.attribute(s, attributes.get(s).toString());
    }
    printer.endObject();
    return printer.getContent();
  }
  
}
