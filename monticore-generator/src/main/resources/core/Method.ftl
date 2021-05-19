<#-- (c) https://github.com/MontiCore/monticore -->
<#assign isAbstract = ast.getModifier().isAbstract()>
<#assign service = glex.getGlobalVar("service")>
${cdPrinter.printSimpleModifier(ast.getModifier())} ${cdPrinter.printType(ast.getMCReturnType())} ${ast.getName()} (${cdPrinter.printCDParametersDecl(ast.getCDParameterList())})
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if> {
<#if isAbstract>;<#else> {
    <#if service.isMethodBodyPresent(ast)>
      ${ service.getMethodBody(ast) }
    <#else>
      ${tc.include("core.EmptyBody")}
    </#if>
}
</#if>
