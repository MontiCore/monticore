<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "symbolFullName", "symbolSimpleName","symbolRuleAttribute", "spansScope", "scopeName", "deSerFullName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  ${symbolFullName}Builder builder = ${symTabMill}.${symbolSimpleName?uncap_first}Builder();
  builder.setName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME));
<#list symbolRuleAttribute as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  if (deserialize${attr.getName()?cap_first}(symbolJson).isPresent()) {
    builder.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(symbolJson).get());
  } else {
    builder.${genHelper.getPlainSetter(attr)}Absent();
  }
  <#else>
  builder.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(symbolJson));
  </#if>
</#list>
  ${symbolFullName} symbol = builder.build();

<#if spansScope>
  if(symbolJson.hasObjectMember(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE)){
    ${scopeName} spannedScope = ((${deSerFullName}) ${symTabMill}.globalScope()
        .getDeSer("${scopeName}"))
        .deserializeScope(symbolJson.getObjectMember(
            de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE));
    symbol.setSpannedScope(spannedScope);
  }
  else{
  ${scopeName} spannedScope = ${symTabMill}.scope();
  symbol.setSpannedScope(spannedScope);
  }
</#if>

  deserializeAddons(symbol, symbolJson);
  return symbol;