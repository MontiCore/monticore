<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "symbolClass")}
   if ((${attributeName} != null) && isPresentEnclosingScope()) {
     Optional<${symbolClass}> classSymbol = enclosingScope.get().resolve(symbol.getName(), ${symbolClass}.KIND);
     if(classSymbol.isPresent()){
       set${attributeName?cap_first}Reference(symbol);
       set${attributeName?cap_first}(symbol.getName());
      }
    }