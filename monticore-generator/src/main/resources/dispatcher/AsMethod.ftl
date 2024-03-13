<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "parameterName")}
<#assign service = glex.getGlobalVar("service")>

if(!is${name}(${parameterName})) {
  Log.error("0x54987${service.getGeneratedErrorCode(name)} Cannot cast node to type ${name}.");
}
return getOpt${name}();