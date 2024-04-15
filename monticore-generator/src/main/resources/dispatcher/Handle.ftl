<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "superTypes", "parameterName")}

<#list superTypes as superType>
  this.getTraverser().handle((${superType}) ${parameterName});
</#list>

this.setOpt${name}(${parameterName});