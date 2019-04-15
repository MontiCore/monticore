<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astcdClass")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    super.deepClone(result);

<#list astcdClass.getCDAttributeList() as attribute>
<#if attribute.isPresentModifier() && !attribute.getModifier().isPrivate()>
  <#assign attributeName = attribute.getName()>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#assign attrType = attribute.getType()>
  <#assign typeName = genHelper.printType(attribute.getType())>
  <#if genHelper.isAstNode(attribute)>
    result.set${methName}(get${methName}().deepClone());
  <#elseif genHelper.isPrimitive(attrType)>
    result.set${methName}(${genHelper.getPlainGetter(attribute)}());
  <#elseif genHelper.isOptional(attribute.getType())>
    <#assign reference = genHelper.getSimpleReferenceTypeFromOptional(attrType)>
    <#assign referenceName = genHelper.getQualifiedReferenceNameFromOptional(attrType)>
    <#if genHelper.isString(reference) || genHelper.isAttributeOfTypeEnum(attribute)>
    result.set${methName}Opt(get${methName}Opt());
    <#elseif genHelper.isOptionalAstNode(attribute)>
    if (isPresent${methName}()){
      result.set${methName}(get${methName}().deepClone());
    } else {
      result.set${methName}Absent();
    }
    <#else>
    if (isPresent${methName}()){
      result.set${methName}(get${methName}());
    } else {
      result.set${methName}Absent();
    }
    </#if>
  <#else>
    <#if genHelper.isListAstNode(attribute)>
    get${methName}List().forEach(s -> result.${attributeName}.add(s.deepClone()));
    <#elseif genHelper.isListType(typeName)>
    for ( int i = 0; i < this.get${methName}List().size() ; i++ ) {
        result.add${methName}(get${methName(i)};
    }
    <#elseif genHelper.isString(typeName) || genHelper.isAttributeOfTypeEnum(attribute)>
    result.set${methName}(get${methName}());
    <#else>
    result.set${methName}(get${methName}());
    </#if>
  </#if>
</#if>
</#list>
    
    return result;
