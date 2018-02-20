<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast","astType")}
   <#assign genHelper = glex.getGlobalVar("astHelper")>
   <#assign astName = genHelper.getPlainName(astType)>
   <#if genHelper.hasOnlyAstAttributes(astType)>
    return o instanceof ${astName};
   <#else>
      ${astName} comp;
    if ((o instanceof ${astName})) {
      comp = (${astName}) o;
    } else {
      return false;
    }
      <#-- TODO: attributes of super class - use symbol table -->
       <#list astType.getCDAttributeList()  as attribute>  
         <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
         <#if !genHelper.isAstNode(attribute) && !genHelper.isOptionalAstNode(attribute) && !genHelper.isListAstNode(attribute)>
	// comparing ${attributeName} 
	      <#if genHelper.isPrimitive(attribute.getType())>
    if (!(this.${attributeName} == comp.${attributeName})) {
      return false;
    }
         <#elseif genHelper.isOptional(attribute)>
    if ( this.${attributeName}.isPresent() != comp.${attributeName}.isPresent() ||
       (this.${attributeName}.isPresent() && !this.${attributeName}.get().equals(comp.${attributeName}.get())) ) {
      return false;
    }
	      <#else>
    if ( (this.${attributeName} == null && comp.${attributeName} != null) 
      || (this.${attributeName} != null && !this.${attributeName}.equals(comp.${attributeName})) ) {
      return false;
    }
	      </#if>
	    </#if>  
      </#list>      
    return true;     
    </#if> 

