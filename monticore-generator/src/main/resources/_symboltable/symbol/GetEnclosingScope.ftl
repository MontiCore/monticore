<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName", "errorCode")}
if (enclosingScope instanceof ${scopeName}) {
  return (${scopeName}) enclosingScope;
} else {
  de.se_rwth.commons.logging.Log.error("0xA7006${errorCode} The EnclosingScope could not be casted to the type ${scopeName}.");
}
// Normally this statement is not reachable
throw new IllegalStateException();