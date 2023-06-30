<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("booleans", "optionals", "superDispatchers")}

this.traverser.getTraversedElements().clear();

<#list superDispatchers as superDispatcher>
    ${superDispatcher.getName()}.reset();
</#list>

<#list booleans as boolean>
  ${boolean.getName()} = false;
</#list>
<#list optionals as optionals>
    ${optionals.getName()} = Optional.empty();
</#list>