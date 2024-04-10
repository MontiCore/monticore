<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalScopeType", "errorCode")}
  if (enclosingScope == null || enclosingScope instanceof ${originalScopeType}) {
    return deserialize((${originalScopeType}) enclosingScope, symbolJson);
  } else {
    de.se_rwth.commons.logging.Log.error("0xA7001${errorCode} The EnclosingScope form type could not be casted to the type ${originalScopeType}");
    return null;
  }