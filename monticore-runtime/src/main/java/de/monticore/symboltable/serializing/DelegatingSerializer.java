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

public class DelegatingSerializer<T> implements ISerialization<T> {
  
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
  public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String kind = jsonObject.get(KIND).getAsString();
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
  public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
    String actualKind = src.getClass().getName();
    for (ISerialization<?> s : serializers) {
      String serKind = s.getSerializedClass().getName();
      if (actualKind.equals(serKind)) {
        return context.serialize(src, s.getSerializedClass());
      }
    }
    throw new JsonParseException("Could not find a registered serializer to delegate "+typeOfSrc+" to.");
  }
  
  /**
   * @see de.monticore.symboltable.serializing.ISerialization#getSerializedClass()
   */
  @Override
  public Class<T> getSerializedClass() {
    throw new JsonParseException("Do not invoke DelegatingSerializer#getSerializedClass()!");
  }
  
}
