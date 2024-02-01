<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "originalScopeType", "errorCode")}
  if (enclosingScope == null || enclosingScope instanceof ${originalScopeType}){
    return ${methodName}((${originalScopeType}) enclosingScope, scopeJson);
  } else {
    de.se_rwth.commons.logging.Log.error("0xA7002${errorCode} The EnclosingScope could not be casted to the type ${originalScopeType}.");
    return ${methodName}((${originalScopeType}) null, scopeJson);
  }