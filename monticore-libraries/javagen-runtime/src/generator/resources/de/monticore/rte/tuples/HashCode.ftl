${tc.signature("n")}
    int result = 1;
<#-- add calculation for each index -->
<#list 0..n-1 as i>
    result = 31 * result
    + (this.get${i}() != null
    ? this.get${i}().hashCode()
    : 0);
</#list>
    return result;