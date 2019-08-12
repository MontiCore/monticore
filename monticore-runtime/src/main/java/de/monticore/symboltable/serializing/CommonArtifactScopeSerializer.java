/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serializing;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.monticore.symboltable.ArtifactScope;
import de.se_rwth.commons.logging.Log;

/**
 * Realizes (de)serializing {@link ArtifactScope} instances via JSON. For this, it maintains a list
 * of {@link ISerialization} instances to (de)serialize individual scopes and symbols. This class is
 * the superclass of all generated serializers for languages.
 */
@Deprecated
public abstract class CommonArtifactScopeSerializer implements IArtifactScopeSerializer {
  
  protected GsonBuilder gson;
  
  
  protected String fileExtension;
  
  public CommonArtifactScopeSerializer() {
    gson = new GsonBuilder();
    // TODO: Further configuration might be necessary
    gson.serializeSpecialFloatingPointValues();
    
    fileExtension = ".sym"; //Will be overridden by generated concrete class 
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
  
}
