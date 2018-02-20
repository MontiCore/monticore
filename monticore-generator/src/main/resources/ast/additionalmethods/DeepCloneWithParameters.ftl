<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("ast","astType")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    super.deepClone(result);
    
    <#list astType.getCDAttributeList() as attribute> 
      <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
      <#assign attrType = attribute.getType()>
      <#assign typeName = genHelper.printType(attribute.getType())>
      <#if genHelper.isAstNode(attribute)>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName}.deepClone());
      <#elseif genHelper.isPrimitive(attribute.getType())> 
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName});
      <#elseif genHelper.isOptional(attribute)>
        <#assign reference = genHelper.getSimpleReferenceTypeFromOptional(attrType)>
        <#assign referenceName = genHelper.getQualifiedReferenceNameFromOptional(attrType)>
        <#if genHelper.isString(reference) || genHelper.isAdditionalAttribute(attribute) || genHelper.isAttributeOfTypeEnum(attribute)>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}Opt(this.${attributeName});
        <#elseif genHelper.isOptionalAstNode(attribute)>
    if (isPresent${genHelper.getNativeAttributeName(attribute.getName())?cap_first}()){
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName}.get().deepClone());
    } else {
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}Absent();
    }
        <#else>
    if (isPresent${genHelper.getNativeAttributeName(attribute.getName())?cap_first}()){
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName}.get().clone());
    } else {
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}Absent();
    }
    </#if>
      <#else>
        <#if genHelper.isListAstNode(attribute)>
    for ( int i = 0; i < result.get${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}List().size() ; i++ ) {
      result.set${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}( i, this.${attributeName}.get(i));
    }
    this.${attributeName}.forEach(s -> result.${attributeName}.add(s.deepClone()));
        <#elseif genHelper.isListType(typeName)>
    for ( int i = 0; i < this.get${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}List().size() ; i++ ) {
        result.add${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}(this.get${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}(i));
    }
        <#elseif genHelper.isString(typeName) || genHelper.isAttributeOfTypeEnum(attribute)>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName});
        <#else>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName});
        </#if>
      </#if>    
    </#list>
    
    return result;
