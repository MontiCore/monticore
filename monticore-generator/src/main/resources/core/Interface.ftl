${tc.signature("cdInterface")}
${tc.include("core.Package")}

${tc.include("core.Imports")}

${cdInterface.printModifier()} interface ${cdInterface.getName()}
<#if !cdInterface.isEmptyInterfaces()>extends ${cdInterface.printInterfaces()} </#if>{

<#list cdInterface.getCDMethodList() as method>
  <#if !method.getModifier().isAbstract()>default </#if>${tc.include("core.Method", method)}
</#list>
}