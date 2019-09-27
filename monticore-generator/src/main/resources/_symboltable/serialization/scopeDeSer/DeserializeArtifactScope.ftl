<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  String name = scopeJson.get(de.monticore.symboltable.serialization.JsonConstants.NAME).getAsJsonString().getValue();
  String packageName = scopeJson.get(de.monticore.symboltable.serialization.JsonConstants.PACKAGE).getAsJsonString().getValue();
  List<de.monticore.symboltable.ImportStatement> imports = de.monticore.symboltable.serialization.JsonUtil.deserializeImports(scopeJson);
  boolean exportsSymbols = scopeJson.get(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS).getAsJsonBoolean().getValue();

  ${artifactScope} scope = new ${artifactScope}(packageName, imports);
  scope.setName(name);
  scope.setExportingSymbols(exportsSymbols);
    <#list scopeRuleAttrList as attr>
      scope.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scopeJson));
    </#list>

  addSymbols(scopeJson, scope);
  addAndLinkSubScopes(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson);
  return scope;