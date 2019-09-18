<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolNameList")}
<#list symbolNameList?keys as symbol>
  if (scopeJson.containsKey("${symbol?uncap_first}Symbols")) {
    List<de.monticore.symboltable.serialization.json.JsonElement> ${symbol?uncap_first}s = scopeJson.get("${symbol?uncap_first}s").getAsJsonArray().getValues();
    for (de.monticore.symboltable.serialization.json.JsonElement ${symbol?uncap_first} : ${symbol?uncap_first}s) {
      deserialize${symbol}(${symbol?uncap_first}.getAsJsonObject(), scope);
    }
  }
</#list>