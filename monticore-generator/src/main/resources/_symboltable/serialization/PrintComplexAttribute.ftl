<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "methodName", "operation", "returnValue")}
  Log.error("Unable to ${operation} type ${attrType}. " +
      "Please override the method ${methodName} using the TOP mechanism!");
//return ${returnValue};