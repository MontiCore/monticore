
package de.monticore.types.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

public class ASTMCImportStatementSerializer implements ISerialization<ASTMCImportStatement> {
  
  public static final String MC_QUALIFIED_NAME = "mCQualifiedName";
  
  public static final String STAR = "star";
  
  @Override
  public ASTMCImportStatement deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (isCorrectSerializer(jsonObject)) {
      ASTMCQualifiedName qName = context.deserialize(jsonObject.get(MC_QUALIFIED_NAME),
          ASTMCQualifiedName.class);
      boolean isStar = jsonObject.get(STAR).getAsBoolean();
      
      return MCBasicTypesMill.mCImportStatementBuilder()
          .setMCQualifiedName(qName)
          .setStar(isStar)
          .build();
    }
    return fail();
  }
  
  @Override
  public JsonElement serialize(ASTMCImportStatement src, Type typeOfSrc,
      JsonSerializationContext context) {
    
    JsonObject json = new JsonObject();
    json = new SerializationBuilder(json, context)
        .add(CLASS, getSerializedClass().getName())
        .add(MC_QUALIFIED_NAME, src.getMCQualifiedName())
        .add(STAR, src.isStar())
        .build();
    return json;
  }
  
  @Override
  public Class<ASTMCImportStatement> getSerializedClass() {
    return ASTMCImportStatement.class;
  }
}
