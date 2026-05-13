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
<#list mandatoryObjects as object>
<#--creates a match method for each object-->
  <#if !object.isListObject() >
    protected ${object.getType()} match_${object.getObjectName()}(){
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
                    ${object.getObjectName()}_candidates_temp.remove(0);
                    return cand;
            }
          }
    ${object.getObjectName()}_candidates_temp.remove(0);
        }
        return null;
    }
  </#if >
</#list>
