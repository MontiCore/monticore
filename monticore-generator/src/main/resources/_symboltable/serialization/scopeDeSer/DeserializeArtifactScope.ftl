<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "artifactScope", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  ${artifactScope} scope = ${symTabMill}.artifactScope();
  scope.setPackageName(de.monticore.symboltable.serialization.JsonDeSers.getPackage(scopeJson));
  if (scopeJson.hasStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME)) {
    scope.setName(scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME));
  }
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

  this.symbolHierarchiesJsonObjectOpt = scopeJson.getObjectMemberOpt(de.monticore.symboltable.serialization.JsonDeSers.SYMBOL_HIERARCHY);

  deserializeAddons(scope,scopeJson);
  deserializeSymbols(scope, scopeJson);

  this.symbolHierarchiesJsonObjectOpt = Optional.empty();
  return scope;