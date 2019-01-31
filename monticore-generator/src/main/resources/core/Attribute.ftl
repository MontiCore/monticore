${tc.signature("attribute")}
<#assign value = attribute.printValue()>
${attribute.printModifier()} ${attribute.printType()} ${attribute.getName()}<#if value?has_content> = ${value}</#if>;