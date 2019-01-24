
package de.monticore.types.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

public class ASTMCVoidTypeSerializer implements ISerialization<ASTMCVoidType> {
  
  public static final String MC_QUALIFIED_NAME = "mCQualifiedName";
  
  public static final String STAR = "star";
  
  @Override
  public ASTMCVoidType deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (isCorrectSerializer(jsonObject)) {
      return MCBasicTypesMill.mCVoidTypeBuilder().build();
    }
    return fail();
  }
  
  @Override
  public JsonElement serialize(ASTMCVoidType src, Type typeOfSrc,
      JsonSerializationContext context) {
    
    JsonObject json = new JsonObject();
    json = new SerializationBuilder(json, context)
        .add(CLASS, getSerializedClass().getName())
        .build();
    return json;
  }
  
  @Override
  public Class<ASTMCVoidType> getSerializedClass() {
    return ASTMCVoidType.class;
  }
}
