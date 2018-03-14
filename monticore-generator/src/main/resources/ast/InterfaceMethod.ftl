<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method

  @params    ASTCDMethod     $ast
  @result

-->
${tc.signature("ast", "astType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#if genHelper.isDefault(ast)>
default ${ast.printModifier()} ${ast.printReturnType()} ${ast.getName()}(${ast.printParametersDecl()}) ${ast.printThrowsDecl()}
  {
  ${tc.include("ast.ErrorIfNull")}
  ${tc.includeArgs("ast.EmptyMethodBody", [ast, astType])}
  }
<#else>
${ast.printModifier()} ${ast.printReturnType()} ${ast.getName()}(${ast.printParametersDecl()}) ${ast.printThrowsDecl()};
</#if>