<#-- (c) https://github.com/MontiCore/monticore -->
<#assign mandatoryChanges = hierarchyHelper.getChangeObjectsWhithoutCreate(ast.getReplacement())>
// apply changes
int pos;
<#list mandatoryChanges as change>
  <#if change.isObjectWithinOpt()>if(m.${change.getObjectName()}.isPresent()) {</#if>

  <#if change.isPrimitiveType() || change.isValueStringList() >
    ${tc.includeArgs("de.monticore.tf.odrules.undoreplacement.ChangeAttributeValueSinglePrimitive", change, [ast.getClassname()])}
  <#elseif change.composite && change.isObjectWithinList()>
    ${tc.include("de.monticore.tf.odrules.undoreplacement.ChangeAttributeValueList", change)}
  <#elseif change.composite && !change.isOldValueWithinList() && !change.valueWithinList>
    ${tc.includeArgs("de.monticore.tf.odrules.undoreplacement.ChangeAttributeValueSingleCompositeNotInList", change, [ast.getClassname()])}
  <#elseif change.composite>
    ${tc.includeArgs("de.monticore.tf.odrules.undoreplacement.ChangeAttributeValueSingleCompositeInList", change, [ast.getClassname()])}
  </#if>

  <#if change.isObjectWithinOpt()>}</#if>
</#list>



