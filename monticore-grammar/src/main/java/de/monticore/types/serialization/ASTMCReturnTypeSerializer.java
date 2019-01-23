
package de.monticore.types.serialization;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

public class ASTMCReturnTypeSerializer implements ISerialization<ASTMCReturnType> {
  
  public static final String MC_TYPE = "mCType";
  
  public static final String MC_VOID_TYPE = "mCVoidType";
  
  @Override
  public ASTMCReturnType deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (isCorrectSerializer(jsonObject)) {
      Optional<ASTMCType> mCType = Optional.empty();
      if (jsonObject.has(MC_TYPE)) {
        mCType = Optional.ofNullable(context.deserialize(jsonObject.get(MC_TYPE), ASTMCType.class));
      }
      
      Optional<ASTMCVoidType> mCVoidType = Optional.empty();
      if (jsonObject.has(MC_VOID_TYPE)) {
        mCVoidType = Optional
            .ofNullable(context.deserialize(jsonObject.get(MC_VOID_TYPE), ASTMCVoidType.class));
      }
      return MCBasicTypesMill.mCReturnTypeBuilder()
          .setMCTypeOpt(mCType)
          .setMCVoidTypeOpt(mCVoidType)
          .build();
    }
    return fail();
  }
  
  @Override
  public JsonElement serialize(ASTMCReturnType src, Type typeOfSrc,
      JsonSerializationContext context) {
    
    JsonObject json = new JsonObject();
    json = new SerializationBuilder(json, context)
        .add(CLASS, getSerializedClass().getName())
        .add(MC_TYPE, src.getMCTypeOpt())
        .add(MC_VOID_TYPE, src.getMCVoidTypeOpt())
        .build();
    return json;
  }
  
  @Override
  public Class<ASTMCReturnType> getSerializedClass() {
    return ASTMCReturnType.class;
  }
}
