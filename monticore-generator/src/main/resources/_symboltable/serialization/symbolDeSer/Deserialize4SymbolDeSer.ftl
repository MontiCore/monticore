<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "symbolFullName", "symbolSimpleName","symbolRuleAttribute", "spansScope", "scopeName", "deSerFullName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  ${symbolFullName}Builder builder = ${symTabMill}.${symbolSimpleName?uncap_first}Builder();
  builder.setName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME));
  if (symbolJson.hasStringMember(de.monticore.symboltable.serialization.JsonDeSers.FULL_NAME)) {
    builder.setFullName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.FULL_NAME));
  }
  if (symbolJson.hasStringMember(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE_NAME)) {
    builder.setPackageName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE_NAME));
  }
  <#list symbolRuleAttribute as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  if (deserialize${attr.getName()?cap_first}(symbolJson).isPresent()) {
    builder.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scope, symbolJson).get());
  } else {
    builder.${genHelper.getPlainSetter(attr)}Absent();
  }
  <#else>
  builder.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scope, symbolJson));
  </#if>
</#list>

<#if spansScope>
  if(symbolJson.hasObjectMember(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE)){
    ${scopeName} spannedScope = (${scopeName})
        ${symTabMill}.globalScope().getDeSer()
        .deserializeScope(symbolJson.getObjectMember(
            de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE));
    builder.setSpannedScope(spannedScope);
  }
  else{
  ${scopeName} spannedScope = ${symTabMill}.scope();
  builder.setSpannedScope(spannedScope);
  }
</#if>

  ${symbolFullName} symbol = builder.build();

  deserializeAddons(symbol, symbolJson);
  return symbol;