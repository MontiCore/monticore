<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    super.deepClone(result);

<#list astType.getCDAttributeList() as attribute>
<#if !genHelper.isModifierPrivate(attribute)>
  <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#assign attrType = attribute.getType()>
  <#assign typeName = genHelper.printType(attribute.getType())>
  <#if genHelper.isAstNode(attribute)>
    result.set${methName}(get${methName}().deepClone());
  <#elseif genHelper.isPrimitive(attrType)>
    result.set${methName}(${genHelper.getPlainGetter(attribute)}());
  <#elseif genHelper.isOptional(attribute)>
    <#assign reference = genHelper.getSimpleReferenceTypeFromOptional(attrType)>
    <#assign referenceName = genHelper.getQualifiedReferenceNameFromOptional(attrType)>
    <#if genHelper.isString(reference) || genHelper.isAdditionalAttribute(attribute) || genHelper.isAttributeOfTypeEnum(attribute)>
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
    get${methName?remove_ending("s")}List().forEach(s -> result.${attributeName}.add(s.deepClone()));
    <#elseif genHelper.isListType(typeName)>
    for ( int i = 0; i < this.get${methName?remove_ending("s")}List().size() ; i++ ) {
        result.add${methName?remove_ending("s")}(get${methName?remove_ending("s")}(i));
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
