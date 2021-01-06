<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolAttributeNameList")}

Set<ISymbol> allSymbols = new HashSet<>();
<#list symbolAttributeNameList as attrName>
    allSymbols.addAll(get${attrName?cap_first}());
</#list>
return allSymbols.size();