/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Symbol;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ArtifactScopeSerialization implements ISerialization<ArtifactScope> {

  
  @Override
  public JsonElement serialize(ArtifactScope src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    Collection<Symbol> symbols = SymbolTableSerializationHelper.getLocalSymbols(src);
    json = new SerializationBuilder(json, context)
        .add(PACKAGE, src.getPackageName())
        .add(IMPORTS, src.getImports())
        .addOnlyIfFalse(EXPORTS_SYMBOLS, src.exportsSymbols())
        .add(NAME, src.getName())
        .add(SYMBOLS, symbols)
        .add(SUBSCOPES, src.getSubScopes())
        .build();
    
    // TODO: Remove if ScopeSpanningSymbols are removed
    SymbolTableSerializationHelper.serializeSpanningSymbol(src, json);
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
    
    String _package = SymbolTableSerializationHelper.deserializePackage(jsonObject);
    List<ImportStatement> imports = SymbolTableSerializationHelper
        .deserializeImports(jsonObject.get(IMPORTS));
    ArtifactScope result = new ArtifactScope(_package,
        imports);

    SymbolTableSerializationHelper.deserializeName(jsonObject, result);
    SymbolTableSerializationHelper.deserializeExportsSymbolsFlag(jsonObject, result);
    SymbolTableSerializationHelper.deserializeSymbols(jsonObject, context, result);
    SymbolTableSerializationHelper.deserializeSubscopes(jsonObject, context, result);
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
