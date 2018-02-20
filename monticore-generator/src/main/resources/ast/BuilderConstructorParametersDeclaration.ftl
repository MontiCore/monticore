<#-- (c) https://github.com/MontiCore/monticore -->
  <#assign nameHelper = glex.getGlobalVar("javaNameHelper")>
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  ${tc.signature("constructorParameters")}
    <#assign del = "">
    <#list constructorParameters as parameter>
         ${del}this.${nameHelper.javaAttribute(parameter.getName())}
      <#assign del = ",">
    </#list> 
