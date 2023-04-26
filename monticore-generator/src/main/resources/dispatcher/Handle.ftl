<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "superTypes", "handlerName")}

<#list superTypes as superType>
  this.getTraverser().handle((${superType}) node);
</#list>
this.setIs${name}(true);
this.setOpt${name}(node);