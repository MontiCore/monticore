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
              scope.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scopeJson));
            </#list>

  addSymbols(scopeJson, scope);
  addAndLinkSubScopes(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson);
  return scope;