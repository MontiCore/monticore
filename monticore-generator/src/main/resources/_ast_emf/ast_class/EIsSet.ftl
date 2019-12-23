<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeList", "packageName", "className")}
<#assign service = glex.getGlobalVar("service")>
<#assign genHelper = glex.getGlobalVar("astHelper")>
switch (featureID) {
<#list attributeList as attribute>
    <#assign getter = astHelper.getPlainGetter(attribute)>
  case ${packageName}.${className}_${attribute.getName()?cap_first}:
    <#if genHelper.isListType(attribute.printType())>
      return !${getter}().isEmpty();
    <#elseif genHelper.isOptional(attribute.getMCType())>
      return ${attribute.getName()}.isPresent();
    <#else>
      return ${getter}() != ${service.getDefaultValue(attribute)};
    </#if>
    </#list>
    }
    return eDynamicIsSet(featureID);
