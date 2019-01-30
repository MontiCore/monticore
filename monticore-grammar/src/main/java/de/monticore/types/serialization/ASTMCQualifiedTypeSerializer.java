package de.monticore.types.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

public class ASTMCQualifiedTypeSerializer implements ISerialization<ASTMCQualifiedType> {
  
  public static final String MC_QUALIFIED_NAME = "mCQualifiedName";
  
  @Override
  public ASTMCQualifiedType deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (isCorrectSerializer(jsonObject)) {
      ASTMCQualifiedName qName = context.deserialize(jsonObject.get(MC_QUALIFIED_NAME),
          ASTMCQualifiedName.class);
      return MCBasicTypesMill.mCQualifiedTypeBuilder()
          .setMCQualifiedName(qName)
          .build();
    }
    return fail();
  }
  
  @Override
  public JsonElement serialize(ASTMCQualifiedType src, Type typeOfSrc,
      JsonSerializationContext context) {
    
    return new SerializationBuilder(context)
        .add(CLASS, getSerializedClass().getName())
        .add(MC_QUALIFIED_NAME, src.getMCQualifiedName())
        .build();
  }
  
  @Override
  public Class<ASTMCQualifiedType> getSerializedClass() {
    return ASTMCQualifiedType.class;
  }
}
