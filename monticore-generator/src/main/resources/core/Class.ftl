${tc.signature("cdClass")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${tc.include("core.Package")}

${tc.include("core.Imports")}
import de.monticore.ast.ASTCNode;

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
