<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "attrName", "jsonParamName", "typeMap")}
List<${attrType}> result = new ArrayList<>();
if(!${jsonParamName}.hasMember("${attrName()}"){
  return result;
}
for(JsonElement e : ${jsonParamName}.getArrayMemberOpt("${attrName()}")){
  result.add(${jsonParamName}.${typeMap});
}
return result;
