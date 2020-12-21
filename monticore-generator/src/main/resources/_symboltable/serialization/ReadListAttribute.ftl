<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("jsonParamName", "attrType", "attrName", "typeMap")}
  List<${attrType}> result = new ArrayList<>();
  if(!${jsonParamName}.hasMember("${attrName}")) {
    return result;
  }
  for(de.monticore.symboltable.serialization.json.JsonElement e : ${jsonParamName}.getArrayMember("${attrName}")) {
    result.add(e.${typeMap});
  }
  return result;