<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}
<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.symboltable.serializing.SymbolTableSerializationHelper;

public class ${className}
    implements ISerialization<${scopeName}> {
    
  @Override
  public ${scopeName} deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {   
    JsonObject jsonObject = json.getAsJsonObject();
    if (${scopeName}.class.getName().equals(SymbolTableSerializationHelper.getClassName(jsonObject))) { 
      ${scopeName} result = new ${scopeName}(SymbolTableSerializationHelper.getIsShadowingScopeFlag(jsonObject));
      SymbolTableSerializationHelper.deserializeName(jsonObject, result);
      SymbolTableSerializationHelper.deserializeSymbols(jsonObject, context, result);
      SymbolTableSerializationHelper.deserializeSubscopes(jsonObject, context, result);
      result.setExportsSymbols(true);
      return result;
    }
    throw new JsonParseException("Deserialization of '${scopeName}' failed!");
  }
    
  @Override
  public JsonElement serialize(${scopeName} src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    Collection<Symbol> symbols = SymbolTableSerializationHelper.getLocalSymbols(src);
    Collection<Scope> subscopes = SymbolTableSerializationHelper.filterRelevantSubScopes(src);
    
    json = new SerializationBuilder(json, context)
        .add(CLASS, ${scopeName}.class.getName())
        .add(NAME, src.getName())
        .add(IS_SHADOWING_SCOPE, src.isShadowingScope())
        .add(SYMBOLS, symbols)
        .add(SUBSCOPES, subscopes)
        .build();
        
    // TODO: Remove if ScopeSpanningSymbols are removed
    SymbolTableSerializationHelper.serializeSpanningSymbol(src, json);
    return json;
  }
  
  @Override
  public Class<${scopeName}> getSerializedClass() {
    return ${scopeName}.class;
  }
}