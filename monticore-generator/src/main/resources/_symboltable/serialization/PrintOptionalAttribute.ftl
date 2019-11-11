<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "jsonParamName", "typeMap")}
if(${jsonParamName}.hasMember("${attrName}")){
  return java.util.Optional.of(${jsonParamName}.${typeMap});
}
else{
return java.util.Optional.empty();
}
