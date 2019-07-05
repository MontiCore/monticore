${tc.signature("cdInterface")}
${tc.include("core.Package")}

${tc.include("core.Imports")}

${cdInterface.printModifier()} interface ${cdInterface.getName()} <#rt><#lt>
<#if !cdInterface.isEmptyInterfaces()>extends ${cdInterface.printInterfaces()} </#if>{


<#list cdInterface.getCDAttributeList() as attribute>
    ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdInterface.getCDMethodList() as method>
    <#if !method.getModifier().isAbstract()>default </#if>${tc.include("core.Method", method)}
</#list>

<#assign simpleName = cdInterface.getName()?remove_beginning("AST")>
  interface Literals {
    org.eclipse.emf.ecore.EEnum Constants${simpleName} = eINSTANCE.getConstants${simpleName}
    <#list cdInterface.getAttributeList() as attribute>

    </#list>
  }
}