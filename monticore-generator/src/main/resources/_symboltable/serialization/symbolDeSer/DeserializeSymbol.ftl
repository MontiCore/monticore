<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolBuilderFullName","symbolBuilderSimpleName", "symTabMill", "symbolName", "symbolRuleAttribute")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  ${symbolBuilderFullName} builder = ${symTabMill}.${symbolBuilderSimpleName?uncap_first}();
  builder.setName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonConstants.NAME));
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
  ${symbolName} symbol = builder.build();
  deserializeAdditionalAttributes(symbol, symbolJson,enclosingScope);
  return symbol;