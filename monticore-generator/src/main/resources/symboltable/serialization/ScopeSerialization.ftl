<#-- (c) https://github.com/MontiCore/monticore -->
${signature("name")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}
<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SymbolTableSerializationHelper;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;

class ${name}Serialization 
    implements ISerialization<${name}> {
    
  @Override
  public ${name} deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String kind = jsonObject.get("kind").getAsString();
    if (${name}.class.getName().equals(kind)) {     
      boolean exportsSymbols = jsonObject.get("exportsSymbols").getAsBoolean();
      boolean isShadowingScope = jsonObject.get("isShadowingScope").getAsBoolean();
      
      ${name} result = new ${name}(isShadowingScope);
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
        MutableScope subScope = context.deserialize(e, MutableScope.class);
        result.addSubScope(subScope);
      }
      
      return result;
    }
    throw new JsonParseException("Deserialization of '${name}' failed!");
  }
    
  @Override
  public JsonElement serialize(${name} src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("kind", ${name}.class.getName());
    json.addProperty("name", src.getName().orElse(null));
    json.addProperty("exportsSymbols", src.exportsSymbols());
    json.addProperty("isShadowingScope", src.isShadowingScope());
    
    json.add("subScopes", context.serialize(src.getSubScopes()));
    json.add("symbols", context.serialize(SymbolTableSerializationHelper.getLocalSymbols(src)));
    // TODO: Add scope-specific attributes
    return json;
  }
  
  @Override
  public Class<${name}> getSerializedClass() {
    return ${name}.class;
  }
}