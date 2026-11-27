<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "count")}
package benchmark.generated;

/**
 * Generated benchmark class ${className}.
 */
public class ${className} {
<#list 1..count as i>
${tc.includeArgs("templateEnginePerformance.IncludedMember", ["String", "member" + i])}

  /**
   * Computes result for item ${i}.
   */
  <@compress single_line=true>
    public
    <#if i % 3 == 0>final</#if>
    String
    process${i}
    (String input${i})
    <#if i % 4 == 0> throws IllegalStateException</#if>
  </@compress> {
    if (input${i} == null) {
      return this.member${i};
    }
    return input${i} + "_" + this.member${i};
  }
</#list>
}
