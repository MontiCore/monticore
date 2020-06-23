<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "artifactScope", "artifactScopeBuilder", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  de.monticore.symboltable.serialization.JsonDeSers.checkCorrectDeSerForKind("${artifactScope}", scopeJson);
  String name = scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME);
  String packageName = scopeJson.getStringMemberOpt(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE).orElse("");
  List<de.monticore.symboltable.ImportStatement> imports = de.monticore.symboltable.serialization.JsonDeSers.deserializeImports(scopeJson);
   ${artifactScope} scope = ${symTabMill}.${artifactScopeBuilder?uncap_first}().setPackageName(packageName) .setImportList(imports).build();
  scope.setName(name);
  scope.setExportingSymbols(true);

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
  addAndLinkSubScopes(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson);
  return scope;