<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "isShadowing", "isNonExporting", "isOrdered")}
${scopeInterface} scope = createScope(<#if isShadowing>true<#else>false</#if>);
<#if isNonExporting>
  scope.setExportingSymbols(false);
</#if>
<#if isOrdered>
  scope.setOrdered(true);
</#if>
return scope;