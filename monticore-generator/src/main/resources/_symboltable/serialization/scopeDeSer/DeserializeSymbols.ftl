<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolMap", "mill", "errorCode")}
  for (de.monticore.symboltable.serialization.json.JsonObject symbol :
      de.monticore.symboltable.serialization.JsonDeSers.getSymbols(scopeJson)) {
    String kind = de.monticore.symboltable.serialization.JsonDeSers.getKind(symbol);
<#assign count=0>
    switch (kind) {
<#list symbolMap?keys as kind>
      case "${kind}":
        ${kind} s${count} = (${kind})
            ${mill}.globalScope().getSymbolDeSer(kind).deserialize(symbol);
        scope.add(s${count});
  <#if symbolMap[kind]>
        scope.addSubScope(s${count}.getSpannedScope());
  </#if>
        break;
<#assign count++>
</#list>
      default:
        Log.warn("0xA1234x${errorCode} No DeSer found to deserialize symbol of kind `" + kind
            + "`. The following will be ignored: " + symbol);
    }
  }

