<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("transitionName","modelName")}
    abstract void handle${transitionName?cap_first}(${modelName?cap_first} model);
