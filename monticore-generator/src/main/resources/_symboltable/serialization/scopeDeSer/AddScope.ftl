<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolNameList")}
<#list symbolNameList as symbol>
  if (scopeJson.containsKey("${symbol?uncap_first}Symbols")) {
    List<de.monticore.symboltable.serialization.json.JsonElement> ${symbol?uncap_first}Symbols = scopeJson.get("${symbol?uncap_first}Symbols").getAsJsonArray().getValues();
    for (de.monticore.symboltable.serialization.json.JsonElement ${symbol?uncap_first}Symbol : ${symbol?uncap_first}Symbols) {
      deserialize${symbol}Symbol(${symbol?uncap_first}Symbol.getAsJsonObject(), scope);
    }
  }
</#list>