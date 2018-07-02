/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * Realizes (de)serializing {@link ArtifactScope} instances via JSON.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonArtifactScopeSerializer implements IArtifactScopeSerializer {
  
  protected GsonBuilder gson;
  
  public JsonArtifactScopeSerializer() {
    gson = new GsonBuilder();
    // TODO: Further configuration might be necessary
    gson.serializeSpecialFloatingPointValues();
    gson.addSerializationExclusionStrategy(getExcusionStrategy());
    gson.registerTypeAdapter(ArtifactScope.class, getArtifactScopeInstanceCreator());
  }
  
  /**
   * @see de.monticore.symboltable.serializing.IArtifactScopeSerializer#registerDeserializer(java.lang.Class,
   * de.monticore.symboltable.serializing.ISymbolDeserializer)
   */
  @Override
  public void registerDeserializer(Class<?> clazz,
      JsonDeserializer<?> deserializer) {
    gson.registerTypeAdapter(clazz, deserializer);
  }
  
  /**
   * @see de.monticore.symboltable.serializing.scopes.IArtifactScopeSerializer#serialize(de.monticore.symboltable.ArtifactScope)
   */
  @Override
  public Optional<String> serialize(ArtifactScope as) {
    String serialize;
    try {
      serialize = getGson().toJson(as);
    }
    catch (Exception e) {
      Log.info("Serialization of symbols in \"" + as.toString() + "\" failed.", e,
          "JsonArtifactScopeSerializer");
      return Optional.empty();
    }
    
    return Optional.ofNullable(serialize);
  }
  
  /**
   * @see de.monticore.symboltable.serializing.scopes.IArtifactScopeSerializer#deserialize(java.lang.String)
   */
  @Override
  public Optional<ArtifactScope> deserialize(String content) {
    ArtifactScope fromJson;
    try {
      fromJson = getGson().fromJson(content, ArtifactScope.class);
    }
    catch (Exception e) {
      Log.info("Deserialization of symbols from \"" + content + "\" failed.", e,
          "JsonArtifactScopeSerializer");
      return Optional.empty();
    }
    
    return Optional.ofNullable(fromJson);
  }
  
  protected Gson getGson() {
    return gson.create();
  }
  
  public <S extends Symbol> void addInstanceCreator(Class<S> clazz, InstanceCreator<S> ic) {
    gson.registerTypeAdapter(clazz, ic);
  }
  
  public ExclusionStrategy getExcusionStrategy() {
    return new ExclusionStrategy() {
      
      @Override
      public boolean shouldSkipField(FieldAttributes f) {
        return !isRelevantField(f);
      }
      
      @Override
      public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }
    };
  }
  
  public InstanceCreator<ArtifactScope> getArtifactScopeInstanceCreator() {
    return new InstanceCreator<ArtifactScope>() {
      
      @Override
      public ArtifactScope createInstance(Type type) {
        return new ArtifactScope(Optional.empty(), type.getClass().getPackage().toString(),
            new ArrayList<>());
      }
      
    };
  }
  
  public boolean isRelevantField(FieldAttributes f) {
    List<String> relevantFields = ImmutableList.of("symbols", "subScopes", "exportsSymbols",
        "isShadowingScope", "name", "packageName");
    return relevantFields.contains(f.getName());
  }
  
}
