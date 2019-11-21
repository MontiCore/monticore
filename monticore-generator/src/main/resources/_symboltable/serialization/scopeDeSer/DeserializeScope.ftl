<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClass", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  boolean isShadowingScope = false;
  if (scopeJson.hasBooleanMember(de.monticore.symboltable.serialization.JsonConstants.IS_SHADOWING_SCOPE)) {
    isShadowingScope = scopeJson.getBooleanMember(de.monticore.symboltable.serialization.JsonConstants.IS_SHADOWING_SCOPE);
  }
  boolean exportsSymbols = true;
  if (scopeJson.hasBooleanMember(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS)) { 
    exportsSymbols = scopeJson.getBooleanMember(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS);
  }  

  ${scopeClass} scope = new ${scopeClass}(isShadowingScope);
  if (scopeJson.hasStringMember(de.monticore.symboltable.serialization.JsonConstants.NAME)) {
    scope.setName(scopeJson.getStringMember(de.monticore.symboltable.serialization.JsonConstants.NAME));
  }
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
  deserializeAdditionalAttributes(scope,scopeJson,enclosingScope);
  return scope;