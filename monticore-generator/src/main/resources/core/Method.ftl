<#assign isAbstract = ast.getModifier().isAbstract()>
<#assign service = glex.getGlobalVar("service")>
${ast.printModifier()} ${ast.printReturnType()} ${ast.getName()} (${ast.printParametersDecl()}) ${ast.printThrowsDecl()}<#if isAbstract>;<#else> {
    <#if service.isMethodBodyPresent(ast)>
      ${ service.getMethodBody(ast) }
    <#else>
      ${tc.include("core.EmptyBody")}
    </#if>
}
</#if>
