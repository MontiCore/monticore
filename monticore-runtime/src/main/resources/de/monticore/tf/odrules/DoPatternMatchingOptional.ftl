<#-- (c) https://github.com/MontiCore/monticore -->
<#-- We are only interested in optional structures here -->
<#list hierarchyHelper.getOptionalMatchObjects(ast.getPattern().getLHSObjectsList()) as structure>

protected boolean doPatternMatching_${structure.getObjectName()}(boolean isParentBacktrackingNegative) {
  // indicates whether this rule is currently backtracking
  // (this will skip all attempts to match lists or negative nodes)
  boolean isBacktracking = false;
  boolean isBacktrackingNegative = isParentBacktrackingNegative;

  Stack<String> backtracking = new Stack<String>();
  Stack<String> backtrackingNegative = new Stack<String>();
  Stack<String> searchPlan = (Stack<String>) searchPlan_${structure.getObjectName()}.clone();

 <#list structure.getInnerLinkObjectNamesList() as elem>
   <#if hierarchyHelper.isNoOptionalName(ast.getPattern().getLHSObjectsList(), elem)>
     ${elem}_cand = null;
   </#if>
 </#list>

  String nextNode = null;
  while(!searchPlan.isEmpty()){
    nextNode = searchPlan.pop();
    <#--creates an if statement for each object for matching the object-->

    <#-- <#list ast.getPattern().getLHSObjectsList() as object> -->
    <#list hierarchyHelper.getInnerLinkObjectsLHS(ast.getPattern().getLHSObjectsList(), structure) as object>
      <#if object.isListObject()>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleListObject", object, [true])}
      <#elseif object.isOptObject()>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleOptObject", object, [true])}
      <#elseif object.isNotObject()>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNotObject", object, [true])}
      <#else>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNormalObject", object, [true])}
      </#if>
      <#if object_has_next>else</#if>
    </#list>
  }

  return true;
}
</#list>
