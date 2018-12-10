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
    String kind = jsonObject.get(KIND).getAsString();
    if (${name}.class.getName().equals(kind)) {     
      boolean exportsSymbols = jsonObject.get(EXPORTS_SYMBOLS).getAsBoolean();
      boolean isShadowingScope = jsonObject.get(IS_SHADOWING_SCOPE).getAsBoolean();
      
      ${name} result = new ${name}(isShadowingScope);
      if(jsonObject.has(NAME)){
        String name = jsonObject.get(NAME).getAsString();
        result.setName(name);
      }
      result.setExportsSymbols(exportsSymbols);
      
      // Deserialize symbols
      for(JsonElement e : jsonObject.get(SYMBOLS).getAsJsonArray()) {
        Symbol sym = context.deserialize(e, Symbol.class);
        result.add(sym);
      }
      
      // Deserialize subscopes
      for(JsonElement e : jsonObject.get(SUBSCOPES).getAsJsonArray()) {
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
    json.addProperty(KIND, ${name}.class.getName());
    json.addProperty(NAME, src.getName().orElse(null));
    json.addProperty(EXPORTS_SYMBOLS, src.exportsSymbols());
    json.addProperty(IS_SHADOWING_SCOPE, src.isShadowingScope());
    
    json.add(SUBSCOPES, context.serialize(src.getSubScopes()));
    json.add(SYMBOLS, context.serialize(SymbolTableSerializationHelper.getLocalSymbols(src)));
    // TODO: Add scope-specific attributes
    return json;
  }
  
  @Override
  public Class<${name}> getSerializedClass() {
    return ${name}.class;
  }
}