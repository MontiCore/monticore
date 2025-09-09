<#-- (c) https://github.com/MontiCore/monticore -->
// get all required LHS values
<#list ast.getReplacement().getRequirementsList() as requirement>
  ${requirement.type} _${requirement.getObject()}_${requirement.attribute} = m.${requirement.getObject()}.${requirement.getGetter()}();
</#list>

<#list ast.getReplacement().getCreateObjectsList() as create>
<#assign isWithinOpt = hierarchyHelper.isWithinOptionalStructure(create.getName())>
<#assign isWithinList = hierarchyHelper.isWithinListStructure(create.getName())>



<#if !isWithinList>
if (!is_${create.getName()}_fix) {
  <#if create.getFactoryName() != "__missing">
    ${create.getType()}Builder builder = ${create.getFactoryName()}.${create.getSimpleType()?keep_after("AST")?uncap_first}Builder();
    <#list ast.getReplacement().getChangesList() as change>
      <#if change.getObjectName() == create.getName()>
        <#if change.isPresentValue()>
          <#if change.isPrimitiveType()>
            <#assign changeGetValue = change.getValue()>
          <#elseif change.isValueStringList()>
            <#assign changeGetValue = change.getValue()>
          <#else>
            <#assign changeGetValue = "m.${change.getValue()}">
            <#if hierarchyHelper.isWithinOptionalStructure(change.getObjectName()) || change.valueWithinOpt>
              if(${changeGetValue}.isPresent())
              <#assign changeGetValue += ".get()">
            </#if>
          </#if>
    builder.${change.getSetter()}(${changeGetValue});
        <#else>
    builder.${change.getSetter()}();
        </#if>
      </#if>
    </#list>
    m.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>builder.build()<#if isWithinOpt>)</#if>;
  <#list ast.getReplacement().getChangesList() as change>
    <#if change.getObjectName() == create.getName()>
    Reporting.reportTransformationObjectChange("${ast.getClassname()}",m.${create.getName()}<#if isWithinOpt>.get()</#if>, "${change.getAttributeName()}");
    </#if>
  </#list>
<#else>
  // TODO: There exists no builder for ${create.getType()}s - check if this is set from external
</#if>
} else {
  m.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>(${create.getType()}) ${create.getName()}_candidates.get(0)<#if isWithinOpt>)</#if>;
}
Reporting.reportTransformationObjectCreation("${ast.getClassname()}",m.${create.getName()}<#if isWithinOpt>.get()</#if>);
<#else>
<#assign listParent = hierarchyHelper.getListParent(create.getName())>
if (!is_${create.getName()}_fix) {
  for (Match${listParent} list : get_${listParent}()) {
    list.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>${create.getFactoryName()}.create${create.getSimpleType()}()<#if isWithinOpt>)</#if>;
  }
} else {
  for (Match${listParent} list : get_${listParent}()) {
    list.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>(${create.getType()}) ${create.getName()}_candidates.get(get_${listParent}().indexOf(list))<#if isWithinOpt>)</#if>;
  }
}
//TODO find a way for list objects Reporting.reportTransformationObjectCreation("${ast.getClassname()}",get_${create.getName()}()<#if isWithinOpt>.get()</#if>);
</#if>
</#list>
