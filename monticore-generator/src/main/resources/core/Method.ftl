<#assign isAbstract = ast.getModifier().isAbstract()>
${ast.printModifier()} ${ast.printReturnType()} ${ast.getName()} (${ast.printParametersDecl()}) ${ast.printThrowsDecl()}<#if isAbstract>;<#else> {
    ${tc.include("core.EmptyBody")}
}
</#if>