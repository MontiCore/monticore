${tc.signature("attributeName", "methodName", "paramCall", "returnType")}
<#if returnType != "void">return </#if>this.${attributeName}.${methodName}(${paramCall});