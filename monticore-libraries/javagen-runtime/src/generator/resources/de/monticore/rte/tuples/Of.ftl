${tc.signature("n")}
    return new Tuple${n}<>(
<#list 0..n-1 as i>
      e${i}<#if i != (n-1)>, </#if>
</#list>
    );
