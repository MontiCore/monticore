<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "count")}
package benchmark.generated;

public class ${className} {
<#list 1..count as i>
${tc.includeArgs("templateEnginePerformance.IncludedMember", ["String", "field" + i])}
</#list>
}
