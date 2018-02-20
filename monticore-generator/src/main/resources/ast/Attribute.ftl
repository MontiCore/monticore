<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java attribute
  
  @params    ASTCDAttribute     $ast
  @result    
  
-->
  ${tc.signature("ast", "astType")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign attributeName = genHelper.getJavaConformName(ast.getName())>
  <#assign attributeValue = genHelper.getAstAttributeValue(ast, astType)>
  ${ast.printModifier()} ${ast.printType()} ${attributeName}<#if attributeValue?has_content> = ${attributeValue}</#if>;
