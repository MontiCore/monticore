<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "existsHWCExtension")}

public <#if existsHWCExtension>abstract </#if>
		class ${className} {

  <#-- Iterate over list of states: one factory method for each state -->
  ${tc.include("FactoryStateMethod.ftl", ast.getStateList())}

}
