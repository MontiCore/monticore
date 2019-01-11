<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "symbolName", "symbolRule", "imports")}
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
import de.monticore.symboltable.serializing.SerializationBuilder;
import de.monticore.symboltable.serializing.SymbolTableSerializationHelper;
<#list imports as imp>
import ${imp}._ast.*;
</#list>

class ${className} 
    implements ISerialization<${symbolName}Symbol> {
    
  <#if symbolRule.isPresent()>
  <#list symbolRule.get().getAdditionalAttributeList() as attr>
  public static final String ${attr.getName()?upper_case} = "${attr.getName()}";
  </#list>
  </#if>
    
  @Override
  public ${symbolName}Symbol deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (${symbolName}Symbol.class.getName().equals(SymbolTableSerializationHelper.getClassName(jsonObject))) { 
      String name = jsonObject.get(NAME).getAsString();
      ${symbolName}SymbolBuilder builder = new ${symbolName}SymbolBuilder();
      builder.name(name);
      <#if symbolRule.isPresent()>
      <#list symbolRule.get().getAdditionalAttributeList() as attr>
      <#assign attrType=genHelper.getQualifiedASTName(attr.getGenericType().getTypeName())>
      ${attrType} ${attr.getName()} = (${attrType}) context.deserialize(jsonObject.get(${attr.getName()?upper_case}), typeOfT);
      builder.${attr.getName()}(${attr.getName()});
      </#list>
      </#if>
      
      return builder.build();
    }
    throw new JsonParseException("Deserialization of '${symbolName}Symbol' failed!");
  }
    
  @Override
  public JsonElement serialize(${symbolName}Symbol src, Type typeOfSrc,
      JsonSerializationContext context) {
      
    JsonObject json = new JsonObject();
    json = new SerializationBuilder(json, context)
        .add(CLASS, ${symbolName}Symbol.class.getName())
        .add(NAME, src.getName())
        <#if symbolRule.isPresent()>
        <#list symbolRule.get().getAdditionalAttributeList() as attr>
        <#assign attrType=genHelper.getQualifiedASTName(attr.getGenericType().getTypeName())>
        <#if attrType == "boolean" || attrType == "Boolean">
        .add(${attr.getName()?upper_case}, context.serialize(src.is${attr.getName()?cap_first}()));
        <#else>
        .add(${attr.getName()?upper_case}, context.serialize(src.get${attr.getName()?cap_first}()));
        </#if>
        </#list>
        </#if>
        .build();

    return json;
  }
  
  @Override
  public Class<${symbolName}Symbol> getSerializedClass() {
    return ${symbolName}Symbol.class;
  }
}