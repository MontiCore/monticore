<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolSimpleName","symbolFullName", "spansScope", "scopeSimpleName","scopeFullName", "symTabMill")}
  ${symbolFullName} symbol = ${symbolSimpleName?uncap_first}DeSer.deserialize${symbolSimpleName}(symbolJson, scope);
  scope.add(symbol);
<#if spansScope>
  ${scopeFullName} spannedScope;
  if(symbolJson.hasMember(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE)){
    spannedScope = this.deserialize${scopeSimpleName}(
    symbolJson.getObjectMember(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE));
  }
  else {
    spannedScope = ${symTabMill}.${scopeSimpleName?uncap_first}Builder().build();
  }
  symbol.setSpannedScope(spannedScope);
  scope.addSubScope(spannedScope);
</#if>
