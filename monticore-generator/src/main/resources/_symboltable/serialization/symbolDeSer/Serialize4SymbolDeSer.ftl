<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("hasSpannedScope", "symbolRuleAttributes")}
  de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
  p.beginObject();
  p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
  p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

  // serialize symbolrule attributes
<#list symbolRuleAttributes as attr>
  serialize${attr.getName()?cap_first}(toSerialize, s2j);
</#list>

<#if hasSpannedScope>
  // serialize spanned scope
  if (toSerialize.getSpannedScope().isExportingSymbols()
    && toSerialize.getSpannedScope().getSymbolsSize() > 0) {
    toSerialize.getSpannedScope().accept(s2j.getTraverser());
  }
</#if>

  serializeAddons(toSerialize, s2j);
  p.endObject();

  return p.toString();
