<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package")}
// Test:Replace Template
/* (c) https://github.com/MontiCore/monticore */

${defineHookPoint("ClassContent:addComment")}

${tc.include("cd2java.Package", package)}

${tc.include("cd2java.Imports")}
${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
${defineHookPoint("ClassContent:Imports")}

${tc.include("cd2java.Annotations")}

<#-- Annotations hook -->
${defineHookPoint("ClassContent:Annotations")}

${cdPrinter.printSimpleModifier(ast.getModifier())} class ${ast.getName()} <#rt><#lt>
<#if ast.isPresentCDExtendUsage()>extends ${cdPrinter.printType(ast.getCDExtendUsage().getSuperclass(0))} </#if> <#rt><#lt>
<#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#-- Elements HOOK -->
${defineHookPoint("ClassContent:Elements")}

<#list ast.getCDAttributeList() as attribute>
    ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list ast.getCDConstructorList() as constructor>
    ${tc.include("cd2java.Constructor", constructor)}
</#list>

<#list ast.getCDMethodList() as method>
    ${tc.include("cd2java.Method", method)}
</#list>

}


