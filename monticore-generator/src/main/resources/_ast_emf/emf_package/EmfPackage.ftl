<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageInterface", "astCDDefinition")}
${tc.include("core.Package")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${tc.include("core.Imports")}

${cdPrinter.printSimpleModifier(packageInterface.getModifier())} interface ${packageInterface.getName()} <#rt><#lt>
<#if cdInterface.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(cdInterface.getCDExtendUsage().getSuperclassList())} </#if>{


<#list packageInterface.getCDAttributeList() as attribute>
    ${tc.include("core.Attribute", attribute)}
</#list>

<#list packageInterface.getCDMethodList() as method>
    <#if !method.getModifier().isAbstract()>default </#if>${tc.include("core.Method", method)}
</#list>

  interface Literals {
    org.eclipse.emf.ecore.EEnum Constants${astCDDefinition.getName()} = eINSTANCE.getConstants${astCDDefinition.getName()}();
    <#list astCDDefinition.getCDClassesList() as cdClass>
      org.eclipse.emf.ecore.EClass ${cdClass.getName()} = eINSTANCE.get${cdClass.getName()}();
    </#list>
    <#list astCDDefinition.getCDInterfacesList() as cdInterface>
        <#if cdInterface.getName() != "AST"+ astCDDefinition.getName() + "Node">
      org.eclipse.emf.ecore.EClass ${cdInterface.getName()} = eINSTANCE.get${cdInterface.getName()}();
        </#if>
    </#list>
  }
}
