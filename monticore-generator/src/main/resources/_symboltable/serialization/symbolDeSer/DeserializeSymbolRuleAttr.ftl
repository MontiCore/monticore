<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "deSerName", "returnType")}
Log.error("Unable to deserialize Json of type ${attribute.printType()}. " +
"Please override the method ${deSerName}#deserialize${attribute.getName()?cap_first}(JsonObject) using the TOP mechanism!");
return ${returnType};