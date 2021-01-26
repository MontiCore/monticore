<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolFullName", "symbols")}
  List<${symbolFullName}> res = new ArrayList<>();
<#list symbols as simpleName>
  res.addAll(resolve${simpleName}LocallyMany(foundSymbols, name, modifier, predicate));
</#list>
  return res;