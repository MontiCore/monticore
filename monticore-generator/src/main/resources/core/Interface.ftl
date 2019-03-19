${tc.signature("cdInterface")}

${tc.include("core.Imports")}

${cdInterface.printModifier()} interface ${cdInterface.getName()}
<#if !cdInterface.isEmptyInterfaces()>extends ${cdInterface.printInterfaces()} </#if>{

<#list cdInterface.getCDAttributeList() as attribute>
  ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdInterface.getCDMethodList() as method>
  ${tc.include("core.Method", method)}
</#list>
}