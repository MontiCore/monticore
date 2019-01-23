package de.monticore.types.serialization;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;

import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

public class ASTMCQualifiedNameSerializer implements ISerialization<ASTMCQualifiedName> {
  
  public static final String PART_LIST = "partList";
  
  @Override
  public ASTMCQualifiedName deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (isCorrectSerializer(jsonObject)) {
      
      Type listType = new TypeToken<List<String>>() {}.getType();
      List<String> parts = new Gson().fromJson(jsonObject.get(PART_LIST), listType);
      
      return MCBasicTypesMill.mCQualifiedNameBuilder()
          .setPartList(parts)
          .build();
    }
    return fail();
  }
  
  @Override
  public JsonElement serialize(ASTMCQualifiedName src, Type typeOfSrc,
      JsonSerializationContext context) {
    
    JsonObject json = new JsonObject();
    json = new SerializationBuilder(json, context)
        .add(CLASS, getSerializedClass().getName())
        .add(PART_LIST, src.getPartList())
        .build();
    return json;
  }
  
  @Override
  public Class<ASTMCQualifiedName> getSerializedClass() {
    return ASTMCQualifiedName.class;
  }
}
