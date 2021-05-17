<#-- (c) https://github.com/MontiCore/monticore -->
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.getName()}(${ast.printParametersDecl()}) ${ast.printThrowsDecl()} {
    ${tc.include("core.EmptyBody")}
}
