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

class ${name}SymbolSerialization 
    implements ISerialization<${name}Symbol> {
    
  @Override
  public ${name}Symbol deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String kind = jsonObject.get(KIND).getAsString();
    if(${name}Symbol.class.getName().equals(kind)) {
      String name = jsonObject.get(NAME).getAsString();
      ${name}SymbolBuilder builder = new ${name}SymbolBuilder();
      builder.name(name);
      
      //TODO: Add symbol-specific attributes
      
      return builder.build();
    }
    throw new JsonParseException("Deserialization of '${name}Symbol' failed!");
  }
    
  @Override
  public JsonElement serialize(${name}Symbol src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty(KIND, ${name}Symbol.class.getName());
    json.addProperty(NAME, src.getName());
    
    //TODO: Add symbol-specific attributes
    return json;
  }
  
  @Override
  public Class<${name}Symbol> getSerializedClass() {
    return ${name}Symbol.class;
  }
}
  
