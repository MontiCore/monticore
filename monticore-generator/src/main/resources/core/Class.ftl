${tc.signature("cdClass")}

${cdClass.printModifier()} ${cdClass.getName()} extends ${cdClass.printSuperClass()} implements ${cdClass.printInterfaces()} {

<#list cdClass.getCDAttributeList() as attribute>
    ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdClass.getCDConstructors() as constructor>
    ${tc.include("core.Constructor", constructor)}
</#list>

<#list cdClass.getCDMethodList() as method>
    ${tc.include("core.Method", method)}
</#list>
}