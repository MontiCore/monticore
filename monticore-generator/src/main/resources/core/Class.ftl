${tc.signature("cdClass")}

${tc.include("core.Imports")}

${cdClass.printModifier()} class ${cdClass.getName()}
<#if cdClass.isPresentSuperclass()>extends ${cdClass.printSuperClass()} </#if>
<#if !cdClass.isEmptyInterfaces()>implements ${cdClass.printInterfaces()} </#if>{

<#list cdClass.getCDAttributeList() as attribute>
    ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdClass.getCDConstructorList() as constructor>
    ${tc.include("core.Constructor", constructor)}
</#list>

<#list cdClass.getCDMethodList() as method>
    ${tc.include("core.Method", method)}
</#list>
}