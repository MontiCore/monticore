<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolAttributeNameList")}

Set<ISymbol> allSymbols = new LinkedHashSet<>();
<#list symbolAttributeNameList as attrName>
    allSymbols.addAll(get${attrName?cap_first}().values());
</#list>
return allSymbols.size();