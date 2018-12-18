<#-- (c) https://github.com/MontiCore/monticore -->
${signature("name", "serializerSuffix")}
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
import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SymbolTableSerializationHelper;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;

public class ${name}${serializerSuffix}
    implements ISerialization<${name}> {
    
  @Override
  public ${name} deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {   
    JsonObject jsonObject = json.getAsJsonObject();
    if (${name}.class.getName().equals(SymbolTableSerializationHelper.getKind(jsonObject))) { 
      ${name} result = new ${name}(SymbolTableSerializationHelper.getIsShadowingScopeFlag(jsonObject));
      SymbolTableSerializationHelper.deserializeName(jsonObject, result);
      SymbolTableSerializationHelper.deserializeExportsSymbolsFlag(jsonObject, result);
      SymbolTableSerializationHelper.deserializeSymbols(jsonObject, context, result);
      SymbolTableSerializationHelper.deserializeSubscopes(jsonObject, context, result);
      return result;
    }
    throw new JsonParseException("Deserialization of '${name}' failed!");
  }
    
  @Override
  public JsonElement serialize(${name} src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    Collection<Symbol> symbols = SymbolTableSerializationHelper.getLocalSymbols(src);
    json = new SerializationBuilder(json, context)
        .add(KIND, ${name}.class.getName())
        .add(NAME, src.getName())
        .addOnlyIfFalse(EXPORTS_SYMBOLS, src.exportsSymbols())
        .addOnlyIfFalse(IS_SHADOWING_SCOPE, src.isShadowingScope())
        .add(SYMBOLS, symbols)
        .add(SUBSCOPES, src.getSubScopes())
        .build();
        
    // TODO: Remove if ScopeSpanningSymbols are removed
    SymbolTableSerializationHelper.serializeSpanningSymbol(src, json);
    return json;
  }
  
  @Override
  public Class<${name}> getSerializedClass() {
    return ${name}.class;
  }
}