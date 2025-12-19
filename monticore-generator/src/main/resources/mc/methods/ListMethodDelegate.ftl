<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "methodName", "paramCall", "returnType","attributeType","parameterType","parameterAttribute","errorCode")}
<#if paramCall?has_content && parameterType == "Collection" || parameterType == "Array">
<#if parameterType == "Collection">
<#-- when the argument is a Collection we only need to check every argument -->
boolean allElementsValid = true;
if(${parameterAttribute} != null) {
  for(Object elementOf${attributeName}: ${parameterAttribute}) {
    if(!(elementOf${attributeName} instanceof ${attributeType})){
      allElementsValid = false;
      break;
    }
  }
}

if(allElementsValid) {
  <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
}else{
  Log.error("${errorCode} a unexpected type was set in the a method created with the ListMethodDelegate.ftl");
  <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
}
<#-- when the argument is not a list we only need to check the argument -->
<#elseif parameterType == "Array">
boolean allElementsValid = true;
if(${parameterAttribute} != null) {
  for(Object elementOf${attributeName} : ${parameterAttribute}) {
    if(!(elementOf${attributeName} instanceof ${attributeType})){
      allElementsValid = false;
      break;
    }
  }
}

if(allElementsValid) {
  <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
}else{
  Log.error("${errorCode} a unexpected type was set in the method created with the ArrayMethodDelegate.ftl");
  <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
}

<#else>
if(${parameterAttribute} instanceof ${attributeType}){
   <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
}else{
   Log.error("${errorCode} a unexpected type was set in the a method created with the ListMethodDelegate.ftl");
   <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
}
</#if>

<#else>
   <#if returnType != "void">return </#if>((List<${attributeType}>)this.get${attributeName?cap_first}List()).${methodName}(${paramCall});
</#if>

