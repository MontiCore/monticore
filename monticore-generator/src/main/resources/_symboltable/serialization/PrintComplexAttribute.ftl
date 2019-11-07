<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "methodName", "operation", "returnValue")}
Log.error("Unable to ${operation} type ${attribute.printType()}. " +
"Please override the method ${methodName} using the TOP mechanism!");
return ${returnValue};