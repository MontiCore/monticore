/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import de.monticore.symboltable.serialization.JsonPrinter;

/**
 * Json Objects contain members in form of key-value pairs. The key is a (unique) String, and the
 * value any JsonElement.
 */
public class JsonObject implements JsonElement {
  
  protected Map<String, JsonElement> members;
  
  public JsonObject() {
    this.members = new HashMap<>();
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
  public Map<String, JsonElement> getMembers() {
    return this.members;
  }
  
  /**
   * @param members the attributes to set
   */
  public void setMembers(Map<String, JsonElement> members) {
    this.members = members;
  }
  
  /**
   * @return
   * @see java.util.Map#size()
   */
  public int size() {
    return this.members.size();
  }
  
  /**
   * @return
   * @see java.util.Map#isEmpty()
   */
  public boolean isEmpty() {
    return this.members.isEmpty();
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey(String key) {
    return this.members.containsKey(key);
  }
  
  /**
   * @param value
   * @return
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  public boolean containsValue(JsonElement value) {
    return this.members.containsValue(value);
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  public JsonElement get(String key) {
    return this.members.get(key);
  }
  
  public Optional<String> getStringOpt(String key) {
    if (members.containsKey(key) && members.get(key).isJsonString()) {
      Optional.ofNullable(members.get(key).getAsJsonString().getValue());
    }
    return Optional.empty();
  }
  
  /**
   * @param key
   * @return
   */
  public Optional<Boolean> getBooleanOpt(String key) {
    if (members.containsKey(key) && members.get(key).isJsonBoolean()) {
      Optional.ofNullable(members.get(key).getAsJsonBoolean().getValue());
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
    return this.members.put(key, value);
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#remove(java.lang.Object)
   */
  public JsonElement remove(String key) {
    return this.members.remove(key);
  }
  
  /**
   * @return
   * @see java.util.Map#keySet()
   */
  public Set<String> keySet() {
    return this.members.keySet();
  }
  
  /**
   * @return
   * @see java.util.Map#values()
   */
  public Collection<JsonElement> values() {
    return this.members.values();
  }
  
  /**
   * @return
   * @see java.util.Map#entrySet()
   */
  public Set<Entry<String, JsonElement>> entrySet() {
    return this.members.entrySet();
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    for (String s : members.keySet()) {
      printer.memberJson(s, members.get(s).toString());
    }
    printer.endObject();
    return printer.getContent();
  }
  
}
