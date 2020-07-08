<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "methodName", "operation", "returnStatement", "generatedError")}
  Log.error(""0xA7019${generatedError}  Unable to ${operation} type ${attrType}. " +
      "Please override the method ${methodName} using the TOP mechanism!");
  ${returnStatement}