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

class ${name}Serialization 
    implements ISerialization<${name}> {
    
  @Override
  public ${name} deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String kind = jsonObject.get("kind").getAsString();
    if(${name}.class.getName().equals(kind)) {
      String name = jsonObject.get("name").getAsString();
      ${name}Builder builder = new ${name}Builder();
      builder.name(name);
      
      //TODO: Add symbol-specific attributes
      
      return builder.build();
    }
    throw new JsonParseException("Bla!");
  }
    
  @Override
  public JsonElement serialize(${name} src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("kind", ${name}.class.getName());
    json.addProperty("name", src.getName());
    
    //TODO: Add symbol-specific attributes
    return json;
  }
  
  @Override
  public Class<${name}> getSerializedClass() {
    return ${name}.class;
  }
}
  
