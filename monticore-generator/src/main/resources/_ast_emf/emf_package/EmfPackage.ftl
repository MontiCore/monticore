${tc.signature("packageInterface", "astCDDefinition")}
${tc.include("core.Package")}

${tc.include("core.Imports")}

${packageInterface.printModifier()} interface ${packageInterface.getName()} <#rt><#lt>
<#if !packageInterface.isEmptyInterfaces()>extends ${packageInterface.printInterfaces()} </#if>{


<#list packageInterface.getCDAttributeList() as attribute>
    ${tc.include("core.Attribute", attribute)}
</#list>

<#list packageInterface.getCDMethodList() as method>
    <#if !method.getModifier().isAbstract()>default </#if>${tc.include("core.Method", method)}
</#list>

  interface Literals {
    org.eclipse.emf.ecore.EEnum Constants${astCDDefinition.getName()} = eINSTANCE.getConstants${astCDDefinition.getName()}
    <#list astCDDefinition.getCDClassList() as cdClass>
      org.eclipse.emf.ecore.EClass ${cdClass.getName()} = eINSTANCE.get${cdClass.getName()}()
    </#list>
    <#list astCDDefinition.getCDInterfaceList() as cdInterface>
      org.eclipse.emf.ecore.EClass ${cdInterface.getName()} = eINSTANCE.get${cdInterface.getName()}()
    </#list>
  }
}