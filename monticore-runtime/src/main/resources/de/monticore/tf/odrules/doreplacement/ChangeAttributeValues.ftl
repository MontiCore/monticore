<#-- (c) https://github.com/MontiCore/monticore -->
<#assign mandatoryChanges = hierarchyHelper.getChangeObjectsWhithoutCreate(ast.getReplacement())>
// apply changes
int pos;
<#list mandatoryChanges as change>

  <#if change.isObjectWithinOpt()>if(m.${change.getObjectName()}.isPresent()) {</#if>


  <#if change.isPrimitiveType() || change.isValueStringList() >
    ${tc.includeArgs("de.monticore.tf.odrules.doreplacement.ChangeAttributeValueSinglePrimitive", change, [ast.getClassname()])}
  <#elseif change.composite && change.isObjectWithinList() >
    ${tc.include("de.monticore.tf.odrules.doreplacement.ChangeAttributeValueList", change)}
  <#elseif change.composite && !change.isValueListObject() && !change.isOldValueWithinList() && !change.isValueWithinList()>
    ${tc.includeArgs("de.monticore.tf.odrules.doreplacement.ChangeAttributeValueSingleCompositeNotInList", change, [ast.getClassname()])}
  <#elseif change.composite>
    ${tc.includeArgs("de.monticore.tf.odrules.doreplacement.ChangeAttributeValueSingleCompositeInList", change, [ast.getClassname()])}
  </#if>


  <#if change.isPresentValue() && !change.isObjectWithinList()>
    <#if change.isValueWithinOpt()>if(m.${change.getValue()}.isPresent()) {
  //TODO find a way for lists
      <#if !change.isObjectWithinList()>
  Reporting.reportTransformationNewValue("${ast.getClassname()}",<#if change.composite>m.</#if>${change.getValue()}<#if change.isValueWithinOpt()>.get()</#if><#if change.isValueStringList()>.toString()</#if>);
      </#if>
  }</#if>
  </#if>


  <#if change.isObjectWithinOpt()>
  } else {
  // no new objects should be created
    <#if !change.isPrimitiveType() && !change.isValueStringList() && !change.isOldValueWithinList() && !change.isValueWithinList() >
      <#if change.isValueWithinOpt()>m.${change.getValue()} = Optional.empty(); </#if>
      <#if change.isOldValueWithinOpt()>m.${change.getOldValue()} = Optional.empty(); </#if>
    </#if>
  }
  </#if>

</#list>



