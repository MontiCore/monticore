<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("errorCode", "inheritedScopeType", "originalScopeType")}
  if (enclosingScope instanceof ${originalScopeType}){
    this.enclosingScope = (${originalScopeType}) enclosingScope;
  }else {
    de.se_rwth.commons.logging.Log.error("0xA7005${errorCode} The EnclosingScope form type ${inheritedScopeType} could not be casted to the type ${originalScopeType}. Please call the Method setEnclosingScope with a parameter form type ${originalScopeType}");
  }