<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("booleans", "optionals")}

<#list booleans as boolean>
  ${boolean} = false;
</#list>
<#list optionals as optionals>
    ${optionals} = Optional.empty();
</#list>