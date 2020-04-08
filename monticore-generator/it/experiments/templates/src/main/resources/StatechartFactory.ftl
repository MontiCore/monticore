<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("states","className")}
public <#if className?ends_with("TOP")>abstract </#if>class ${className} {

  ${tc.include("FactoryStateMethod.ftl",states)}

}