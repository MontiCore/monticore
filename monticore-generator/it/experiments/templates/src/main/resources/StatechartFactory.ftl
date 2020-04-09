<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("states","className", "existsHWCExtension")}

public <#if existsHWCExtension>abstract </#if>class ${className} {

  ${tc.include("FactoryStateMethod.ftl",states)}

}