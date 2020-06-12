<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("varName", "scopeName", "errorCode")}
if (${varName} instanceof ${scopeName}) {
  return (${scopeName}) ${varName};
} else {
  de.se_rwth.commons.logging.Log.error("0xA7006${errorCode} ${varName} could not be casted to the type ${scopeName}.");
}
// Normally this statement is not reachable
throw new IllegalStateException();