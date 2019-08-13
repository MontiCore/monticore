<#-- (c) https://github.com/MontiCore/monticore -->
${ast.printModifier()} ${ast.getName()}(${ast.printParametersDecl()}) ${ast.printThrowsDecl()} {
    ${tc.include("core.EmptyBody")}
}
