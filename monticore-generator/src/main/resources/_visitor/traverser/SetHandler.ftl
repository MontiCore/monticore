<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleHanlderName")}
  this.${simpleHanlderName?uncap_first} = Optional.ofNullable(${simpleHanlderName?uncap_first});
  if (this.${simpleHanlderName?uncap_first}.isPresent()) {
    this.${simpleHanlderName?uncap_first}.get().setRealThis(getRealThis());
  }
  // register the ${simpleHanlderName} also to realThis if not this
  if (getRealThis() != this) {
    // to prevent recursion we must differentiate between realThis being
    // the current this or another instance.
    getRealThis().set${simpleHanlderName}(${simpleHanlderName?uncap_first});
  }