<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolBuilderFullName","symbolBuilderSimpleName", "symTabMill", "symbolName", "symbolRuleAttribute")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  ${symbolBuilderFullName} builder = ${symTabMill}.${symbolBuilderSimpleName?uncap_first}();
  builder.setName(symbolJson.get(de.monticore.symboltable.serialization.JsonConstants.NAME).getAsJsonString().getValue());
<#list symbolRuleAttribute as attr>
  builder.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(symbolJson));
</#list>
  ${symbolName} symbol = builder.build();
  deserializeAdditionalAttributes(symbol, symbolJson);
  return symbol;