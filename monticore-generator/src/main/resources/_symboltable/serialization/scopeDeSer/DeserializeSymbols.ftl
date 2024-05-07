<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolMap", "mill", "errorCode", "scopeDeserName", "scopeInterfaceName")}

  for (de.monticore.symboltable.serialization.json.JsonObject symbol :
      de.monticore.symboltable.serialization.JsonDeSers.getSymbols(scopeJson)) {
    Optional${"<"}String${">"} kind = de.monticore.symboltable.serialization.JsonDeSers.getKindOpt(symbol);

    if (!kind.isPresent()) {
      Log.error("0xA1238 Serialized object does not have a kind attribute: '" + symbol + "'.");
      continue;
    }

    de.monticore.symboltable.serialization.ISymbolDeSer deSer = ${mill}.globalScope().getSymbolDeSer(kind.get());

    if (null == deSer) {
      Log.debug(
        "0xA1234xx81662 No DeSer found to deserialize symbol of kind `" + kind
        + "`. Falling back to default for symbols of unknown kinds.",
        ${scopeDeserName}.class.getName()
      );

      deSer = new SymbolWithScopeOfUnknownKindDeSer(this, ${mill}::scope);
      SymbolWithScopeOfUnknownKind s0 = (SymbolWithScopeOfUnknownKind) deSer.deserialize(symbol);
      scope.add(s0);
      scope.addSubScope((${scopeInterfaceName}) s0.getSpannedScope());
    }
<#assign count=0>
<#list symbolMap?keys as kind>
  else if ("${kind}".equals(kind)
        || "${kind}".equals(deSer.getSerializedKind())) {
      ${kind} s${count} = (${kind}) deSer.deserialize(scope, symbol);
      scope.add(s${count});
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
