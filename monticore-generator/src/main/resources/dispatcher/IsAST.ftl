<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "parameterName", "handlerName")}

reset();
${parameterName}.accept(this.getTraverser());
return ${handlerName?uncap_first}.is${name?cap_first}();