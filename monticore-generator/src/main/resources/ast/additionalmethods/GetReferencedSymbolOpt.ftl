<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast","attribute", "referencedSymbol", "symbolName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
     if(!${attribute.getName()}Definition.isPresent()){
       if ((${attribute.getName()} != null) && isPresentEnclosingScope()) {
<#if genHelper.isOptional(attribute)>
         Optional<${referencedSymbol}> symbol = enclosingScope.get().resolve(${attribute.getName()}.get(), ${referencedSymbol}.KIND);
<#else>
         Optional<${referencedSymbol}> symbol = enclosingScope.get().resolve(${attribute.getName()}, ${referencedSymbol}.KIND);
</#if>
         ${attribute.getName()}Definition = symbol.get().get${symbolName}Node();
         if(symbol.isPresent()){
           return symbol;
         }
           return Optional.empty();
       }
      }
      return ${attribute.getName()}Definition.get().get${symbolName}SymbolOpt();
