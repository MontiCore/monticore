<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
    this.${attribute.getName()} = Optional.ofNullable(${attribute.getName()});
    return this.realBuilder;
