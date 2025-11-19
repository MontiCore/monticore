<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolAttributeNameList")}

<#-- we need the full qualified name here to avoid clashes between generated artifacts and the MontiCore runtime interface de.monticore.symboltable.ISymbol -->
Set<de.monticore.symboltable.ISymbol> allSymbols = new HashSet<>();
<#list symbolAttributeNameList as attrName>
    allSymbols.addAll(get${attrName?cap_first}().values());
</#list>
return allSymbols.size();