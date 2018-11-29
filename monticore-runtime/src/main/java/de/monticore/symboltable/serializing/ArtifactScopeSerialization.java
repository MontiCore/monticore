/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class ArtifactScopeSerialization implements ISerialization<ArtifactScope> {
  @Override
  public JsonElement serialize(ArtifactScope src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty(PACKAGE, src.getPackageName());
    json.add(IMPORTS, context.serialize(src.getImports()));
    json.addProperty(EXPORTS_SYMBOLS, src.exportsSymbols());
    json.addProperty(NAME, src.getName().orElse(null));
    
    // TODO: Remove if ScopeSpanningSymbols are removed
    serializeSpanningSymbol(src, json);
    
    //Symbols
    Collection<Symbol> symbols = SymbolTableSerializationHelper.getLocalSymbols(src);
    json.add(SYMBOLS, context.serialize(symbols));
    
    //Subscopes
    json.add(SUBSCOPES, context.serialize(src.getSubScopes()));
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
    boolean exportsSymbols = jsonObject.get(EXPORTS_SYMBOLS).getAsBoolean();
    String _package = jsonObject.get(PACKAGE).getAsString();
    List<ImportStatement> imports = SymbolTableSerializationHelper
        .deserializeImports(jsonObject.get(IMPORTS));
    ArtifactScope result = new ArtifactScope(_package,
        imports);
    if (jsonObject.has(NAME)) {
      String name = jsonObject.get(NAME).getAsString();
      result.setName(name);
    }
    result.setExportsSymbols(exportsSymbols);
    
    //Symbols
    for(JsonElement e : jsonObject.get(SYMBOLS).getAsJsonArray()) {
      Symbol sym = context.deserialize(e, Symbol.class);
      result.add(sym);
    }
    
    //Subscopes
    for(JsonElement e : jsonObject.get(SUBSCOPES).getAsJsonArray()) {
      MutableScope subScope = context.deserialize(e, MutableScope.class);
      
      // TODO: Remove if ScopeSpanningSymbols are removed
      deserializeSpanningSymbol(result, subScope, e);
      
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
  
  /**
   * 
   * TODO: This method can be removed, if ScopeSpanningSymbols are removed
   * @param src
   * @param json
   */
  protected void serializeSpanningSymbol(ArtifactScope src, JsonObject json) {
    if (src.isSpannedBySymbol()) {
      ScopeSpanningSymbol spanningSymbol = src.getSpanningSymbol().get();
      JsonObject jsonSpanningSymbol = new JsonObject();
      jsonSpanningSymbol.addProperty(KIND, spanningSymbol.getKind().getName());
      jsonSpanningSymbol.addProperty(NAME, spanningSymbol.getName());
      json.add("spanningSymbol", jsonSpanningSymbol);
    }
  }
  
  /**
   * 
   * TODO: This method can be removed, if ScopeSpanningSymbols are removed
   * @param result
   * @param subScope
   * @param e
   */
  protected void deserializeSpanningSymbol(ArtifactScope result, MutableScope subScope, JsonElement e) {
    JsonObject jsonSubScope = e.getAsJsonObject();
    if (jsonSubScope.has("spanningSymbol")) {
      JsonObject asJsonObject = jsonSubScope.get("spanningSymbol").getAsJsonObject();
      String spanningSymbolName = asJsonObject.get(NAME).getAsString();
      String spanningSymbolKind = asJsonObject.get(KIND).getAsString();
      
      Collection<Symbol> allLocalSymbols = result.getLocalSymbols().get(spanningSymbolName);
      List<Symbol> symbolsOfCorrectKind = allLocalSymbols.stream()
          .filter(x -> x.getKind().getName().equals(spanningSymbolKind))
          .collect(Collectors.toList());
      if (symbolsOfCorrectKind.size() == 1) {
        ScopeSpanningSymbol symbol = (ScopeSpanningSymbol) symbolsOfCorrectKind.get(0); 
        subScope.setSpanningSymbol(symbol);
        Optional<? extends Scope> enclosingScope = symbol.getSpannedScope().getEnclosingScope();
        if(enclosingScope.isPresent()) {
          enclosingScope.get().getAsMutableScope().removeSubScope((MutableScope)symbol.getSpannedScope());
        }
      }
      else {
        Log.error("Spanning Symbol '" + spanningSymbolName + "' of kind '" + spanningSymbolName
            + "' not resolved!");
      }    
    }
  }
}