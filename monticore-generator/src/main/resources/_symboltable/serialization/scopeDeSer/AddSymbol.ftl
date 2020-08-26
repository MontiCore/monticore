<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolMap")}
symbol.getMember(de.monticore.symboltable.serialization.JsonDeSers.KIND).getAsJsonString().setValue(kind);
  switch (kind) {
<#list symbolMap?keys as kind>
    case "${symbolMap[kind]}":
      deserialize${kind}(symbol, scope);
      return true;
</#list>
  default:
    return false;
}