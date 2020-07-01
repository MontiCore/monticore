<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolSimpleName","symbolFullName", "spansScope", "scopeSimpleName","scopeFullName")}
  ${symbolFullName} symbol = ${symbolSimpleName?uncap_first}DeSer.deserialize${symbolSimpleName}(symbolJson, scope);
  scope.add(symbol);
<#if spansScope>
  if(symbolJson.hasMember(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE)){
  ${scopeFullName} spannedScope = this.deserialize${scopeSimpleName}(
  symbolJson.getObjectMember(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE));
  symbol.setSpannedScope(spannedScope);
  scope.addSubScope(spannedScope);
  }
</#if>
