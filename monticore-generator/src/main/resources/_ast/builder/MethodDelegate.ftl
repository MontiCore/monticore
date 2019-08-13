<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "methodName", "paramCall")}
    this.get${attributeName?cap_first}List().${methodName}(${paramCall});
    return this.realBuilder;
