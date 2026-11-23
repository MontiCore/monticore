<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "count")}
package benchmark.generated;

public class ${className} {
<#list 1..count as i>
  private String field${i} = "value${i}";

  public String getField${i}() {
    return this.field${i};
  }

  public void setField${i}(String field${i}) {
    this.field${i} = field${i};
  }
</#list>
}
