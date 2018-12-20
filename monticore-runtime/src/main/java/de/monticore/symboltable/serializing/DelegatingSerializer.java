/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class DelegatingSerializer implements ISerialization<Object> {
  
  protected List<ISerialization<?>> serializers;
  
  /**
   * Constructor for de.monticore.symboltable.serializing.DelegatingSymbolSerializer
   * 
   * @param serializers
   */
  public DelegatingSerializer(List<ISerialization<?>> serializers) {
    this.serializers = serializers;
  }
  
  /**
   * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
   * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
   */
  @Override
  public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String kind = jsonObject.get(CLASS).getAsString();
    for (ISerialization<?> s : serializers) {
      if (kind.equals(s.getSerializedClass().getName())) {
        return context.deserialize(json, s.getSerializedClass());
      }
    }
    throw new JsonParseException("Could not find a registered deserializer to delegate "+typeOfT+" to.");
  }
  
  /**
   * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
   * com.google.gson.JsonSerializationContext)
   */
  @Override
  public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
    throw new JsonParseException("Do not invoke DelegatingSerializer#serialize()!");
  }
  
  /**
   * @see de.monticore.symboltable.serializing.ISerialization#getSerializedClass()
   */
  @Override
  public Class<Object> getSerializedClass() {
    throw new JsonParseException("Do not invoke DelegatingSerializer#getSerializedClass()!");
  }
  
}
