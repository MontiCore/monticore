<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("names")}

<#list names as name>
  is${name} = false;
  opt${name} = Optional.empty();
</#list>