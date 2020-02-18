<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "attrName", "jsonParamName", "typeMap")}
  ${attrType} result = new ArrayList<>();
  if(!${jsonParamName}.hasMember("${attrName}")) {
    return result;
  }
  for(de.monticore.symboltable.serialization.json.JsonElement e : ${jsonParamName}.getArrayMember("${attrName}")) {
    result.add(${jsonParamName}.${typeMap});
}
return result;