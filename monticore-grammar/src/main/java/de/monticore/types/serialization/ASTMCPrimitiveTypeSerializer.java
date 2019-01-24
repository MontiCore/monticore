
package de.monticore.types.serialization;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

public class ASTMCPrimitiveTypeSerializer implements ISerialization<ASTMCPrimitiveType> {
  
  public static final String PRIMITIVE = "primitive";
  
  @Override
  public ASTMCPrimitiveType deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (isCorrectSerializer(jsonObject)) {
      int primitive = new Gson().fromJson(jsonObject.get(PRIMITIVE), Integer.class);
      
      return MCBasicTypesMill.mCPrimitiveTypeBuilder()
          .setPrimitive(primitive)
          .build();
    }
    throw new JsonParseException(
        "Deserialization of '" + getSerializedClass().getName() + "' failed!");
  }
  
  @Override
  public JsonElement serialize(ASTMCPrimitiveType src, Type typeOfSrc,
      JsonSerializationContext context) {
    
    JsonObject json = new JsonObject();
    json = new SerializationBuilder(json, context)
        .add(CLASS, getSerializedClass().getName())
        .add(PRIMITIVE, src.getPrimitive())
        .build();
    return json;
  }
  
  @Override
  public Class<ASTMCPrimitiveType> getSerializedClass() {
    return ASTMCPrimitiveType.class;
  }
}
