<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName")}
<#assign service = glex.getGlobalVar("service")>
     update${attributeName?cap_first}Surrogate();
     if (${attributeName}Surrogate.lazyLoadDelegate()!=null && ${attributeName}Surrogate.getName() != null && ${attributeName}Surrogate.getEnclosingScope() != null) {
       return ${attributeName}Surrogate.lazyLoadDelegate();
     }
     Log.error("0xA7003${service.getGeneratedErrorCode(attributeName + referencedProdName)} ${attributeName} can't return a value. It is empty.");
     // Normally this statement is not reachable
     throw new IllegalStateException();
     