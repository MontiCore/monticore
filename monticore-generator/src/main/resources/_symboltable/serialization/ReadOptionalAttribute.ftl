<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("jsonParamName", "attrName", "type4Ast")}
if(${jsonParamName}.hasMember("${attrName}")){
  return java.util.Optional.of(${jsonParamName}.${type4Ast});
}
else{
  return java.util.Optional.empty();
}
