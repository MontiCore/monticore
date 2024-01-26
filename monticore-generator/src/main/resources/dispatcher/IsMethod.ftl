<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "parameterName")}

reset();
${parameterName}.accept(this.getTraverser());
return opt${name?cap_first}.isPresent();