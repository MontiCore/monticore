<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName")}
<#assign service = glex.getGlobalVar("service")>
     update${attributeName?cap_first}Loader();
     if (${attributeName}Loader.isSymbolLoaded() && ${attributeName}Loader.getName() != null && ${attributeName}Loader.getEnclosingScope() != null) {
       return ${attributeName}Loader.getLoadedSymbol();
     }
     Log.error("0xA7003${service.getGeneratedErrorCode(attributeName + referencedProdName)} ${attributeName} can't return a value. It is empty.");
     // Normally this statement is not reachable
     throw new IllegalStateException();
     