${tc.signature("n")}
    apply(
<#if n &gt; 0>
<#list 0..n-1 as i>
      arg${i}<#if i != (n-1)>, </#if>
</#list>
</#if>
    );