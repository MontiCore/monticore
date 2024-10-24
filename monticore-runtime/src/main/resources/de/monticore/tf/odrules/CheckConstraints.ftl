<#-- (c) https://github.com/MontiCore/monticore -->
private boolean checkConstraints() {

  boolean result = true;
<#if subConstraints??>
  <#list subConstraints as subConstraint>
    <#if subConstraint.isOptionalInOrPresent()>
  result = result && isSubConstraintValid_${subConstraint?index}(
      <#list subConstraint.dependVars as dependency>
        <#if dependency?index gt 0>,</#if>
        ${dependency.getObjectName()}_cand
      </#list>);
    </#if>
  </#list>
</#if>

  return result;
}
