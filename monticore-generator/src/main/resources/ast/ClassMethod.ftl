<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method
  
  @params    ASTCDMethod     $ast
  @result    
  
-->
  ${tc.signature("ast", "astType")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  ${ast.printAnnotation()}
  ${ast.printModifier()} ${ast.printReturnType()} ${ast.getName()}(${ast.printParametersDecl()}) ${ast.printThrowsDecl()}<#if genHelper.isAbstract(ast, astType)>;
  <#else>
  { 
     ${tc.include("ast.ErrorIfNull")}
     ${tc.includeArgs("ast.EmptyMethodBody", [ast, astType])}
  } 
  </#if>
