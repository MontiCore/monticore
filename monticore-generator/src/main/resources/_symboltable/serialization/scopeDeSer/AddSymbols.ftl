<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolNameList")}
<#list symbolNameList?keys as symbol>
  if (scopeJson.hasMember("${symbol?uncap_first}s")) {
    List<de.monticore.symboltable.serialization.json.JsonElement> ${symbol?uncap_first}s = scopeJson.getArrayMember("${symbol?uncap_first}s");
    for (de.monticore.symboltable.serialization.json.JsonElement ${symbol?uncap_first} : ${symbol?uncap_first}s) {
      deserialize${symbol}(${symbol?uncap_first}.getAsJsonObject(), scope);
    }
  }
</#list>