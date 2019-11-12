<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClass", "scopeRuleAttrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  Optional<String> name = scopeJson.getStringMemberOpt(de.monticore.symboltable.serialization.JsonConstants.NAME);
  Optional<Boolean> exportsSymbols = scopeJson.getBooleanMemberOpt(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS);
  Optional<Boolean> isShadowingScope = scopeJson.getBooleanMemberOpt(de.monticore.symboltable.serialization.JsonConstants.IS_SHADOWING_SCOPE);

  ${scopeClass} scope = new ${scopeClass}(isShadowingScope.orElse(false));
  name.ifPresent(scope::setName);
  scope.setExportingSymbols(exportsSymbols.orElse(true));
            <#list scopeRuleAttrList as attr>
              scope.${genHelper.getPlainSetter(attr)}(deserialize${attr.getName()?cap_first}(scopeJson));
            </#list>

  addSymbols(scopeJson, scope);
  addAndLinkSubScopes(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson);
  return scope;