<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolMap", "mill")}
  for (de.monticore.symboltable.serialization.json.JsonObject symbol :
      de.monticore.symboltable.serialization.JsonDeSers.getSymbols(scopeJson)) {
    String kind = symbol.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.KIND);

    switch (kind) {
<#list symbolMap?keys as kind>
      case "${kind}":
        ${kind} s = (${kind})
            ${mill}.getDeSer(kind).deserialize(symbol);
        scope.add(s);
  <#if symbolMap[kind]>
        scope.addSubScope(s.getSpannedScope());
  </#if>
        break;
</#list>
      default:
        Log.warn("Ignoring deserialization of unknown symbol kind '"+kind+"'");
    }
  }

