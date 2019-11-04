<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdClass")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign service = glex.getGlobalVar("service")>
/* (c) https://github.com/MontiCore/monticore */
${tc.include("core.Package")}

${tc.include("core.Imports")}
import de.monticore.ast.ASTCNode;

${tc.include("core.Annotations")}
${cdPrinter.printSimpleModifier(cdClass.getModifierOpt())} class ${cdClass.getName()} <#rt><#lt>
<#if cdClass.isPresentSuperclass()>extends ${cdPrinter.printType(cdClass.getSuperclass())} </#if> <#rt><#lt>
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
