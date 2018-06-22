<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "referencedSymbol", "symbolName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
     if(!${attribute.getName()}Definition.isPresent()){
       if (get${attribute.getName()?cap_first}DefinitionOpt().isPresent()) {
         return get${attribute.getName()?cap_first}DefinitionOpt().get().get${symbolName}SymbolOpt();
       }else {
         return Optional.empty();
       }
     }
     return ${attribute.getName()}Definition.get().get${symbolName}SymbolOpt();