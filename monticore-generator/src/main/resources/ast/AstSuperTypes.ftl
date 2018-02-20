<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>
${genHelper.getSuperClass(ast)} implements ${tc.include("ast.AstSuperInterfaces")} ${genHelper.getASTNodeBaseType()}
