<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
     update${attributeName?cap_first}Surrogate();
     if (${attributeName}Surrogate.getName() != null && ${attributeName}Surrogate.getEnclosingScope() != null) {
        return ${attributeName}Surrogate.lazyLoadDelegate()!=null;
     }
     return false;
     