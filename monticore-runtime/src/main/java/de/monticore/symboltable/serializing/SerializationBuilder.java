/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.util.Collection;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
@Deprecated 
public class SerializationBuilder {
  
  protected JsonSerializationContext context;
  
  protected JsonObject json;
  
  @Deprecated //Constructor below should be used instead when a new JsonObject shall be created
  public SerializationBuilder(JsonObject json, JsonSerializationContext context) {
    this.json = json;
    this.context = context;
  }
  
  public SerializationBuilder(JsonSerializationContext context) {
    this.json = new JsonObject();
    this.context = context;
  }
  
  public SerializationBuilder add(String key, Object o) {
    json.add(key, context.serialize(o));
    return this;
  }
  
  public SerializationBuilder add(String key, String o) {
    if (o.length() > 0) {
      json.addProperty(key, o);
    }
    return this;
  }
  
  public SerializationBuilder add(String key, boolean o) {
    json.addProperty(key, o);
    return this;
  }
  
  public SerializationBuilder add(String key, int o) {
    json.addProperty(key, o);
    return this;
  }
  
  public SerializationBuilder add(String key, Collection<?> o) {
    if (!o.isEmpty()) {
      json.add(key, context.serialize(o));
    }
    return this;
  }
  
  public SerializationBuilder add(String key, Optional<?> o) {
    if (o.isPresent()) {
      json.add(key, context.serialize(o.get()));
    }
    return this;
  }
  
  public JsonObject build() {
    return json;
  }
  
  @Deprecated //TODO: remove if 5.0.3 is released
  public SerializationBuilder addOnlyIfTrue(String key, boolean o) {
    if (o) {
      json.addProperty(key, o);
    }
    return this;
  }
  
  @Deprecated //TODO: remove if 5.0.3 is released
  public SerializationBuilder addOnlyIfFalse(String key, boolean o) {
    if (!o) {
      json.addProperty(key, o);
    }
    return this;
  }
  
}
