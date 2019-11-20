<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  String name = scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonConstants.NAME);
  String packageName = scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonConstants.PACKAGE);
  List<de.monticore.symboltable.ImportStatement> imports = de.monticore.symboltable.serialization.JsonUtil.deserializeImports(scopeJson);
  boolean exportsSymbols = scopeJson.getBooleanMember(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS);

  ${artifactScope} scope = new ${artifactScope}(packageName, imports);
  scope.setName(name);
  scope.setExportingSymbols(exportsSymbols);
<#list scopeRuleAttrList as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  ${attr.printType()} _${attr.getName()} = deserialize${attr.getName()?cap_first}(scopeJson,enclosingScope);
  if (_${attr.getName()}.isPresent()) {
    scope.${genHelper.getPlainSetter(attr)}(_${attr.getName()}.get());
  } else {
    scope.${genHelper.getPlainSetter(attr)}Absent();
  }
  <#else>
  scope.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scopeJson,enclosingScope));
  </#if>
</#list>

  addSymbols(scopeJson, scope);
  addAndLinkSubScopes(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson, enclosingScope);
  return scope;