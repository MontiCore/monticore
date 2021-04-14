<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname")}

return ${grammarname}Mill
        .${grammarname}SymbolTableCreatorBuilder()
        .addToScopeStack(new ${grammarname}GlobalScope(new ModelPath(), "aut"))
        .build()
        .createFromAST(ast);