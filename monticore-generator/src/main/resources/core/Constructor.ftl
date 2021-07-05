<#-- (c) https://github.com/MontiCore/monticore -->
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.getName()}(${cdPrinter.printCDParametersDecl(ast.getCDParameterList())})
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if> {
  ${tc.include("core.EmptyBody")}
}
