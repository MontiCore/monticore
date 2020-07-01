<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolBuilderFullName","symbolBuilderSimpleName", "symTabMill", "symbolFullName", "symbolSimpleName","symbolRuleAttribute")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  de.monticore.symboltable.serialization.JsonDeSers.checkCorrectDeSerForKind(getSerializedKind(), symbolJson);
  ${symbolBuilderFullName} builder = ${symTabMill}.${symbolBuilderSimpleName?uncap_first}();
  builder.setName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME));
<#list symbolRuleAttribute as attr>
  <#if genHelper.isOptional(attr.getMCType())>
    if (deserialize${attr.getName()?cap_first}(symbolJson, enclosingScope).isPresent()) {
  builder.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(symbolJson,enclosingScope).get());
  } else {
  builder.${genHelper.getPlainSetter(attr)}Absent();
  }
  <#else>
  builder.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(symbolJson, enclosingScope));
  </#if>
</#list>
  ${symbolFullName} symbol = builder.build();
  deserializeAdditional${symbolSimpleName}Attributes(symbol, symbolJson, enclosingScope);
  return symbol;