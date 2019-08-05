<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeList", "packageName", "className")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    switch (featureID) {
    <#list attributeList as attribute>
      <#assign setter = astHelper.getPlainSetter(attribute)>
      case ${packageName}.${className}_${attribute.getName()?cap_first}:
        ${setter}<#if genHelper.isOptional(attribute.getMCType())>Opt</#if>((${attribute.printType()})newValue);
        return;
    </#list>
    }
    eDynamicSet(featureID, newValue);
