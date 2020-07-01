<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "scopeClass", "scopeBuilder", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  de.monticore.symboltable.serialization.JsonDeSers.checkCorrectDeSerForKind("${scopeClass}", scopeJson);
  boolean isShadowingScope = false;
  if (scopeJson.hasBooleanMember(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE)) {
    isShadowingScope = scopeJson.getBooleanMember(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE);
  }
  boolean exportsSymbols = true;
  if (scopeJson.hasBooleanMember(de.monticore.symboltable.serialization.JsonDeSers.EXPORTS_SYMBOLS)) {
    exportsSymbols = scopeJson.getBooleanMember(de.monticore.symboltable.serialization.JsonDeSers.EXPORTS_SYMBOLS);
  }  

  ${scopeClass} scope = ${symTabMill}.${scopeBuilder?uncap_first}().setShadowing(isShadowingScope).build();
  if (scopeJson.hasStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME)) {
    scope.setName(scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME));
  }
  scope.setExportingSymbols(exportsSymbols);

<#list scopeRuleAttrList as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  ${attr.printType()} _${attr.getName()} = deserialize${attr.getName()?cap_first}(scopeJson);
  if (_${attr.getName()}.isPresent()) {
    scope.${genHelper.getPlainSetter(attr)}(_${attr.getName()}.get());
  } else {
    scope.${genHelper.getPlainSetter(attr)}Absent();
  }
  <#else>
    scope.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scopeJson));
  </#if>
</#list>
  addSymbols(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson);
  return scope;