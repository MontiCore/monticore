<#-- (c) https://github.com/MontiCore/monticore -->
<#assign optListObjects = hierarchyHelper.getOptListObjects(ast.getPattern().getLHSObjectsList())>

  protected void splitSearchplan() {
<#list optListObjects as object>
    searchPlan_${object.getObjectName()} = new Stack<String>();
</#list>

    // split the searchPlan
    for (int i = 0; i < searchPlan.size(); i++) {
      String obj = searchPlan.get(i);

      switch(obj) {
        <#list optListObjects as object>
        case <#list object.getInnerLinkObjectNamesList() as elem>"${elem}<#if hierarchyHelper.isListObject(elem)>_$List</#if>"<#if elem_has_next>, </#if></#list> -> {
          searchPlan.remove(i);
          i--;
          searchPlan_${object.getObjectName()}.push(obj);
        }
        </#list>
      }
    }
  }
