<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("hasSpannedScope", "symbolRuleAttributes")}
  de.monticore.symboltable.serialization.JsonPrinter p = symbols2Json.getJsonPrinter();
  p.beginObject();
  p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
  p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

  // serialize symbolrule attributes
<#list symbolRuleAttributes as attr>
  // serialize ${attr.getName()}
</#list>

<#if hasSpannedScope>
  // serialize spanned scope
  if (toSerialize.getSpannedScope().isExportingSymbols()
    && toSerialize.getSpannedScope().getSymbolsSize() > 0) {
//      printer.beginObject(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE);
    toSerialize.getSpannedScope().accept(symbols2Json.getTraverser());
  }
</#if>

  serializeAddons(toSerialize, symbols2Json);
  p.endObject();

return p.toString();
