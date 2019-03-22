${tc.signature("cdEnum")}

${tc.include("core.Package")}

public enum ${cdEnum.getName()}<#if !cdEnum.isEmptyInterfaces()> implements ${cdEnum.printInterfaces()}</#if> {

<#list cdEnum.getCDEnumConstantList() as constants>
  <#if !constants?is_first>,</#if>${tc.include("core.EmptyConstants", constants)}
</#list>
;

<#list cdEnum.getCDAttributeList() as attribute>
  ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdEnum.getCDConstructorList() as constructor>
  ${tc.include("core.Constructor", constructor)}
</#list>

<#list cdEnum.getCDMethodList() as method>
  ${tc.include("core.Method", method)}
</#list>
}
