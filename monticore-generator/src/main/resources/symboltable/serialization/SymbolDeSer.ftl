<#-- (c) https://github.com/MontiCore/monticore -->
${signature("languageName","className","symbolName", "symbolRule")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "">

<#assign serializedKind = "${genHelper.getSymbolTablePackage()}.${symbolName}Symbol">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()}.serialization;

import ${genHelper.getSymbolTablePackage()}.*;

import java.util.Optional;
import java.util.List;

import de.monticore.symboltable.serialization.*;
import de.monticore.symboltable.serialization.json.*;
import de.se_rwth.commons.logging.Log;


/**
 * Class for serializing and deserializing ${symbolName}Symbols
 */
public class ${className} implements IDeSer<${symbolName}Symbol> {

  /**
  * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
  */
  @Override
  public String getSerializedKind() {
    return "${serializedKind}";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(${symbolName}Symbol toSerialize) {
    ${languageName}SymbolTablePrinter ${className?lower_case}SymbolTablePrinter = new ${languageName}SymbolTablePrinter();
    toSerialize.accept(${className?lower_case}SymbolTablePrinter);
    return ${className?lower_case}SymbolTablePrinter.getSerializedString();
  }
  
    /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<${symbolName}Symbol> deserialize(String serialized) {
    JsonObject symbol = JsonParser.parseJsonObject(serialized);
    return deserialize(symbol);
  }
  
  public Optional<${symbolName}Symbol> deserialize(JsonObject symbolJson) {
    String kind = symbolJson.get(JsonConstants.KIND).getAsJsonString().getValue();
    if (this.getSerializedKind().equals(kind)) {
      return Optional.of(deserialize${symbolName}Symbol(symbolJson));
    }
    return Optional.empty();
  }
  
  protected ${symbolName}Symbol deserialize${symbolName}Symbol(JsonObject symbolJson) {
    ${symbolName}SymbolBuilder builder = ${languageName}SymTabMill.${symbolName?uncap_first}SymbolBuilder();
    builder.setName(symbolJson.get(JsonConstants.NAME).getAsJsonString().getValue());
<#if symbolRule.isPresent()>
<#list symbolRule.get().getAdditionalAttributeList() as attr>
    builder.set${attr.getName()?cap_first}(deserialize${attr.getName()?cap_first}(symbolJson));
</#list>   
</#if> 
    return builder.build();
  }
  
<#if symbolRule.isPresent()>
<#list symbolRule.get().getAdditionalAttributeList() as attr>
  <#assign attrType=genHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  protected ${genHelper.getQualifiedASTName(attrType)} deserialize${attr.getName()?cap_first}(JsonObject symbolJson) {
<#switch attrType>
<#case "String">
    return symbolJson.get("${attr.getName()}").getAsJsonString().getValue();
<#break>
<#case "boolean">
    return symbolJson.get("${attr.getName()}").getAsJsonBoolean().getValue();
<#break>
<#case "int">
    return symbolJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsInt();
<#break>
<#case "float">
    return symbolJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsFloat();
<#break>
<#case "double">
    return symbolJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsDouble();
<#break>
<#case "long">
    return symbolJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsLong();
<#break>
<#default>
    Log.error("Unable to deserialize symbol attribute ${attr.getName()} of type ${attrType}. Please override the method ${className}#deserialize${attr.getName()?cap_first}(JsonObject) using the TOP mechanism!");
    return null;
</#switch>
  }
</#list>   
</#if>
  
}