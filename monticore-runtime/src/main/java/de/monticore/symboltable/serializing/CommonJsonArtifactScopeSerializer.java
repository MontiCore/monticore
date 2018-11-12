/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * Realizes (de)serializing {@link ArtifactScope} instances via JSON. For this, it maintains a list
 * of {@link ISerialization} instances to (de)serialize individual scopes and symbols. This class is
 * the superclass of all generated serializers for languages.
 *
 */
public abstract class CommonJsonArtifactScopeSerializer implements IArtifactScopeSerializer {
  
  protected GsonBuilder gson;
  
  protected List<ISerialization<?>> serializers;
  
  public CommonJsonArtifactScopeSerializer() {
    serializers = getSerializers();
    
    gson = new GsonBuilder();
    // TODO: Further configuration might be necessary
    gson.serializeSpecialFloatingPointValues();
    // gson.addSerializationExclusionStrategy(getExcusionStrategy());
    
    registerSerializers();
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
  
  public class ArtifactScopeSerialization implements ISerialization<ArtifactScope> {
    @Override
    public JsonElement serialize(ArtifactScope src, Type typeOfSrc,
        JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.addProperty("package", src.getPackageName());
      json.add("imports", context.serialize(src.getImports()));
      json.addProperty("exportsSymbols", src.exportsSymbols());
      json.addProperty("name", src.getName().orElse(null));
      json.add("subScopes", context.serialize(src.getSubScopes()));
      json.add("symbols", context.serialize(SymbolTableSerializationHelper.getLocalSymbols(src)));
      
      return json;
    }
    
    /**
     * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    @Override
    public ArtifactScope deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();
      boolean exportsSymbols = jsonObject.get("exportsSymbols").getAsBoolean();
      String _package = jsonObject.get("package").getAsString();
      List<ImportStatement> imports = SymbolTableSerializationHelper
          .deserializeImports(jsonObject.get("imports"));
      ArtifactScope result = new ArtifactScope(_package,
          imports);
      if (jsonObject.has("name")) {
        String name = jsonObject.get("name").getAsString();
        result.setName(name);
      }
      result.setExportsSymbols(exportsSymbols);
      
      //Deserialize symbols
      for(JsonElement e : jsonObject.get("symbols").getAsJsonArray()) {
        Symbol sym = context.deserialize(e, Symbol.class);
        result.add(sym);
      }
      
      for(JsonElement e : jsonObject.get("subScopes").getAsJsonArray()) {
        MutableScope subScope = context.deserialize(e, MutableScope.class);
        result.addSubScope(subScope);
      }
      
      return result;
    }
    
    /**
     * @see de.monticore.symboltable.serializing.ISerialization#getSerializedClass()
     */
    @Override
    public Class<ArtifactScope> getSerializedClass() {
      return ArtifactScope.class;
    }
  }
  
}
