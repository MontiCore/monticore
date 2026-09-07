${tc.signature("n")}
  if (!(object instanceof Tuple${n})) {
    return false;
  }
  Tuple${n}<<#list 0..n-1 as i>?<#if i!=(n-1)>,</#if></#list>> tuple =
    (Tuple${n}<<#list 0..n-1 as i>?<#if i!=(n-1)>,</#if></#list>>) object;
<#list 0..n-1 as i>
  if (!Objects.equals(this.get${i}(), tuple.get${i}())) {
    return false;
  }
</#list>
  return true;