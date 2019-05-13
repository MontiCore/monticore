<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass")}
value = new ${domainClass.getName()}(this.name);
<#list domainClass.getCDAttributeList() as attribute>
    <#if attribute.getName() != "name">
value.set${attribute.getName()?cap_first}(this.${attribute.getName()});
    </#if>
</#list>