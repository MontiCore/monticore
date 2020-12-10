<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolMap", "mill")}
  for (de.monticore.symboltable.serialization.json.JsonObject symbol :
      de.monticore.symboltable.serialization.JsonDeSers.getSymbols(scopeJson)) {
    String kind = symbol.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.KIND);
<#assign count=0>
    switch (kind) {
<#list symbolMap?keys as kind>
      case "${kind}":
        ${kind} s${count} = (${kind})
            ${mill}.globalScope().getDeSer(kind).deserialize(symbol);
        scope.add(s${count});
  <#if symbolMap[kind]>
        scope.addSubScope(s${count}.getSpannedScope());
  </#if>
        break;
<#assign count++>
</#list>
      default:
        Log.warn("Ignoring deserialization of unknown symbol kind '"+kind+"'");
    }
  }

