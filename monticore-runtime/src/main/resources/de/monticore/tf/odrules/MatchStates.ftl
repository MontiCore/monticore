<#-- (c) https://github.com/MontiCore/monticore -->
<#assign mandatoryObjects = hierarchyHelper.getMandatoryMatchObjects(ast.getPattern().getLHSObjectsList())>
<#assign listObjects = hierarchyHelper.getListObjects(ast.getPattern().getLHSObjectsList())>
<#macro commaSeperatedNames object>
  <#list hierarchyHelper.getMandatoryObjectsWithoutOptAndListChilds(ast.getPattern().getLHSObjectsList()) as o>
    <#if object.getObjectName() = o.getObjectName()>cand
    <#else>${o.getObjectName()}_cand
    </#if>
    <#if o_has_next>, </#if>
  </#list>
</#macro>
<#macro commaSeperatedObjects object>
  <#list hierarchyHelper.getMandatoryObjectsWithoutOptAndListChilds(ast.getPattern().getLHSObjectsList()) as o>
    <#if !o.isNotObject()>
      <#if !(object.getObjectName() = o.getObjectName())>
        <#if !hierarchyHelper.isWithinOptionalStructure(o.getObjectName()) && !o.isListObject()>
          ${o.getObjectName()}_cand == null ||
        </#if>
      </#if>
    </#if>
  </#list>
</#macro>
<#assign listChildObjects = hierarchyHelper.getListChilds(ast.getPattern().getLHSObjectsList())>
<#macro commaSeperatedListObjects object>
  <#list listChildObjects as o>
    <#if !o.isNotObject()><#if !(object.getObjectName() = o.getObjectName())>
      <#if !hierarchyHelper.isWithinOptionalStructure(o.getObjectName()) && !o.isListObject()>
        ${o.getObjectName()}_cand == null ||
      </#if>
    </#if>
    </#if>
  </#list>
</#macro>
<#list mandatoryObjects as object>
<#--creates a match method for each object-->
  <#if !object.isListObject() >
    private ${object.getType()} match_${object.getObjectName()}(){
        //test if there are candidates for the object
        while(!${object.getObjectName()}_candidates_temp.isEmpty()){
          if(${object.getObjectName()}_candidates_temp.get(0) instanceof ${object.getType()}) {
    ${object.getType()} cand = (${object.getType()})${object.getObjectName()}_candidates_temp.get(0);

            //test if candidate matches the conditions for this object
            if(checkConditions_${object.getObjectName()}(cand)
    <#list mandatoryObjects as o>
      <#if object.getObjectName() != o.getObjectName() && !ast.getFoldingHash()[object.getObjectName()]?seq_contains(o.getObjectName())>
        <#if !o.isListObject() && !o.isNotObject() && !hierarchyHelper.isListChild(o)>
                        && cand != (ASTNode) ${o.getObjectName()}_cand
        </#if>
      </#if>
    </#list>
    <#list listObjects as list>
                    && (${list.getObjectName()}_candidates == null || !isMatchedBy${list.getObjectName()}(cand))
    </#list>){
            	if (<@commaSeperatedObjects object/><@commaSeperatedListObjects object/>true) {
    ${object.getObjectName()}_candidates_temp.remove(0);
                    return cand;
                }
            }
          }
    ${object.getObjectName()}_candidates_temp.remove(0);
        }
        return null;
    }
  </#if >
</#list>
