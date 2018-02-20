<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method
  
  @params    ASTCDMethod     $ast
  @result    
  
-->
  ${tc.signature("ast", "astType")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  ${ast.printModifier()} ${ast.getName()} (${tc.include("ast.ParametersDeclaration")} 
    )<#assign excs = ast.printThrowsDecl()> <#if excs?length != 0> throws ${excs} </#if> 
  ${tc.includeArgs("ast.EmptyMethodBody", [ast, astType])}
