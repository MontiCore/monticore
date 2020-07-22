<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolMap")}
  switch (kind) {
<#list symbolMap?keys as kind>
    case "${symbolMap[kind]}":
      deserialize${kind}(symbol, scope);
      return true;
</#list>
  default:
    return false;
}