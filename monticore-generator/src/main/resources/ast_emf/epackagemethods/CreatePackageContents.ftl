<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("method", "ast", "grammarName", "astClasses", "emfAttributes", "externalTypes")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
/**
 * Creates the meta-model objects for the package.  This method is
 * guarded to have no affect on any invocation but its first.
*/
    if (isCreated) {
      return;
    }
    isCreated = true;
    
    // Create classes and their features
    constants${grammarName}EEnum = createEEnum(Constants${grammarName});
  
  <#list astClasses as astClass>
    <#assign name = astHelper.getPlainName(astClass)>
    ${name[3..]?uncap_first}EClass = createEClass(${name});
  </#list>  
  
  <#list emfAttributes as emfAttribute>
    create${emfAttribute.getEmfType()}(${astHelper.getPlainName(emfAttribute.getCdType())[3..]?uncap_first}EClass, ${emfAttribute.getFullName()});
  </#list> 
  
  <#list externalTypes as externalType>
    ${externalType?uncap_first}EDataType = createEDataType(${externalType});
  </#list>   
