${tc.signature("n")}
    StringBuilder res = new StringBuilder();
    res.append("(");
<#list 0..n-1 as i>
    res.append(get${i}().toString());
    <#if i != (n-1)>res.append(", ");</#if>
</#list>
    res.append(")");
    return res.toString();