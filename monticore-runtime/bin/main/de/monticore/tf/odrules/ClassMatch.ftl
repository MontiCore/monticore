<#-- (c) https://github.com/MontiCore/monticore -->
<#assign mandatoryObjects = hierarchyHelper.getMandatoryObjectsWithoutOptAndListChilds(ast.getPattern().getLHSObjectsList())>
<#assign matchingObjects = hierarchyHelper.getMandatoryObjectsWithoutOptAndListChilds(ast.getPattern().getMatchingObjectsList())>
<#assign matchingListObjects = hierarchyHelper.getListObjects(ast.getPattern().getLHSObjectsList())>

public class Match {
  private Match(
<#list mandatoryObjects as object>
  <#if !object.isListObject()> ${object.getType()}
  <#else>${object.getListtype()}
  </#if>
  ${object.getObjectName()}
  <#if object_has_next>,</#if>
</#list>) {
<#list mandatoryObjects as object>
  <#if hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
      this.${object.getObjectName()} = Optional.ofNullable(${object.getObjectName()});
  <#else>
      this.${object.getObjectName()} = ${object.getObjectName()};
  </#if>
</#list>}

<#list matchingObjects as object>
  <#assign isWithinOpt = hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
      private <#if isWithinOpt>Optional<</#if>
  <#if !object.isListObject()> ${object.getType()}
  <#else>${object.getListtype()} </#if>
  <#if isWithinOpt>></#if>${object.getObjectName()};
</#list>

// before variables for undo operations
<#list ast.getReplacement().getChangesList() as change>
  <#if change.isPresentValue()><#assign valueName = change.getValue()><#else><#assign valueName = change.getOldValue()></#if>
  <#if change.isPrimitiveType() || change.isValueStringList()>
      <#if !change.isObjectWithinList()>
        private ${change.getType()} ${change.getObjectName()}_${change.getAttributeName()}_before;
      <#else>
        private Map<${change.getObjectType()}, ${change.getType()?cap_first}>  ${change.getObjectName()}_${change.getAttributeName()}_before = new HashMap();
      </#if>
  <#elseif change.isObjectWithinList() >
      <#if change.isAttributeIterated()>
        private int ${change.getObjectName()}_${valueName}_before_pos;
        private Map<${change.getGenericType()}, Integer> ${change.getObjectName()}_${valueName}_before = new HashMap();
      <#else>
        private Map<${change.getObjectType()}, ${change.getGenericType()}> ${change.getObjectName()}_${valueName}_before = new HashMap();
      </#if>
  <#else>
      <#if change.isAttributeIterated()>
        private int ${change.getObjectName()}_${valueName}_before_pos;
        private Map<${change.getGenericType()}, Integer> ${change.getObjectName()}_${valueName}_before = new HashMap();
      <#else>
        private ${change.getGenericType()} ${change.getObjectName()}_${valueName}_before;
      </#if>
  </#if>
</#list>
}
