<#-- (c) https://github.com/MontiCore/monticore -->
<#-- for each subConstraint a checkSubConstraint method is created -->
<#if subConstraints??>
  <#list subConstraints as subConstraint>
    <#if subConstraint.dependVars??>
      private boolean isSubConstraintValid_${subConstraint?index}(
      <#list subConstraint.dependVars as dependVar>
        <#if dependVar?index gt 0>, </#if>
        ${dependVar.getType()} ${dependVar.getObjectName()}
      </#list>
      ) {

    <#-- Create java-optionals for elements of type not and optional -->
      <#list subConstraint.dependVars as dependVar>
        <#if hierarchyHelper.isWithinOptionalStructure(dependVar.getObjectName())>
        Optional${"\l"+dependVar.getType()+"\g"} ${dependVar.getObjectName()}_candAsOptional = Optional.ofNullable(${dependVar.getObjectName()});
        <#elseif hierarchyHelper.isWithinNegativeStructure(dependVar.getObjectName())>
        Optional${"\l"+dependVar.getType()+"\g"} ${dependVar.getObjectName()}_candAsOptional = Optional.ofNullable(${dependVar.getObjectName()});
        </#if>
      </#list>

        if(<#list subConstraint.dependVars as dependVar>
    <#-- check for null only if element is not of type not or optional -->
      <#if hierarchyHelper.isWithinOptionalStructure(dependVar.getObjectName())>
      <#elseif hierarchyHelper.isWithinNegativeStructure(dependVar.getObjectName())>
      <#else>
        ${dependVar.getObjectName()} != null &&
      </#if>
    </#list>
        !(${subConstraint.getConstrExpr()})){
          return false;
        }

        return true;
      }
    </#if>
  </#list>
</#if>
