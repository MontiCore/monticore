${tc.signature("cdClass")}

${cdClass.printModifier()} ${cdClass.getName()} extends ${cdClass.printSuperClass()} implements ${cdClass.printInterfaces()} {

<#list cdClass.getCDAttributeList() as attribute>
    ${tc.includeArgs("core.Attribute", [attribute])}
</#list>

<#list cdClass.getCDConstructorList() as constructor>
    ${tc.includeArgs("core.Constructor", [constructor])}
</#list>

<#list cdClass.getCDMethodList() as method>
    ${tc.includeArgs("core.Method", [method])}
</#list>
}