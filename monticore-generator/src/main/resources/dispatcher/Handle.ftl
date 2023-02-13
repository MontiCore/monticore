<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "superTypes")}

<#list superTypes as superType>
  this.handle((${superType}) node);
</#list>
is${name} = true;
opt${name} = Optional.of(node);