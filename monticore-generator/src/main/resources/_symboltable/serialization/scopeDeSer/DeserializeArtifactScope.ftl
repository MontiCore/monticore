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
      scope.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scopeJson,enclosingScope));
    </#list>

  addSymbols(scopeJson, scope);
  addAndLinkSubScopes(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson, enclosingScope);
  return scope;