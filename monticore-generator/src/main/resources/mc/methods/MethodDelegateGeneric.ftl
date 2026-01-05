<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "methodName", "paramCall", "returnType","attributeType","methodSignature","errorCode")}

<#-- addAll, addAll_ methods -->
<#if methodSignature?has_content && methodSignature == "addAll" && methodSignature == "addAll_">
List<${attributeType}> castedList = new ArrayList<>();
<#-- we use the name collection here as in the addAll_method the paramCall also contains the int index -->
if(collection != null) {
  for(Object elementOf${attributeName}: collection) {
    if(!(elementOf${attributeName}.getClass() == ${attributeType}.class)){
      Log.error("${errorCode} a unexpected type was set in the method created ");
    }
    ${attributeType} castedElementOf${attributeName} = (${attributeType}) elementOf${attributeName};
    castedList.add(castedElementOf${attributeName});
  }
}
Log.error("${errorCode} a unexpected type was set in the method addAll${attributeName?cap_first} created with GenericMethodDelegate.ftl");
return this.get${attributeName?cap_first}().${methodName}(${paramCall});

<#-- add -->
<#elseif methodSignature == "add">
if(element != null && !(element.getClass() == ${attributeType}.class)){
  Log.error("${errorCode} a unexpected type was set in t");
}
${attributeType} castedElement = (${attributeType}) element;
return ((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(castedElement);

<#-- iterator_generic -->
<#elseif methodSignature == "iterator_generic">
   return ((List<? extends ${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}();

<#-- get -->
<#elseif methodSignature == "get">
${attributeType} elementOf${attributeName} = (${attributeType})this.get${attributeName?cap_first}List().${methodName}(${paramCall});
if(!(elementOf${attributeName}.getClass() == ${attributeType}.class)){
  Log.error("${errorCode} a unexpected type was set in the method get${attributeName?cap_first} created with GenericMethodDelegate.ftl");
}
return elementOf${attributeName};

<#-- everything else -->
<#else>
   <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
</#if>

