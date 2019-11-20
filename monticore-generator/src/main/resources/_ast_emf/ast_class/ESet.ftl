<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeList", "packageName", "className")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    switch (featureID) {
    <#list attributeList as attribute>
      <#assign setter = astHelper.getPlainSetter(attribute)>
      case ${packageName}.${className}_${attribute.getName()?cap_first}:
      <#if genHelper.isOptional(attribute.getMCType())>
        ${setter}(((${attribute.printType()})newValue).get());
      <#else>
        ${setter}((${attribute.printType()})newValue);
      </#if>
        return;
    </#list>
    }
    eDynamicSet(featureID, newValue);
