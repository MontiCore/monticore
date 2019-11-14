<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeList", "packageName", "className")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
switch (featureID) {
<#list attributeList as attribute>
    <#assign getter = astHelper.getPlainGetter(attribute)>
  case ${packageName}.${className}_${attribute.getName()?cap_first}:
    <#if genHelper.isOptional(attribute.getMCType())>
      return isPresent${genHelper.getNativeAttributeName(attribute.getName())?cap_first}()? ${getter}() : null;
    <#else>
      return ${getter}();
    </#if>
</#list>
}
return eDynamicGet(featureID, resolve, coreType);