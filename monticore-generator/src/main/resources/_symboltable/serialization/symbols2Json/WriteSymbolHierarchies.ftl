<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolToSuperSymbolsMap")}
// Store the symbol hierarchy within the exported JSON to adapt to unknown symbols of the target lang
<#list symbolToSuperSymbolsMap as symbolClassName, superSymbolClassName>
	getJsonPrinter().member(${symbolClassName}.class.getName(), ${superSymbolClassName}.class.getName());
</#list>