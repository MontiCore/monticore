<#-- (c) https://github.com/MontiCore/monticore -->
<#-- for each object a checkConditions method is created -->
<#list hierarchyHelper.getMandatoryMatchObjects(ast.getPattern().getLHSObjectsList()) as object>
  <#if !object.isListObject()>
    private boolean checkConditions_${object.getObjectName()}(${object.getType()} cand){
      // if there are dependency objects and they are not null and the condition does not hold return false
    <#list ast.getPattern().getObjectConditionsList() as condition>
    <#-- test if the condition concerns the object the method is created for-->
      <#if condition.getObjectName() = object.getObjectName()>
      <#--create null-checks for all dependencies and the condition-check-->
      if(<#if condition.isPresentDependency()>${condition.getDependency().getContent()}_cand != null && </#if>
        ${condition.conditionString}){
        return false;
      }
      </#if>
    </#list>

    <#if subConstraints??>
      <#list subConstraints as subConstraint>
        <#if subConstraint.isDependendOn(object)>
          <#if !subConstraint.isOptionalInOrPresent()>
      if(!isSubConstraintValid_${subConstraint?index}(
            <#list subConstraint.dependVars as dependency>
              <#if dependency?index gt 0>,</#if>
              <#if dependency.getObjectName() != object.getObjectName()>${dependency.getObjectName()}_cand
              <#else>cand
              </#if>
            </#list>)) {
        return false;
      }
          </#if>
        </#if>
      </#list>
    </#if>
      return true;
    }
  </#if>
</#list>
