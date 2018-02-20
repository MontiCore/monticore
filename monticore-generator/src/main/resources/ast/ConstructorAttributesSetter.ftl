<#-- (c) https://github.com/MontiCore/monticore -->
 <#assign genHelper = glex.getGlobalVar("astHelper")>
  {
  <#-- TODO: MB Use method getPlainSetter -->
  <#list ast.getCDParameterList() as attribute>
    <#if genHelper.isOptional(attribute.getType())>
	  set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}Opt(${attribute.getName()});
    <#elseif genHelper.isListType(astHelper.printType(attribute.getType()))>
      <#assign name=genHelper.getNativeAttributeName(attribute.getName())?cap_first>     
      set${name?keep_before_last("s")}List(${attribute.getName()});
    <#else>
      set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(${attribute.getName()});
    </#if>
  </#list>
  }
