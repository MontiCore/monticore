<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdInterface")}
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
${tc.include("core.Package")}

${tc.include("core.Imports")}

${tc.include("core.Annotations")}
${cdPrinter.printSimpleModifier(cdInterface.getModifier())} interface ${cdInterface.getName()} <#rt><#lt>
<#if cdInterface.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(cdInterface.getCDExtendUsage().getSuperclassList())} </#if> <#rt><#lt>


<#list cdInterface.getCDAttributeList() as attribute>
    ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdInterface.getCDMethodList() as method>
  <#if !method.getModifier().isAbstract()>default </#if>${tc.include("core.Method", method)}
</#list>
}
