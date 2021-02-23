<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolMap", "mill", "errorCode")}

  for (de.monticore.symboltable.serialization.json.JsonObject symbol :
      de.monticore.symboltable.serialization.JsonDeSers.getSymbols(scopeJson)) {
    String kind = de.monticore.symboltable.serialization.JsonDeSers.getKind(symbol);
    de.monticore.symboltable.serialization.ISymbolDeSer deSer = ${mill}.globalScope().getSymbolDeSer(kind);
    if (null == deSer) {
      Log.warn("0xA1234x${errorCode} No DeSer found to deserialize symbol of kind `" + kind
        + "`. The following will be ignored: " + symbol);
    continue;
    }

<#assign count=0>
<#list symbolMap?keys as kind>
  <#if count!=0>else</#if> if ("${kind}".equals(kind)
        || "${kind}".equals(deSer.getSerializedKind())) {
      ${kind} s0 = (${kind}) deSer.deserialize(symbol);
      scope.add(s0);
<#if symbolMap[kind]>
      scope.addSubScope(s${count}.getSpannedScope());
</#if>
    }
<#assign count++>
</#list>
<#if count!=0>
    else {
      Log.warn("0xA1634x${errorCode} Unable to integrate deserialization with DeSer for kind `" + kind
        + "`. The following will be ignored: " + symbol);
    }
</#if>
  }
