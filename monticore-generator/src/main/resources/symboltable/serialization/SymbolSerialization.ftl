<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "symbolName")}
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

class ${className} 
    implements ISerialization<${symbolName}Symbol> {
    
  @Override
  public ${symbolName}Symbol deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String kind = jsonObject.get(KIND).getAsString();
    if(${symbolName}Symbol.class.getName().equals(kind)) {
      String name = jsonObject.get(NAME).getAsString();
      ${symbolName}SymbolBuilder builder = new ${symbolName}SymbolBuilder();
      builder.name(name);
      
      //TODO: Add symbol-specific attributes
      
      return builder.build();
    }
    throw new JsonParseException("Deserialization of '${symbolName}Symbol' failed!");
  }
    
  @Override
  public JsonElement serialize(${symbolName}Symbol src, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty(KIND, ${symbolName}Symbol.class.getName());
    json.addProperty(NAME, src.getName());
    
    //TODO: Add symbol-specific attributes
    return json;
  }
  
  @Override
  public Class<${symbolName}Symbol> getSerializedClass() {
    return ${symbolName}Symbol.class;
  }
}
  
