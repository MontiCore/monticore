<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
     update${attributeName?cap_first}Loader();
     if (${attributeName}Loader.getName() != null && ${attributeName}Loader.getEnclosingScope() != null) {
        return ${attributeName}Loader.isSymbolLoaded();
     }
     return false;
     