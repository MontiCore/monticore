<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "methodName", "operation", "returnStatement")}
  Log.error("Unable to ${operation} type ${attrType}. " +
      "Please override the method ${methodName} using the TOP mechanism!");
  ${returnStatement}