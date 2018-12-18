/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * Realizes (de)serializing {@link ArtifactScope} instances via JSON. For this, it maintains a list
 * of {@link ISerialization} instances to (de)serialize individual scopes and symbols. This class is
 * the superclass of all generated serializers for languages.
 */
public abstract class CommonLanguageSerialization implements IArtifactScopeSerializer {
  
  protected GsonBuilder gson;
  
  protected List<ISerialization<?>> serializers;
  
  protected String fileExtension;
  
  public CommonLanguageSerialization() {
    serializers = getSerializers();
    
    gson = new GsonBuilder();
    // TODO: Further configuration might be necessary
    gson.serializeSpecialFloatingPointValues();
    
    registerSerializers();
    
    fileExtension = ".sym"; //Will be overridden by generated concrete class 
  }
  
  /**
   * This method is called to obtain a list of language-specific {@link ISerialization} instances
   * for each symbol and scope of the language.
   * 
   * @return
   */
  protected abstract List<ISerialization<?>> getSerializers();
  
  protected void registerSerializers() {
    gson.registerTypeAdapter(ArtifactScope.class, new ArtifactScopeSerialization());
    gson.registerTypeAdapter(Symbol.class, new DelegatingSerializer<Symbol>(serializers));
    gson.registerTypeAdapter(CommonSymbol.class,
        new DelegatingSerializer<CommonSymbol>(serializers));
    gson.registerTypeAdapter(MutableScope.class,
        new DelegatingSerializer<MutableScope>(serializers));
    gson.registerTypeAdapter(CommonScope.class, new DelegatingSerializer<CommonScope>(serializers));
    
    for (ISerialization<?> s : serializers) {
      gson.registerTypeAdapter(s.getSerializedClass(), s);
    }
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
