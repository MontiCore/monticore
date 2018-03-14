<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java attribute for a constant
  
  @params    ASTCDAttribute     $ast
  @result    
  
-->
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign attributeValue = genHelper.getAstAttributeValue(ast)>
  ${ast.printModifier()} ${ast.printType()} ${ast.getName()}<#if attributeValue?has_content> = ${attributeValue}</#if>;
