<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "count")}
package benchmark.generated;

public class ${className} {
<#list 1..count as i>
  <@compress single_line=true>
    public
    <#if i % 2 == 0>static</#if>
    String
    compute${i}
    (int arg${i}, String label${i})
    <#if i % 5 == 0> throws IllegalArgumentException</#if>
  </@compress> {
    return label${i} + arg${i};
  }
</#list>
}
