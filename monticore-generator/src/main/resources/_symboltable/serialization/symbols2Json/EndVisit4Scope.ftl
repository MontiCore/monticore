<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("rteScope", "symbols2Json", "isArtifactScope")}

getJsonPrinter().endArray();
scopeDeSer.serializeAddons(node, getRealThis());
<#if isArtifactScope>
	// store symbol hierarchy to fallback in case of unknown symbols
	getJsonPrinter().beginObject(de.monticore.symboltable.serialization.JsonDeSers.SYMBOL_HIERARCHY);
	this.writeSymbolHierarchies();
	getJsonPrinter().endObject();
</#if>
getJsonPrinter().endObject();