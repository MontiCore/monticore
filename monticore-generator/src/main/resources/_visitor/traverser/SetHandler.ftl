<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleVisitorName")}
  this.${simpleVisitorName?uncap_first} = Optional.ofNullable(${simpleVisitorName?uncap_first});
  if (this.${simpleVisitorName?uncap_first}.isPresent()) {
    this.${simpleVisitorName?uncap_first}.get().setTraverser(this);
  }
