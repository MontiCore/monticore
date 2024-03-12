<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("optionals")}

this.traverser.getTraversedElements().clear();

<#list optionals as optionals>
    ${optionals.getName()} = Optional.empty();
</#list>