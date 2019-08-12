${tc.signature("attributeName", "methodName", "paramCall", "returnType")}
    <#if returnType != "void">return </#if>this.get${attributeName?cap_first}List().${methodName}(${paramCall});
