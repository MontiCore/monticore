/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.mocks.languages.automaton;

import com.google.gson.*;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.serializing.CommonArtifactScopeSerializer;
import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SymbolTableSerializationHelper;

import java.lang.reflect.Type;

public class AutomatonSerializer extends CommonArtifactScopeSerializer {
  
  /**
   * @see de.monticore.symboltable.serializing.CommonArtifactScopeSerializer#getSerializers()
   */
//  @Override
//  protected List<ISerialization<?>> getSerializers() {
//    return ImmutableList.of(new StateSymbolSerialization(), new AutSymbolSerialization(),
//        new AutomatonScopeSerialization());
//  }
  
  class StateSymbolSerialization
      implements ISerialization<StateSymbol> {
    
    /**
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
     * com.google.gson.JsonSerializationContext)
     */
    @Override
    public JsonElement serialize(StateSymbol src, Type typeOfSrc,
        JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.addProperty("kind", StateSymbol.class.getName());
      json.addProperty("name", src.getName());
      
      // TODO: Add symbol-specific attributes
      return json;
    }
    
    /**
     * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    @Override
    public StateSymbol deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();
      String kind = jsonObject.get("kind").getAsString();
      if (StateSymbol.class.getName().equals(kind)) {
        String name = jsonObject.get("name").getAsString();
        return new StateSymbol(name);
      }
      throw new JsonParseException("Bla!");
    }
    
    /**
     * @see de.monticore.symboltable.serializing.ISerialization#getSerializedClass()
     */
    @Override
    public Class<StateSymbol> getSerializedClass() {
      return StateSymbol.class;
    }
    
  }
  
  class AutomatonScopeSerialization
      implements ISerialization<AutomatonScope> {
    
    /**
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
     * com.google.gson.JsonSerializationContext)
     */
    @Override
    public JsonElement serialize(AutomatonScope src, Type typeOfSrc,
        JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.addProperty("kind", AutomatonScope.class.getName());
      json.addProperty("name", src.getName().orElse(null));
      json.addProperty("exportsSymbols", src.exportsSymbols());
      json.addProperty("isShadowingScope", src.isShadowingScope());
      
//      json.add("subScopes", context.serialize(src.getSubScopes()));
//      json.add("symbols", context.serialize(src.getLocalSymbols().entrySet()));
      
      json.add("subScopes", context.serialize(src.getSubScopes()));
      json.add("symbols", context.serialize(SymbolTableSerializationHelper.getLocalSymbols(src)));
      
      // TODO: Add scope-specific attributes
      return json;
    }
    
    /**
     * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    @Override
    public AutomatonScope deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();
      String kind = jsonObject.get("kind").getAsString();
      if (AutomatonScope.class.getName().equals(kind)) {
        
        boolean exportsSymbols = jsonObject.get("exportsSymbols").getAsBoolean();
        boolean isShadowingScope = jsonObject.get("isShadowingScope").getAsBoolean();
        
        AutomatonScope result = new AutomatonScope(isShadowingScope);
        if(jsonObject.has("name")){
          String name = jsonObject.get("name").getAsString();
          result.setName(name);
        }
        result.setExportsSymbols(exportsSymbols);
        
        // Deserialize symbols
        for(JsonElement e : jsonObject.get("symbols").getAsJsonArray()) {
          Symbol sym = context.deserialize(e, Symbol.class);
          result.add(sym);
        }
        
        // Deserialize subscopes
        for(JsonElement e : jsonObject.get("subScopes").getAsJsonArray()) {
          Scope subScope = context.deserialize(e, Scope.class);
          result.addSubScope(subScope);
        }
        
        return result;
      }
      throw new JsonParseException("Bla!");
    }
    
    /**
     * @see de.monticore.symboltable.serializing.ISerialization#getSerializedClass()
     */
    @Override
    public Class<AutomatonScope> getSerializedClass() {
      return AutomatonScope.class;
    }
    
  }
  
  class AutSymbolSerialization implements ISerialization<AutSymbol> {
    
    /**
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
     * com.google.gson.JsonSerializationContext)
     */
    @Override
    public JsonElement serialize(AutSymbol src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.addProperty("kind", AutSymbol.class.getName());
      json.addProperty("name", src.getName());
      
      // TODO: Add symbol-specific attributes
      return json;
    }
    
    /**
     * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    @Override
    public AutSymbol deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();
      String kind = jsonObject.get("kind").getAsString();
      if (AutSymbol.class.getName().equals(kind)) {
        String name = jsonObject.get("name").getAsString();
        return new AutSymbol(name);
      }
      throw new JsonParseException("Bla!");
    }
    
    /**
     * @see de.monticore.symboltable.serializing.ISerialization#getSerializedClass()
     */
    @Override
    public Class<AutSymbol> getSerializedClass() {
      return AutSymbol.class;
    }
    
  }
  
}
