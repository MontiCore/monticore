<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleVisitorName")}
  this.${simpleVisitorName?uncap_first} = Optional.ofNullable(${simpleVisitorName?uncap_first});
  this.${simpleVisitorName?uncap_first}.ifPresent(handler -> handler.setTraverser(this));
