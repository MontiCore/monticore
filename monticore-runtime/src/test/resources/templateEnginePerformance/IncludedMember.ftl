<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name")}
  private ${type} ${name};

  public ${type} get${name?cap_first}() {
    return this.${name};
  }
