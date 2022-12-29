<#-- (c) https://github.com/MontiCore/monticore -->
${signature("alternativeAst")}<#t>
${defineHookPointWithDefault("WithoutAst", "default text 1")}
${defineHookPointWithDefault3("WithAst", ast, "default text 2")}
${defineHookPointWithDefault3("WithAlternativeAst", alternativeAst, "default text 3")}
