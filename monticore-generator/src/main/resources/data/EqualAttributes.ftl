<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astcdClass", "simpleClassName")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
   <#if genHelper.hasOnlyAstAttributes(astcdClass)>
    return o instanceof ${simpleClassName};
   <#else>
      ${simpleClassName} comp;
    if ((o instanceof ${simpleClassName})) {
      comp = (${simpleClassName}) o;
    } else {
      return false;
    }
       <#list astcdClass.getCDAttributeList()  as attribute>
         <#assign attributeName = attribute.getName()>
         <#if !genHelper.isSimpleAstNode(attribute) && !genHelper.isOptionalAstNode(attribute) && !genHelper.isListAstNode(attribute)>
	// comparing ${attributeName} 
	      <#if genHelper.isPrimitive(attribute.getMCType())>
    if (!(this.${attributeName} == comp.${attributeName})) {
      return false;
    }
         <#elseif genHelper.isOptional(attribute.getMCType())>
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

