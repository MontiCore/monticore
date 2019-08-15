<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleVisitorName")}
  this.${simpleVisitorName?uncap_first} = Optional.ofNullable(${simpleVisitorName?uncap_first});
  if (this.${simpleVisitorName?uncap_first}.isPresent()) {
    this.${simpleVisitorName?uncap_first}.get().setRealThis(getRealThis());
  }
  // register the ${simpleVisitorName} also to realThis if not this
  if (getRealThis() != this) {
    // to prevent recursion we must differentiate between realThis being
    // the current this or another instance.
    getRealThis().set${simpleVisitorName}(${simpleVisitorName?uncap_first});
  }