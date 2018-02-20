<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java attribute
  
  @params    ASTCDAttribute     $ast
  @result    
  
-->
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign typeHelper = tc.instantiate("de.monticore.types.TypesHelper")>
  <#assign attributeValue = genHelper.getAstAttributeValueForBuilder(ast)>
    ${ast.printModifier()} ${ast.printType()} ${ast.getName()}<#if attributeValue?has_content> = ${attributeValue}</#if>;
