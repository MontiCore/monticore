<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeClass")}
  Optional<String> name = scopeJson.getStringOpt(de.monticore.symboltable.serialization.JsonConstants.NAME);
  Optional<Boolean> exportsSymbols = scopeJson.getBooleanOpt(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS);
  Optional<Boolean> isShadowingScope = scopeJson.getBooleanOpt(de.monticore.symboltable.serialization.JsonConstants.IS_SHADOWING_SCOPE);

  ${scopeClass} scope = new ${scopeClass}(isShadowingScope.orElse(false));
  name.ifPresent(scope::setName);
  scope.setExportingSymbols(exportsSymbols.orElse(true));

  addSymbols(scopeJson, scope);
  addAndLinkSubScopes(scopeJson, scope);
  deserializeAdditionalAttributes(scope,scopeJson);
  return scope;