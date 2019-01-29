
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
    
    return new SerializationBuilder(context)
        .add(CLASS, getSerializedClass().getName())
        .build();
  }
  
  @Override
  public Class<ASTMCVoidType> getSerializedClass() {
    return ASTMCVoidType.class;
  }
}
