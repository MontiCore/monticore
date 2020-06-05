<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("errorCode", "inheritedScopeType", "originalScopeType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  if (enclosingScope instanceof ${originalScopeType}){
    this.spannedScope = (${originalScopeType}) spannedScope;
  }else {
    de.se_rwth.commons.logging.Log.error("0xA7006${errorCode}The SpannedScope form type ${inheritedScopeType} could not be casted to the type ${originalScopeType}. Please call the Method setSpannedScope with a parameter form type ${originalScopeType}");
  }