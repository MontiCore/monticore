<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "scopeClass", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  boolean isShadowingScope = scopeJson
      .getBooleanMemberOpt(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE)
      .orElse(false);
  ${scopeClass} scope = ${symTabMill}.scope();
  scope.setShadowing(isShadowingScope);
  scope.setExportingSymbols(true);

<#list scopeRuleAttrList as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  ${attr.printType()} _${attr.getName()} = deserialize${attr.getName()?cap_first}(scope, scopeJson);
  if (_${attr.getName()}.isPresent()) {
    scope.${genHelper.getPlainSetter(attr)}(_${attr.getName()}.get());
  } else {
    scope.${genHelper.getPlainSetter(attr)}Absent();
  }
  <#else>
    scope.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scope, scopeJson));
  </#if>
</#list>

  deserializeAddons(scope,scopeJson);
  deserializeSymbols(scope, scopeJson);
  return scope;