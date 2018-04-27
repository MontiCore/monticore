<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "referencedSymbol", "symbolName")}
     if ((ast.getName() != null) && isPresentEnclosingScope()) {
     Optional<${referencedSymbol}> classSymbol = enclosingScope.get().resolve(ast.getName(), ${referencedSymbol}.KIND);
     if(classSymbol.isPresent()){
       set${attributeName?cap_first}Reference(classSymbol.get());
       set${attributeName?cap_first}(ast.getName());
      }
    }