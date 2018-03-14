<#-- (c) https://github.com/MontiCore/monticore -->
  <#assign nameHelper = glex.getGlobalVar("javaNameHelper")>
  <#assign astHelper = glex.getGlobalVar("astHelper")>
  <#assign typeHelper = tc.instantiate("de.monticore.types.TypesHelper")>
  <#assign del = "">
  <#list ast.getCDParameterList() as parameter>   ${del}${astHelper.printFullType(parameter.getType())} ${nameHelper.javaAttribute(parameter.getName())} 
      <#assign del = " ,\n     ">
  </#list>
